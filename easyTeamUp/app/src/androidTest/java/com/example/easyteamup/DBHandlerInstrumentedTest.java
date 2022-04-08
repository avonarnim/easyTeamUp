package com.example.easyteamup;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DBHandlerInstrumentedTest {

    private DBHandler dataSource;

    @Before
    public void setUp() {
        dataSource = new DBHandler(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void testPreConditions() {
        assertNotNull(dataSource);
    }

    @Test
    public void testAddNewEvent() {
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, Long.valueOf(111000));
        Event retrievedEvent = dataSource.getEventInfo(eventId);

        assertTrue(retrievedEvent.getName().equals("sampleEvent"));
        assertTrue(retrievedEvent.getHost().equals("hostUsername"));
        assertTrue(Double.valueOf(retrievedEvent.getLatitude()).equals(0.0));
        assertTrue(Double.valueOf(retrievedEvent.getLongitude()).equals(0.0));
        assertTrue(retrievedEvent.getDeadline().equals(Long.valueOf(111000)));
    }

    @Test
    public void testAddNewProfile() {
        dataSource.addNewProfile("hostUsername", "enteredPassword");
        Profile retrievedProfile = dataSource.getPublicProfile("hostUsername");

        assertTrue(retrievedProfile.getUsername().equals("hostUsername"));
        assertTrue(Integer.valueOf(retrievedProfile.getMessages().size()).equals(0));
        assertTrue(Integer.valueOf(retrievedProfile.getFutureEvents().size()).equals(0));
        assertTrue(Integer.valueOf(retrievedProfile.getPastEvents().size()).equals(0));
        assertTrue(Integer.valueOf(retrievedProfile.getCurrentlyHosting().size()).equals(0));
    }

    @Test
    public void testVerifyProfile() {
        dataSource.addNewProfile("hostUsername", "enteredPassword");
        Boolean profResultsTrue = dataSource.verifyProfile("hostUsername", "enteredPassword");
        assertTrue(profResultsTrue.equals(Boolean.TRUE));

        Boolean profResultsPasswordFalse = dataSource.verifyProfile("hostUsername", "incorrectPassword");
        assertTrue(profResultsPasswordFalse.equals(Boolean.FALSE));

        Boolean profResultsUsernameFalse = dataSource.verifyProfile("incorrectUsername", "enteredPassword");
        assertTrue(profResultsUsernameFalse.equals(Boolean.FALSE));
    }

    @Test
    public void testUpdateEvent() {
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, Long.valueOf(111000));
        Event newEvent = new Event(eventId, "newName", "newUsername", 1.0, 1.0, Long.valueOf(111001), Long.valueOf(111100));
        dataSource.updateEventInfo(newEvent);

        Event retrievedEvent = dataSource.getEventInfo(eventId);
        assertTrue(retrievedEvent.getName().equals("newName"));
        assertTrue(retrievedEvent.getHost().equals("newUsername"));
        assertTrue(Double.valueOf(retrievedEvent.getLatitude()).equals(1.0));
        assertTrue(Double.valueOf(retrievedEvent.getLongitude()).equals(1.0));
        assertTrue(retrievedEvent.getDeadline().equals(Long.valueOf(111001)));
    }

    @Test
    public void testSentMessage() {
        dataSource.addNewMessage("sender", "recipient", "body");
        ArrayList<Message> messages = dataSource.getMessages("recipient");

        assertTrue(messages.get(0).getSender().equals("sender"));
        assertTrue(messages.get(0).getRecipient().equals("recipient"));
        assertTrue(messages.get(0).getBody().equals("body"));
    }

    @Test
    public void testCurrentlyHostedEvent() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        ArrayList<Event> retrievedEvent = dataSource.currentlyHostingEvents("hostUsername", currentTime);

        assertTrue(retrievedEvent.get(0).getHost().equals("hostUsername"));
    }

    @Test
    public void testFutureEvent() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "sampleGuest", currentTime+Long.valueOf(111000));
        ArrayList<Event> retrievedEvent = dataSource.futureEvents("sampleGuest", currentTime);

        assertTrue(retrievedEvent.get(0).getName().equals("sampleEvent"));
        assertTrue(retrievedEvent.get(0).getHost().equals("hostUsername"));
        assertTrue(Double.valueOf(retrievedEvent.get(0).getLatitude()).equals(1.0));
        assertTrue(Double.valueOf(retrievedEvent.get(0).getLongitude()).equals(1.0));
        assertTrue(retrievedEvent.get(0).getDeadline().equals(currentTime+Long.valueOf(111000)));
    }

    @Test
    public void testPastEvent() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime-Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "sampleGuest", Long.valueOf(111000));
        ArrayList<Event> retrievedEvent = dataSource.pastEvents("sampleGuest", currentTime);

        assertTrue(retrievedEvent.get(0).getName().equals("sampleEvent"));
        assertTrue(retrievedEvent.get(0).getHost().equals("hostUsername"));
        assertTrue(Double.valueOf(retrievedEvent.get(0).getLatitude()).equals(1.0));
        assertTrue(Double.valueOf(retrievedEvent.get(0).getLongitude()).equals(1.0));
        assertTrue(retrievedEvent.get(0).getDeadline().equals(currentTime-Long.valueOf(111000)));
    }

    @Test
    public void testUpdateGuestList() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "sampleGuest", Long.valueOf(111000));
        dataSource.updateEventGuestList(eventId, "sampleGuest");

        ArrayList<String> guestList = dataSource.getGuestList(eventId);
        for(int i = 0; i < guestList.size(); i++) {
            assertNotEquals("sampleGuest", guestList.get(0));
        }
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.easyteamup", appContext.getPackageName());
    }
}