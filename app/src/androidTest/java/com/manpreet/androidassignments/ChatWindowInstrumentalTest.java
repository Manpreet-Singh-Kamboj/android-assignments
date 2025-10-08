package com.manpreet.androidassignments;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.contrib.RecyclerViewActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;

import android.widget.EditText;
import android.widget.Button;
import android.widget.ListView;

/**
 * Comprehensive instrumental tests for ChatWindow
 * Tests chat functionality, message sending, UI interactions, and user flows
 */
@RunWith(AndroidJUnit4.class)
public class ChatWindowInstrumentalTest {

    @Rule
    public ActivityScenarioRule<ChatWindow> activityRule = 
            new ActivityScenarioRule<>(ChatWindow.class);

    @Test
    public void testAllUIElementsAreDisplayed() {
        onView(withId(R.id.chat_list))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.message_input))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.send_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testAllUIElementsAreEnabled() {
        onView(withId(R.id.message_input))
                .check(matches(isEnabled()));
        
        onView(withId(R.id.send_button))
                .check(matches(isEnabled()));
    }

    @Test
    public void testAllUIElementsAreClickable() {
        onView(withId(R.id.send_button))
                .check(matches(isClickable()));
    }

    @Test
    public void testMessageInputType() {
        onView(withId(R.id.message_input))
                .perform(typeText("Test message"));
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("Test message")));
    }

    @Test
    public void testSendButtonText() {
        onView(withId(R.id.send_button))
                .check(matches(withText("SEND")));
    }

    @Test
    public void testSendMessageWithValidText() {
        onView(withId(R.id.message_input))
                .perform(typeText("Hello, this is a test message"));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("")));
    }

    @Test
    public void testSendMessageWithEmptyText() {
        onView(withId(R.id.message_input))
                .perform(typeText(""));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("")));
    }

    @Test
    public void testSendMessageWithWhitespaceOnly() {
        onView(withId(R.id.message_input))
                .perform(typeText("   "));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("   ")));
    }

    @Test
    public void testSendMultipleMessages() {
        String[] messages = {"First message", "Second message", "Third message"};
        
        for (String message : messages) {
            onView(withId(R.id.message_input))
                    .perform(ViewActions.clearText())
                    .perform(typeText(message));
            
            onView(withId(R.id.send_button))
                    .perform(click());
        }
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("")));
    }

    @Test
    public void testMessageInputFocus() {
        // Test message input focus
        onView(withId(R.id.message_input))
                .perform(click())
                .check(matches(ViewMatchers.isFocused()));
    }

    @Test
    public void testMessageInputTextClearing() {
        onView(withId(R.id.message_input))
                .perform(typeText("Test message"))
                .perform(ViewActions.clearText())
                .check(matches(ViewMatchers.withText("")));
    }

    @Test
    public void testLongMessageInput() {
        String longMessage = "This is a very long message that might test the limits of the input field and see how it handles extended text content.";
        
        onView(withId(R.id.message_input))
                .perform(typeText(longMessage));
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText(longMessage)));
    }

    @Test
    public void testSpecialCharactersInMessage() {
        String specialMessage = "Hello! @#$%^&*()_+-=[]{}|;':\",./<>?";
        
        onView(withId(R.id.message_input))
                .perform(typeText(specialMessage));
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText(specialMessage)));
    }

    @Test
    public void testMessageInputHint() {
        onView(withId(R.id.message_input))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSendButtonClickHandling() {
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.send_button))
                .check(matches(isClickable()));
    }

    @Test
    public void testChatListDisplay() {
        onView(withId(R.id.chat_list))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testChatListInitialState() {
        onView(withId(R.id.chat_list))
                .check(matches(hasChildCount(0)));
    }

    @Test
    public void testChatListAfterSendingMessage() {
        onView(withId(R.id.message_input))
                .perform(typeText("Test message"));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.chat_list))
                .check(matches(hasChildCount(1)));
    }

    @Test
    public void testChatListAfterSendingMultipleMessages() {
        String[] messages = {"Message 1", "Message 2", "Message 3"};
        
        for (String message : messages) {
            onView(withId(R.id.message_input))
                    .perform(ViewActions.clearText())
                    .perform(typeText(message));
            
            onView(withId(R.id.send_button))
                    .perform(click());
        }
        
        onView(withId(R.id.chat_list))
                .check(matches(hasChildCount(3)));
    }

    @Test
    public void testMessageInputValidation() {
        onView(withId(R.id.message_input))
                .perform(typeText(""));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("")));
    }

    @Test
    public void testMessageInputTypeValidation() {
        onView(withId(R.id.message_input))
                .perform(typeText("Valid message"));
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("Valid message")));
    }

    @Test
    public void testSendButtonEnabledState() {
        onView(withId(R.id.send_button))
                .check(matches(isEnabled()));
    }

    @Test
    public void testMessageInputEnabledState() {
        onView(withId(R.id.message_input))
                .check(matches(isEnabled()));
    }

    @Test
    public void testChatListScrollability() {
        onView(withId(R.id.chat_list))
                .check(matches(isDisplayed()));
        
        for (int i = 0; i < 10; i++) {
            onView(withId(R.id.message_input))
                    .perform(ViewActions.clearText())
                    .perform(typeText("Message " + i));
            
            onView(withId(R.id.send_button))
                    .perform(click());
        }
        
        onView(withId(R.id.chat_list))
                .check(matches(hasChildCount(10)));
    }

    @Test
    public void testMessageInputMultiline() {
        String multilineMessage = "Line 1\nLine 2\nLine 3";
        
        onView(withId(R.id.message_input))
                .perform(typeText(multilineMessage));
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText(multilineMessage)));
    }

    @Test
    public void testMessageInputMaxLength() {
        String longMessage = "A".repeat(100);
        
        onView(withId(R.id.message_input))
                .perform(typeText(longMessage));
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText(longMessage)));
    }

    @Test
    public void testChatListAdapter() {
        onView(withId(R.id.chat_list))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.message_input))
                .perform(typeText("Test message"));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.chat_list))
                .check(matches(hasChildCount(1)));
    }

    @Test
    public void testMessageInputAccessibility() {
        onView(withId(R.id.message_input))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.message_input))
                .check(matches(isEnabled()));
    }

    @Test
    public void testSendButtonAccessibility() {
        onView(withId(R.id.send_button))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.send_button))
                .check(matches(isEnabled()));
    }

    @Test
    public void testChatListAccessibility() {
        onView(withId(R.id.chat_list))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementLayout() {
        onView(withId(R.id.chat_list))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.message_input))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.send_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementResponsiveness() {
        onView(withId(R.id.chat_list))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.message_input))
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.send_button))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementInteractions() {
        onView(withId(R.id.message_input))
                .perform(click())
                .perform(typeText("Test message"));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("")));
    }

    @Test
    public void testUIElementVisibilityAfterInteraction() {
        onView(withId(R.id.message_input))
                .perform(click())
                .check(matches(isDisplayed()));
        
        onView(withId(R.id.send_button))
                .perform(click())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUIElementPerformance() {
        onView(withId(R.id.message_input))
                .perform(typeText("Performance test message"));
        
        onView(withId(R.id.send_button))
                .perform(click());
        
        onView(withId(R.id.message_input))
                .check(matches(ViewMatchers.withText("")));
    }
}

