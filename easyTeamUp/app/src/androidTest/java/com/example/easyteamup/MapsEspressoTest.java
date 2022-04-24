package com.example.easyteamup;

import static androidx.test.espresso.Espresso.onView;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
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

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Rule;
import android.os.SystemClock;
import android.view.View;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class MapsEspressoTest {

    private UiDevice device;
    private DBHandler dataSource;

    @Rule
    public ActivityScenarioRule<MapsActivity> activityRule =
            new ActivityScenarioRule<>(MapsActivity.class);

    @Before
    public void setUp() {
        Intents.init();
        dataSource = new DBHandler(InstrumentationRegistry.getInstrumentation().getTargetContext());
        dataSource.deleteTableContents("events");
        dataSource.deleteTableContents("messages");
        dataSource.deleteTableContents("timeslots");
        dataSource.deleteTableContents("guestlists");
        dataSource.deleteTableContents("profiles");
    }

    @After
    public void breakDown() {
        Intents.release();
    }

//    Events that have already occurred and are in the past should not appear
    @Test
    public void testPastDeadlinePastDecidedTime() {

        Integer pastEventId = dataSource.addNewEvent("past event", "username", -5.0, -5.0, Long.valueOf(10));
        dataSource.addNewTimeslot(pastEventId, "username", Long.valueOf(11));
        dataSource.addNewTimeslot(pastEventId, "username", Long.valueOf(12));
        dataSource.addNewTimeslot(pastEventId, "username", Long.valueOf(13));

        Event pastEventInfo = dataSource.getEventInfo(pastEventId);
        pastEventInfo.setFinalTime(Long.valueOf(15));
        dataSource.updateEventInfo(pastEventInfo);

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            UiObject marker = device.findObject(new UiSelector().descriptionContains("past event"));
            marker.click();
            fail("registered a past event incorrectly");
        } catch (UiObjectNotFoundException err) {
            return;
        }
    }

//    Events that have a past deadline should still be findable
    @Test
    public void testPastDeadlineFutureDecidedTime() {

        Integer pastEventId = dataSource.addNewEvent("semi-past event", "username", -15.0, -15.0, Long.valueOf(10));
        Event pastEventInfo = dataSource.getEventInfo(pastEventId);
        Long futureTime = new Long(System.currentTimeMillis() + 100000);
        dataSource.addNewTimeslot(pastEventId, "username", Long.valueOf(11));
        dataSource.addNewTimeslot(pastEventId, "username", Long.valueOf(12));
        dataSource.addNewTimeslot(pastEventId, "username", futureTime);
        pastEventInfo.setFinalTime(futureTime);
        dataSource.updateEventInfo(pastEventInfo);

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            UiObject marker = device.findObject(new UiSelector().descriptionContains("semi-past event"));
            marker.click();
            return;
        } catch (UiObjectNotFoundException err) {
            fail("registered a past deadline, future event incorrectly");
        }
    }

//    Events that have a future deadline should be findable
    @Test
    public void testFutureDeadlineFutureDecidedTime() {

        Long futureDeadline = new Long(System.currentTimeMillis() + 100000);
        Long futureDecidedTime = new Long(System.currentTimeMillis() + 100001);
        Integer futureEventId = dataSource.addNewEvent("future event", "username", -5.0, -5.0, futureDeadline);
        dataSource.addNewTimeslot(futureEventId, "username", futureDecidedTime);
        dataSource.addNewTimeslot(futureEventId, "username", futureDecidedTime+2);
        dataSource.addNewTimeslot(futureEventId, "username", futureDecidedTime+1);


        Event futureEventInfo = dataSource.getEventInfo(futureEventId);
        futureEventInfo.setFinalTime(futureDecidedTime);
        dataSource.updateEventInfo(futureEventInfo);

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            UiObject marker = device.findObject(new UiSelector().descriptionContains("future event"));
            marker.click();
            return;
        } catch (UiObjectNotFoundException err) {
            fail("registered a future deadline, future event incorrectly");
        }
    }

//    Events on the map should be clickable and yield the event detail page.
    @Test
    public void testMapElementClickable() throws UiObjectNotFoundException, InterruptedException {
        int eventId = dataSource.addNewEvent("good event", "username", 40.0, 40.0, Long.valueOf(System.currentTimeMillis() + 100000));
        dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100001));
        dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100002));
        dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100003));

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("good event"));
        marker.click();

        device.findObject(new UiSelector().descriptionContains("Event"));
    }


//    Events should appear on the map as the map moves to encompass their locations
    @Test
    public void testMapPinsDynamic() {

        Long futureDeadline = new Long(System.currentTimeMillis() + 100000);
        int eventId = dataSource.addNewEvent("future event", "username", -5.0, -5.0, futureDeadline);
        dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100001));
        dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100002));
        dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100003));

        double baseLongitude = 45.0;
        for (int i = 0 ; i < 5; i++) {
            eventId = dataSource.addNewEvent("future event - out of bounds", "username", -5.0, baseLongitude+i*30.0, futureDeadline);
            dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100001));
            dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100002));
            dataSource.addNewTimeslot(eventId, "username", new Long(System.currentTimeMillis() + 100003));
        }

        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            // perform scroll
            Espresso.onView(ViewMatchers.withId(R.id.map)).perform(ViewActions.swipeLeft());
            UiObject marker = device.findObject(new UiSelector().descriptionContains("future event - out of bounds"));
            TimeUnit.SECONDS.sleep(2);

            marker.click();
            return;
        } catch (UiObjectNotFoundException | InterruptedException err) {
            fail("registered a future deadline, future event incorrectly");
        }
    }
}
