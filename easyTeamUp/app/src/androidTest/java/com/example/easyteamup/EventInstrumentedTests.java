package com.example.easyteamup;
import static android.app.PendingIntent.getActivity;
import static android.service.autofill.Validators.not;
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
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;
import android.os.SystemClock;
import android.widget.Toast;

import static org.hamcrest.CoreMatchers.is;
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
        Intents.init();
        dataSource.deleteTableContents("events");
    }

    @After
    public void breakDown() {
        Intents.release();
    }

    @Test
    public void testDeadline() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        //verify that the toast pops up and you can't enter it
        //onView(withText("Make sure the Deadline is before all Timeslots...")).check(matches(isDisplayed()));
        onView(withId(R.id.username))
                .perform(typeText("guest"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("guestPassword"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
    }

    @Test
    public void testJoinPublic() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        onView(withId(R.id.username))
                .perform(typeText("guest"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("guestPassword"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
        }


    @Test
    public void testJoinTimeSlot() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        onView(withId(R.id.username))
                .perform(typeText("guest"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("guestPassword"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
    }

    @Test
    public void testSentInvites() throws UiObjectNotFoundException {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
            dataSource.addNewTimeslot(eventId, "guest", currentTime + Long.valueOf(111000));

            onView(withId(R.id.username))
                    .perform(typeText("guest"), closeSoftKeyboard());
            onView(withId(R.id.password))
                    .perform(typeText("guestPassword"), closeSoftKeyboard());
            onView(withId(R.id.signUpButton)).perform(click());

            UiObject event = device.findObject(new UiSelector()
                    .text("testHostMessageEvent")
                    .className("android.widget.TextView"));
            event.click();

            UiObject withdraw = device.findObject(new UiSelector()
                    .text("JOIN")
                    .className("android.widget.Button"));
            withdraw.click();

            UiObject signOut = device.findObject(new UiSelector()
                    .text("Sign Out")
                    .className("android.widget.TextView"));
            signOut.click();

            onView(withId(R.id.username))
                    .perform(typeText("username"), closeSoftKeyboard());
            onView(withId(R.id.password))
                    .perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.signUpButton)).perform(click());

            UiObject message = device.findObject(new UiSelector()
                    .text("FROM: guest\nUser guest has entered timeslot testHostMessageEvent")
                    .className("android.widget.TextView"));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(message.exists());
    }

    @Test
    public void testEventCreateToProfile() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "guest", currentTime + Long.valueOf(111000));

        onView(withId(R.id.username))
                .perform(typeText("guest"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("guestPassword"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());
    }


}
