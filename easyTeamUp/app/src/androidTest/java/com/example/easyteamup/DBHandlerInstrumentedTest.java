package com.example.easyteamup;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
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
        dataSource.addNewProfile("emptyUser", "enteredPassword");
        Profile retrievedProfile = dataSource.getPublicProfile("emptyUser");

        assertTrue(retrievedProfile.getUsername().equals("emptyUser"));
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
        Event newEvent = new Event(eventId, "newName", "", 1.0, 1.0, Long.valueOf(111001), Long.valueOf(111100));
        dataSource.updateEventInfo(newEvent);

        Event retrievedEvent = dataSource.getEventInfo(eventId);
        assertTrue(retrievedEvent.getName().equals("newName"));
        assertTrue(retrievedEvent.getHost().equals("hostUsername")); // host username should not be changed in an update event user flow
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
        int eventId = dataSource.addNewEvent("sampleFutureEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "sampleGuest", currentTime+Long.valueOf(11000));
        ArrayList<Event> retrievedEvent = dataSource.futureEvents("sampleGuest", currentTime);

        for(int i = 0; i < retrievedEvent.size(); i++) {
            if(retrievedEvent.get(0).getName().equals("sampleFutureEvent"))  {
                assertTrue(retrievedEvent.get(0).getHost().equals("hostUsername"));
                assertTrue(Double.valueOf(retrievedEvent.get(0).getLatitude()).equals(0.0));
                assertTrue(Double.valueOf(retrievedEvent.get(0).getLongitude()).equals(0.0));
                assertTrue(retrievedEvent.get(0).getDeadline().equals(currentTime+Long.valueOf(111000)));
            }
        }
    }

    @Test
    public void testPastEvent() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("samplePastEvent", "hostUsername", 0.0, 0.0, currentTime-Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "sampleGuest", Long.valueOf(111000));
        ArrayList<Event> retrievedEvent = dataSource.pastEvents("sampleGuest", currentTime);

        for(int i = 0; i < retrievedEvent.size(); i++) {
            if (retrievedEvent.get(0).getName().equals("samplePastEvent")) {
                assertTrue(retrievedEvent.get(0).getHost().equals("hostUsername"));
                assertTrue(Double.valueOf(retrievedEvent.get(0).getLatitude()).equals(0.0));
                assertTrue(Double.valueOf(retrievedEvent.get(0).getLongitude()).equals(0.0));
                assertTrue(retrievedEvent.get(0).getDeadline().equals(currentTime - Long.valueOf(111000)));
            }
        }

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
    public void testGetEventsInAreaExclusive() {
        dataSource.deleteTableContents("events");

        ArrayList<Integer> includedEventIds = new ArrayList<>();
        ArrayList<Integer> excludedEventIds = new ArrayList<>();
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        Long futureTime = new Long(System.currentTimeMillis() / 100L + 100000);
        for (int i = 0; i < 5; i++) {
            includedEventIds.add(dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0+10*i, 0.0+10*i, futureTime));
            excludedEventIds.add(dataSource.addNewEvent("sampleEvent", "hostUsername", 50.0+10*i, 50.0+10*i, futureTime));
        }

        Double rightUpperLat = 49.0;
        Double rightUpperLong = 49.0;
        Double leftLowerLat = -1.0;
        Double leftLowerLong = -1.0;

        ArrayList<Event> eventsInArea = dataSource.getEventsInArea(rightUpperLat, rightUpperLong,
                leftLowerLat, leftLowerLong, currentTime);
        for (Event event : eventsInArea) {
            Integer inIncluded = 0;
            Integer inExcluded = 0;
            for (int i = 0; i < 5; i++) {
                if (event.getId() == includedEventIds.get(i)) inIncluded += 1;
                if (event.getId() == excludedEventIds.get(i)) inExcluded += 1;
            }
            assertTrue(inIncluded.equals(1));
            assertTrue(inExcluded.equals(0));
        }

    }

    @Test
    public void testGetAvailableTimeslots() {
        int eventId = dataSource.addNewEvent("timeslotTestEvent", "hostUsername", 0.0, 0.0, Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "hostUsername", Long.valueOf(111005));
        dataSource.addNewTimeslot(eventId, "hostUsername", Long.valueOf(111006));
        dataSource.addNewTimeslot(eventId, "guestUsername", Long.valueOf(111007));
        dataSource.addNewTimeslot(eventId, "guestUsername", Long.valueOf(111008));
        ArrayList<Long> availableTimeslots = dataSource.getAvailableTimeslots(eventId);

        ArrayList<Long> includedTimes = new ArrayList<>();
        includedTimes.add(Long.valueOf(111005));
        includedTimes.add(Long.valueOf(111006));

        ArrayList<Long> excludedTimes = new ArrayList<>();
        excludedTimes.add(Long.valueOf(111007));
        excludedTimes.add(Long.valueOf(111008));


        Boolean onlyGoodAppear = Boolean.TRUE;

        for (Long timeslot : availableTimeslots) {
            Boolean timeslotInIncluded = Boolean.FALSE;
            Boolean timeslotNotInExcluded = Boolean.TRUE;

            for (Long incl : includedTimes) {
                if (incl.equals(timeslot)) {
                    timeslotInIncluded = Boolean.TRUE;
                }
            }
            for (Long excl : excludedTimes) {
                if (excl.equals(timeslot)) {
                    timeslotNotInExcluded = Boolean.FALSE;
                }
            }

            onlyGoodAppear = onlyGoodAppear && timeslotInIncluded && timeslotNotInExcluded;
        }
        assertTrue(onlyGoodAppear);
    }

    @Test
    public void testDecideOnTime() throws InterruptedException {
        Long mostFrequent = Long.valueOf(111005);

        int eventId = dataSource.addNewEvent("decideOnTimeTestEvent", "hostUsername", 0.0, 0.0, Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "hostUsername", mostFrequent);
        dataSource.addNewTimeslot(eventId, "hostUsername", Long.valueOf(111006));
        dataSource.addNewTimeslot(eventId, "hostUsername", Long.valueOf(111007));
        dataSource.addNewTimeslot(eventId, "guestUsername1", mostFrequent);
        dataSource.addNewTimeslot(eventId, "guestUsername2", mostFrequent);
        dataSource.addNewTimeslot(eventId, "guestUsername3", mostFrequent);
        dataSource.addNewTimeslot(eventId, "guestUsername1", Long.valueOf(111006));

        Long decidedTime = dataSource.decideOnTime(eventId);
        assertEquals(mostFrequent, decidedTime);
    }

    @Test
    public void testGetDecidedTime() throws InterruptedException {

        Long mostFrequent = Long.valueOf(111005);
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        Long fiveSecondsAwayDeadline = currentTime + 5;

        int eventId = dataSource.addNewEvent("decideOnTimeTestEvent", "hostUsername", 0.0, 0.0, fiveSecondsAwayDeadline);
        dataSource.addNewTimeslot(eventId, "hostUsername", mostFrequent);
        dataSource.addNewTimeslot(eventId, "hostUsername", Long.valueOf(111006));
        dataSource.addNewTimeslot(eventId, "hostUsername", Long.valueOf(111007));
        dataSource.addNewTimeslot(eventId, "guestUsername1", mostFrequent);
        dataSource.addNewTimeslot(eventId, "guestUsername2", mostFrequent);
        dataSource.addNewTimeslot(eventId, "guestUsername3", mostFrequent);
        dataSource.addNewTimeslot(eventId, "guestUsername1", Long.valueOf(111006));

//        Waits till after the deadline
        TimeUnit.SECONDS.sleep(5);

        Long decidedTime = dataSource.getDecidedTime(eventId);
        assertEquals(decidedTime, mostFrequent);
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.easyteamup", appContext.getPackageName());
    }

    @Test
    public void testInviteSend() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "sampleGuest", Long.valueOf(111000));
        dataSource.updateEventGuestList(eventId, "sampleGuest");
        ArrayList<Message> messages = dataSource.getMessages("sampleGuest");
        assertNotEquals(0, messages.size());
    }


    @Test
    public void testPrivateEvent() {
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        int eventId = dataSource.addNewEvent("sampleEvent", "hostUsername", 0.0, 0.0, currentTime+Long.valueOf(111000));
        dataSource.addNewTimeslot(eventId, "sampleGuest", Long.valueOf(111000));
        dataSource.updateEventGuestList(eventId, "sampleGuest");
        ArrayList<Message> messages = dataSource.getMessages("sampleGuest");
        assertNotEquals(0, messages.size());
    }
}
