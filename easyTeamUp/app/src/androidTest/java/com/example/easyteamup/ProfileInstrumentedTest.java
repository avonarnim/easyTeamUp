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
public class ProfileInstrumentedTest {

    @Rule
    public ActivityScenarioRule<SignUpActivity> activityRule =
            new ActivityScenarioRule<>(SignUpActivity.class);
    public UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    public DBHandler dataSource = new DBHandler(InstrumentationRegistry.getInstrumentation().getTargetContext());

    @Test
    public void testHostedEvent() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("currentHostEvent", "username", 0.0, 0.0, currentTime+Long.valueOf(111000));

        onView(withId(R.id.username))
                .perform(typeText("username"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());


        UiObject hostedEvent = device.findObject(new UiSelector()
                .text("currentHostEvent")
                .className("android.widget.TextView"));

        assertTrue(hostedEvent.exists());

    }

    @Test
    public void testPastEvent() {

        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("pastSampleEvent", "username", 0.0, 0.0, currentTime-Long.valueOf(111000));

        onView(withId(R.id.username))
                .perform(typeText("username"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());


        UiObject pastEvent = device.findObject(new UiSelector()
                .text("pastSampleEvent")
                .className("android.widget.TextView"));

        assertTrue(pastEvent.exists());

    }

    @Test
    public void testFutureEvent() {

        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleFutureEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(1110000));
        dataSource.addNewTimeslot(eventId, "username", currentTime+Long.valueOf(111000));

        onView(withId(R.id.username))
                .perform(typeText("username"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());


        UiObject futureEvent = device.findObject(new UiSelector()
                .text("sampleFutureEvent")
                .className("android.widget.TextView"));

        assertTrue(futureEvent.exists());

    }

    @Test
    public void testHostEventChange() throws UiObjectNotFoundException {

        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("changeHostEvent", "username", 0.0, 0.0, currentTime+Long.valueOf(11111000));

        onView(withId(R.id.username))
                .perform(typeText("username"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());

        UiObject hostedEvent = device.findObject(new UiSelector()
                .text("changeHostEvent")
                .className("android.widget.TextView"));

            hostedEvent.click();
            UiObject editEventName = device.findObject(new UiSelector()
                    .text("changeHostEvent")
                    .className("android.widget.EditText"));
            editEventName.setText("newEventName");
            UiObject saveButton = device.findObject(new UiSelector()
                    .text("SAVE CHANGES")
                    .className("android.widget.Button"));
            saveButton.click();

    }

        @Test
    public void testMessageSentToGuests() throws UiObjectNotFoundException {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("testMessageEvent", "username", 0.0, 0.0, currentTime+Long.valueOf(11111000));
        dataSource.addNewProfile("guestUsername", "guestPassword");
        dataSource.addNewTimeslot(eventId, "guestUsername", currentTime+Long.valueOf(111000));

        onView(withId(R.id.username))
                .perform(typeText("username"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("password"), closeSoftKeyboard());
        onView(withId(R.id.signUpButton)).perform(click());

        UiObject hostedEvent = device.findObject(new UiSelector()
                .text("testMessageEvent")
                .className("android.widget.TextView"));

            hostedEvent.click();
            UiObject editEventName = device.findObject(new UiSelector()
                    .text("testMessageEvent")
                    .className("android.widget.EditText"));
            editEventName.setText("changingEventName");
            UiObject saveButton = device.findObject(new UiSelector()
                    .text("SAVE CHANGES")
                    .className("android.widget.Button"));
            saveButton.click();

            UiObject signOut = device.findObject(new UiSelector()
                    .text("Sign Out")
                    .className("android.widget.TextView"));
            signOut.click();

            onView(withId(R.id.usernameLogIn))
                    .perform(typeText("guestUsername"), closeSoftKeyboard());
            onView(withId(R.id.passwordLogIn))
                    .perform(typeText("guestPassword"), closeSoftKeyboard());
            onView(withId(R.id.logInButton)).perform(click());

            UiObject message = device.findObject(new UiSelector()
                    .text("Messages")
                    .className("android.widget.TextView"));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertTrue(message.exists());

    }


}