package com.manpreet.androidassignments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class ChatWindowDatabaseInstrumentalTest {

    private Context context;
    private ChatDatabaseHelper dbHelper;
    private SQLiteDatabase db;

    @Rule
    public ActivityScenarioRule<ChatWindow> activityRule =
            new ActivityScenarioRule<>(ChatWindow.class);

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dbHelper = new ChatDatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        db.delete("messages", null, null);
    }

    @After
    public void tearDown() {
        if (db != null && db.isOpen()) {
            db.delete("messages", null, null);
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    @Test
    public void testDatabaseIsCreated() {
        assertNotNull("Database should not be null", db);
        assertTrue("Database should be open", db.isOpen());
    }

    @Test
    public void testMessagesTableExists() {
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='messages'",
                null);
        assertTrue("Messages table should exist", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testMessageSavedToDatabase() {
        String testMessage = "Test DB Message";

        onView(withId(R.id.message_input))
                .perform(typeText(testMessage), closeSoftKeyboard());

        onView(withId(R.id.send_button))
                .perform(click());
        Cursor cursor = db.rawQuery(
                "SELECT * FROM messages WHERE message = ?",
                new String[]{testMessage});

        assertTrue("Message should exist in database", cursor.moveToFirst());
        String savedMessage = cursor.getString(
                cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE));
        assertEquals("Saved message should match", testMessage, savedMessage);
        cursor.close();
    }

    @Test
    public void testMultipleMessagesSavedToDatabase() {
        String[] messages = {"Message 1", "Message 2", "Message 3"};

        for (String message : messages) {
            onView(withId(R.id.message_input))
                    .perform(ViewActions.clearText())
                    .perform(typeText(message), closeSoftKeyboard());

            onView(withId(R.id.send_button))
                    .perform(click());
        }
        Cursor cursor = db.rawQuery("SELECT * FROM messages", null);
        assertEquals("Should have 3 messages", messages.length, cursor.getCount());

        int index = 0;
        if (cursor.moveToFirst()) {
            do {
                String savedMessage = cursor.getString(
                        cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE));
                assertEquals("Message should match", messages[index], savedMessage);
                index++;
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Test
    public void testMessagePersistenceAcrossActivityRecreation() {
        String testMessage = "Persistent Message";
        onView(withId(R.id.message_input))
                .perform(typeText(testMessage), closeSoftKeyboard());

        onView(withId(R.id.send_button))
                .perform(click());
        activityRule.getScenario().recreate();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM messages WHERE message = ?",
                new String[]{testMessage});

        assertTrue("Message should persist in database", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testDatabaseRowIdAutoIncrement() {
        String message1 = "First Message";
        String message2 = "Second Message";

        onView(withId(R.id.message_input))
                .perform(typeText(message1), closeSoftKeyboard());
        onView(withId(R.id.send_button))
                .perform(click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.message_input))
                .perform(ViewActions.clearText())
                .perform(typeText(message2), closeSoftKeyboard());
        onView(withId(R.id.send_button))
                .perform(click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SQLiteDatabase freshDb = dbHelper.getReadableDatabase();
        Cursor cursor = freshDb.rawQuery("SELECT id FROM messages ORDER BY id", null);
        assertTrue("Should have messages", cursor.moveToFirst());

        long firstId = cursor.getLong(0);
        assertTrue("Should have second message", cursor.moveToNext());
        long secondId = cursor.getLong(0);

        assertTrue("Second ID should be greater than first", secondId > firstId);
        cursor.close();
    }

    @Test
    public void testEmptyMessageNotSavedToDatabase() {
        int initialCount = getMessageCountFromDatabase();

        onView(withId(R.id.message_input))
                .perform(typeText(""), closeSoftKeyboard());

        onView(withId(R.id.send_button))
                .perform(click());

        int finalCount = getMessageCountFromDatabase();
        assertEquals("Empty message should not be saved", initialCount, finalCount);
    }

    @Test
    public void testWhitespaceOnlyMessageNotSavedToDatabase() {
        int initialCount = getMessageCountFromDatabase();

        onView(withId(R.id.message_input))
                .perform(typeText("   "), closeSoftKeyboard());

        onView(withId(R.id.send_button))
                .perform(click());

        int finalCount = getMessageCountFromDatabase();
        assertEquals("Whitespace-only message should not be saved", initialCount, finalCount);
    }

    @Test
    public void testSpecialCharactersSavedToDatabase() {
        String specialMessage = "Test!@#$%^&*()";

        onView(withId(R.id.message_input))
                .perform(typeText(specialMessage), closeSoftKeyboard());

        onView(withId(R.id.send_button))
                .perform(click());

        Cursor cursor = db.rawQuery(
                "SELECT * FROM messages WHERE message = ?",
                new String[]{specialMessage});

        assertTrue("Special characters should be saved", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testLongMessageSavedToDatabase() {
        String longMessage = "This is a very long message that tests database storage";

        onView(withId(R.id.message_input))
                .perform(ViewActions.clearText())
                .perform(typeText(longMessage), closeSoftKeyboard());

        onView(withId(R.id.send_button))
                .perform(click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SQLiteDatabase freshDb = dbHelper.getReadableDatabase();
        Cursor cursor = freshDb.rawQuery(
                "SELECT * FROM messages WHERE message = ?",
                new String[]{longMessage});

        assertTrue("Long message should be saved", cursor.moveToFirst());
        String saved = cursor.getString(
                cursor.getColumnIndexOrThrow(ChatDatabaseHelper.KEY_MESSAGE));
        assertEquals("Long message should match", longMessage, saved);
        cursor.close();
    }

    @Test
    public void testMessagePersistsInDatabaseAfterSending() {
        String testMessage = "Persist Test";

        onView(withId(R.id.message_input))
                .perform(ViewActions.clearText())
                .perform(typeText(testMessage), closeSoftKeyboard());
        onView(withId(R.id.send_button))
                .perform(click());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SQLiteDatabase freshDb = dbHelper.getReadableDatabase();
        Cursor cursor = freshDb.rawQuery(
                "SELECT * FROM messages WHERE message = ?",
                new String[]{testMessage});
        assertTrue("Message should persist in database", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testMessagesLoadedFromDatabaseOnActivityStart() {
        db.execSQL("INSERT INTO messages (message) VALUES (?)",
                new Object[]{"Preloaded Message 1"});
        db.execSQL("INSERT INTO messages (message) VALUES (?)",
                new Object[]{"Preloaded Message 2"});

        activityRule.getScenario().close();
        ActivityScenario<ChatWindow> newScenario = ActivityScenario.launch(ChatWindow.class);
        newScenario.onActivity(activity -> {
            assertTrue("Messages should be loaded",
                    activity.chatMessages.size() >= 2);
            assertTrue("First message should be loaded",
                    activity.chatMessages.contains("Preloaded Message 1"));
            assertTrue("Second message should be loaded",
                    activity.chatMessages.contains("Preloaded Message 2"));
        });

        newScenario.close();
    }

    @Test
    public void testUIUpdatesAfterDatabaseInsert() {
        String testMessage = "UI Update Test";

        onView(withId(R.id.message_input))
                .perform(typeText(testMessage), closeSoftKeyboard());

        onView(withId(R.id.send_button))
                .perform(click());
        Cursor cursor = db.rawQuery(
                "SELECT * FROM messages WHERE message = ?",
                new String[]{testMessage});
        assertTrue("Message should be in database", cursor.moveToFirst());
        cursor.close();
        onView(withId(R.id.message_input))
                .check(matches(withText("")));
    }

    @Test
    public void testChatListCountMatchesDatabaseCount() {
        String[] messages = {"Msg1", "Msg2", "Msg3", "Msg4"};

        for (String msg : messages) {
            onView(withId(R.id.message_input))
                    .perform(ViewActions.clearText())
                    .perform(typeText(msg), closeSoftKeyboard());
            onView(withId(R.id.send_button))
                    .perform(click());
        }

        int dbCount = getMessageCountFromDatabase();

        activityRule.getScenario().onActivity(activity -> {
            assertEquals("Chat list count should match database count",
                    dbCount, activity.chatMessages.size());
        });
    }

    @Test
    public void testDatabaseIntegrityAfterMultipleOperations() {
        for (int i = 0; i < 5; i++) {
            onView(withId(R.id.message_input))
                    .perform(ViewActions.clearText())
                    .perform(typeText("Message " + i), closeSoftKeyboard());
            onView(withId(R.id.send_button))
                    .perform(click());
        }
        Cursor cursor = db.rawQuery("SELECT * FROM messages", null);
        assertEquals("Should have 5 messages", 5, cursor.getCount());
        cursor.close();
        activityRule.getScenario().recreate();
        cursor = db.rawQuery("SELECT * FROM messages", null);
        assertEquals("Messages should persist", 5, cursor.getCount());
        cursor.close();
    }

    @Test
    public void testConcurrentMessageSending() {
        String message1 = "Concurrent 1";
        String message2 = "Concurrent 2";

        onView(withId(R.id.message_input))
                .perform(typeText(message1), closeSoftKeyboard());
        onView(withId(R.id.send_button))
                .perform(click());

        onView(withId(R.id.message_input))
                .perform(ViewActions.clearText())
                .perform(typeText(message2), closeSoftKeyboard());
        onView(withId(R.id.send_button))
                .perform(click());
        Cursor cursor = db.rawQuery("SELECT * FROM messages", null);
        assertEquals("Should have 2 messages", 2, cursor.getCount());
        cursor.close();
    }

    @Test
    public void testDatabaseColumnStructure() {
        Cursor cursor = db.rawQuery("SELECT * FROM messages", null);

        int idIndex = cursor.getColumnIndex(ChatDatabaseHelper.KEY_ID);
        int messageIndex = cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE);

        assertTrue("_id column should exist", idIndex >= 0);
        assertTrue("message column should exist", messageIndex >= 0);

        cursor.close();
    }

    @Test
    public void testMessageOrderInDatabase() {
        String[] messages = {"First", "Second", "Third"};

        for (String msg : messages) {
            onView(withId(R.id.message_input))
                    .perform(ViewActions.clearText())
                    .perform(typeText(msg), closeSoftKeyboard());
            onView(withId(R.id.send_button))
                    .perform(click());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        SQLiteDatabase freshDb = dbHelper.getReadableDatabase();
        Cursor cursor = freshDb.rawQuery("SELECT message FROM messages ORDER BY id", null);

        int index = 0;
        if (cursor.moveToFirst()) {
            do {
                String savedMessage = cursor.getString(0);
                assertEquals("Message order should match", messages[index], savedMessage);
                index++;
            } while (cursor.moveToNext());
        }

        assertEquals("Should have processed all messages", messages.length, index);
        cursor.close();
    }

    @Test
    public void testActivityLifecycleWithDatabase() {
        String testMessage = "Lifecycle Test";
        onView(withId(R.id.message_input))
                .perform(typeText(testMessage), closeSoftKeyboard());
        onView(withId(R.id.send_button))
                .perform(click());
        activityRule.getScenario().moveToState(androidx.lifecycle.Lifecycle.State.CREATED);
        activityRule.getScenario().moveToState(androidx.lifecycle.Lifecycle.State.RESUMED);

        // Verify message still in database
        Cursor cursor = db.rawQuery(
                "SELECT * FROM messages WHERE message = ?",
                new String[]{testMessage});
        assertTrue("Message should persist through lifecycle", cursor.moveToFirst());
        cursor.close();
    }

    @Test
    public void testDatabaseConnectionClosed() {
        activityRule.getScenario().onActivity(activity -> {
            assertNotNull("Database should be open in activity", activity.db);
            assertTrue("Database should be open", activity.db.isOpen());
        });

        activityRule.getScenario().close();
    }

    private int getMessageCountFromDatabase() {
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM messages", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }
}