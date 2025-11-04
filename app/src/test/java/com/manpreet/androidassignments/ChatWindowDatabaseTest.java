package com.manpreet.androidassignments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class ChatWindowDatabaseTest {

    private ChatWindow activity;
    private EditText messageInput;
    private Button sendButton;
    private ListView listView;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(ChatWindow.class)
                .create()
                .start()
                .resume()
                .get();

        messageInput = activity.findViewById(R.id.message_input);
        sendButton = activity.findViewById(R.id.send_button);
        listView = activity.findViewById(R.id.chat_list);
        db = activity.db;

        db.delete("messages", null, null);
        activity.chatMessages.clear();
        activity.chatAdapter.notifyDataSetChanged();
    }

    @After
    public void tearDown() {
        if (db != null && db.isOpen()) {
            db.delete("messages", null, null);
            db.close();
        }
        activity.finish();
    }

    @Test
    public void testActivityNotNull() {
        assertNotNull("Activity should not be null", activity);
    }

    @Test
    public void testUIComponentsInitialized() {
        assertNotNull("Message input should not be null", messageInput);
        assertNotNull("Send button should not be null", sendButton);
        assertNotNull("ListView should not be null", listView);
        assertNotNull("Database should not be null", db);
    }

    @Test
    public void testChatAdapterInitialized() {
        assertNotNull("Chat adapter should not be null", activity.chatAdapter);
        assertEquals("Adapter should be set on ListView",
                activity.chatAdapter, listView.getAdapter());
    }

    @Test
    public void testSendMessageWithValidText() {
        String testMessage = "Hello, World!";
        messageInput.setText(testMessage);
        int initialCount = activity.chatMessages.size();
        sendButton.performClick();
        assertEquals("Message should be added to list",
                initialCount + 1, activity.chatMessages.size());
        assertEquals("Last message should match input",
                testMessage, activity.chatMessages.get(activity.chatMessages.size() - 1));
        assertEquals("Input field should be cleared",
                "", messageInput.getText().toString());
    }

    @Test
    public void testSendMessageWithEmptyText() {
        messageInput.setText("");
        int initialCount = activity.chatMessages.size();
        sendButton.performClick();
        assertEquals("No message should be added for empty input",
                initialCount, activity.chatMessages.size());
    }

    @Test
    public void testSendMessageWithWhitespaceOnly() {
        messageInput.setText("   ");
        int initialCount = activity.chatMessages.size();
        sendButton.performClick();
        assertEquals("No message should be added for whitespace-only input",
                initialCount, activity.chatMessages.size());
    }

    @Test
    public void testMessageSavedToDatabase() {
        String testMessage = "Test DB Message";
        messageInput.setText(testMessage);
        sendButton.performClick();
        Cursor cursor = db.rawQuery("SELECT * FROM messages WHERE message = ?",
                new String[]{testMessage});
        assertTrue("Message should exist in database", cursor.moveToFirst());
        assertEquals("Database should contain the test message",
                testMessage,
                cursor.getString(cursor.getColumnIndexOrThrow("message")));
        cursor.close();
    }

    @Test
    public void testMultipleMessagesSent() {
        String[] messages = {"Message 1", "Message 2", "Message 3"};
        for (String msg : messages) {
            messageInput.setText(msg);
            sendButton.performClick();
        }
        assertEquals("All messages should be in the list",
                messages.length, activity.chatMessages.size());
        for (int i = 0; i < messages.length; i++) {
            assertEquals("Message at position " + i + " should match",
                    messages[i], activity.chatMessages.get(i));
        }
    }

    @Test
    public void testMessagesLoadedFromDatabaseOnCreate() {
        String message1 = "Persisted Message 1";
        String message2 = "Persisted Message 2";

        db.execSQL("INSERT INTO messages (message) VALUES (?)", new Object[]{message1});
        db.execSQL("INSERT INTO messages (message) VALUES (?)", new Object[]{message2});
        ChatWindow newActivity = Robolectric.buildActivity(ChatWindow.class)
                .create()
                .start()
                .resume()
                .get();
        assertTrue("Messages should be loaded from database",
                newActivity.chatMessages.size() >= 2);
        assertTrue("First message should be loaded",
                newActivity.chatMessages.contains(message1));
        assertTrue("Second message should be loaded",
                newActivity.chatMessages.contains(message2));

        newActivity.finish();
    }

    @Test
    public void testChatAdapterGetCount() {
        activity.chatMessages.add("Message 1");
        activity.chatMessages.add("Message 2");
        activity.chatMessages.add("Message 3");
        assertEquals("Adapter count should match message list size",
                3, activity.chatAdapter.getCount());
    }

    @Test
    public void testChatAdapterGetItem() {
        String testMessage = "Test Message";
        activity.chatMessages.add(testMessage);
        assertEquals("Adapter should return correct item",
                testMessage, activity.chatAdapter.getItem(0));
    }

    @Test
    public void testChatAdapterGetViewIncoming() {
        activity.chatMessages.add("Incoming message");
        android.view.View view = activity.chatAdapter.getView(0, null, listView);
        assertNotNull("View should not be null", view);
        TextView messageText = view.findViewById(R.id.text_message);
        assertNotNull("TextView should not be null", messageText);
        assertEquals("TextView should contain message text",
                "Incoming message", messageText.getText().toString());
    }

    @Test
    public void testChatAdapterGetViewOutgoing() {
        activity.chatMessages.add("First message");
        activity.chatMessages.add("Outgoing message");
        android.view.View view = activity.chatAdapter.getView(1, null, listView);
        assertNotNull("View should not be null", view);
        TextView messageText = view.findViewById(R.id.text_message);
        assertNotNull("TextView should not be null", messageText);
        assertEquals("TextView should contain message text",
                "Outgoing message", messageText.getText().toString());
    }

    @Test
    public void testDatabaseHelperCreatesTable() {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='messages'",
                null);
        assertTrue("Messages table should exist", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testSendMessageTrimsWhitespace() {
        String messageWithSpaces = "  Hello World  ";
        String expectedMessage = "Hello World";
        messageInput.setText(messageWithSpaces);
        sendButton.performClick();
        assertEquals("Message should be trimmed",
                expectedMessage,
                activity.chatMessages.get(activity.chatMessages.size() - 1));
    }

    @Test
    public void testDatabaseClosedOnDestroy() {
        SQLiteDatabase testDb = activity.db;
        activity.onDestroy();
        assertFalse("Database should be closed after onDestroy", testDb.isOpen());
    }
}