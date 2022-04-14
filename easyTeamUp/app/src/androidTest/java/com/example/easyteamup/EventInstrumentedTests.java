package com.example.easyteamup;
import static androidx.test.espresso.Espresso.onView;

import android.content.Context;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;
import android.os.SystemClock;
import static org.junit.Assert.*;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EventInstrumentedTests {
    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule =
            new ActivityScenarioRule<>(SignUpActivity.class);
    public UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public DBHandler dataSource = new DBHandler(InstrumentationRegistry.getInstrumentation().getTargetContext());

    @Before
    public void setUp() {
        dataSource.deleteTableContents("events");
    }

    @Test
    public void testDeadline() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        //verify that the toast pops up and you can't enter it
    }

    public void testJoinPublic() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        //join event and add to guest list
    }

    public void testJoinTimeSlot() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));

    }
    public void testSentInvites() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
//verify that messages are sent to
    }

}
