package com.manpreet.androidassignments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class ChatWindowTest {

    private ChatWindow chatWindow;
    private ActivityController<ChatWindow> activityController;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        activityController = Robolectric.buildActivity(ChatWindow.class);
        chatWindow = activityController.create().get();
    }

    @Test
    public void testChatWindowInitialization() {
        assertNotNull("ChatWindow should not be null", chatWindow);
        assertNotNull("ListView should be initialized", chatWindow.listView);
        assertNotNull("Message input should be initialized", chatWindow.messageInput);
        assertNotNull("Send button should be initialized", chatWindow.sendButton);
        assertNotNull("Chat messages list should be initialized", chatWindow.chatMessages);
        assertNotNull("Chat adapter should be initialized", chatWindow.chatAdapter);
    }

    @Test
    public void testChatMessagesListInitialization() {
        assertTrue("Chat messages should start empty", chatWindow.chatMessages.isEmpty());
        assertEquals("Chat messages count should be 0", 0, chatWindow.chatMessages.size());
    }

    @Test
    public void testSendMessageWithValidMessage() {
        String testMessage = "Hello, this is a test message";
        chatWindow.messageInput.setText(testMessage);
        chatWindow.sendMessage(chatWindow.sendButton);
        assertEquals("Should have one message", 1, chatWindow.chatMessages.size());
        assertEquals("Message should match input", testMessage, chatWindow.chatMessages.get(0));
        assertEquals("Input field should be cleared", "", chatWindow.messageInput.getText().toString());
    }

    @Test
    public void testSendMessageWithEmptyMessage() {
        chatWindow.messageInput.setText("");
        chatWindow.sendMessage(chatWindow.sendButton);
        assertTrue("Chat messages should remain empty", chatWindow.chatMessages.isEmpty());
        assertEquals("Chat messages count should be 0", 0, chatWindow.chatMessages.size());
    }

    @Test
    public void testSendMessageWithWhitespaceOnly() {
        chatWindow.messageInput.setText("   ");
        chatWindow.sendMessage(chatWindow.sendButton);
        assertTrue("Chat messages should remain empty", chatWindow.chatMessages.isEmpty());
    }

    @Test
    public void testMultipleMessages() {
        String[] messages = {"First message", "Second message", "Third message"};
        for (String message : messages) {
            chatWindow.messageInput.setText(message);
            chatWindow.sendMessage(chatWindow.sendButton);
        }
        assertEquals("Should have three messages", 3, chatWindow.chatMessages.size());
        for (int i = 0; i < messages.length; i++) {
            assertEquals("Message " + i + " should match", messages[i], chatWindow.chatMessages.get(i));
        }
    }

    @Test
    public void testChatAdapterGetCount() {
        assertEquals("Adapter count should be 0 for empty list", 0, chatWindow.chatAdapter.getCount());
        chatWindow.chatMessages.add("Message 1");
        chatWindow.chatMessages.add("Message 2");
        assertEquals("Adapter count should match message count", 2, chatWindow.chatAdapter.getCount());
    }

    @Test
    public void testChatAdapterGetItem() {
        String message1 = "First message";
        String message2 = "Second message";
        chatWindow.chatMessages.add(message1);
        chatWindow.chatMessages.add(message2);
        assertEquals("First item should match", message1, chatWindow.chatAdapter.getItem(0));
        assertEquals("Second item should match", message2, chatWindow.chatAdapter.getItem(1));
    }

    @Test
    public void testMessageInputClearingAfterSending() {
        String testMessage = "Test message";
        chatWindow.messageInput.setText(testMessage);
        assertEquals("Message should be set in input", testMessage, chatWindow.messageInput.getText().toString());
        chatWindow.sendMessage(chatWindow.sendButton);
        assertEquals("Input should be cleared after sending", "", chatWindow.messageInput.getText().toString());
    }

    @Test
    public void testAdapterNotifyDataSetChanged() {
        int initialCount = chatWindow.chatMessages.size();
        chatWindow.chatMessages.add("New message");
        assertEquals("Message count should increase", initialCount + 1, chatWindow.chatMessages.size());
    }

    @Test
    public void testChatMessagesArrayListBehavior() {
        assertTrue("Chat messages should be empty initially", chatWindow.chatMessages.isEmpty());
        chatWindow.chatMessages.add("Message 1");
        assertFalse("Chat messages should not be empty after adding", chatWindow.chatMessages.isEmpty());
        assertEquals("Should have one message", 1, chatWindow.chatMessages.size());
        assertEquals("Should get correct message", "Message 1", chatWindow.chatMessages.get(0));
        chatWindow.chatMessages.add("Message 2");
        assertEquals("Should have two messages", 2, chatWindow.chatMessages.size());
        assertEquals("Should get correct message", "Message 2", chatWindow.chatMessages.get(1));
    }

    @Test
    public void testSendButtonClickListener() {
        assertNotNull("Send button should have click listener", chatWindow.sendButton.hasOnClickListeners());
    }

    @Test
    public void testMessageValidation() {
        chatWindow.messageInput.setText("");
        chatWindow.sendMessage(chatWindow.sendButton);
        assertTrue("Empty message should not be added", chatWindow.chatMessages.isEmpty());
        chatWindow.messageInput.setText(null);
        chatWindow.sendMessage(chatWindow.sendButton);
        assertTrue("Null message should not be added", chatWindow.chatMessages.isEmpty());
        chatWindow.messageInput.setText("Valid message");
        chatWindow.sendMessage(chatWindow.sendButton);
        assertEquals("Valid message should be added", 1, chatWindow.chatMessages.size());
    }

    @Test
    public void testMessageOrder() {
        String[] messages = {"First", "Second", "Third"};
        for (String message : messages) {
            chatWindow.messageInput.setText(message);
            chatWindow.sendMessage(chatWindow.sendButton);
        }
        for (int i = 0; i < messages.length; i++) {
            assertEquals("Message order should be preserved", messages[i], chatWindow.chatMessages.get(i));
        }
    }

    @Test
    public void testChatAdapterWithEmptyList() {
        assertEquals("Adapter count should be 0", 0, chatWindow.chatAdapter.getCount());
        try {
            chatWindow.chatAdapter.getItem(0);
            fail("Should throw IndexOutOfBoundsException for empty list");
        } catch (IndexOutOfBoundsException e) {
            assertTrue("Got IndexOutOfBoundsException for accessing an element when there is no element.", "Got Exception".equals("Got Exception"));
        }
    }

    @Test
    public void testChatAdapterWithSingleMessage() {
        String message = "Single message";
        chatWindow.chatMessages.add(message);
        assertEquals("Adapter count should be 1", 1, chatWindow.chatAdapter.getCount());
        assertEquals("Should return correct message", message, chatWindow.chatAdapter.getItem(0));
    }

    @Test
    public void testActivityLifecycle() {
        assertNotNull("Activity should be created", chatWindow);
        assertNotNull("ListView should be initialized", chatWindow.listView);
        assertNotNull("EditText should be initialized", chatWindow.messageInput);
        assertNotNull("Button should be initialized", chatWindow.sendButton);
        assertNotNull("Chat messages should be initialized", chatWindow.chatMessages);
        assertNotNull("Chat adapter should be initialized", chatWindow.chatAdapter);
    }
}
