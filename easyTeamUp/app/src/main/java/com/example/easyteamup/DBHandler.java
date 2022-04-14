package com.example.easyteamup;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "easyTeamUpDB";

    // below int is our database version
    private static final int DB_VERSION = 20;

    // variables are for table names.
    private static final String EVENT_TABLE_NAME = "events";
    private static final String PROFILE_TABLE_NAME = "profiles";
    private static final String TIMESLOTS_TABLE_NAME = "timeslots";
    private static final String MESSAGES_TABLE_NAME = "messages";

    // variables for the events table
    private static final String EVENT_ID_COL = "eventId";
    private static final String EVENT_NAME_COL = "eventName";
    private static final String EVENT_HOST_COL = "eventHost";
    private static final String LATITUDE_COL = "latitude";
    private static final String LONGITUDE_COL = "longitude";
    private static final String DEADLINE_COL = "deadline";
    private static final String FINAL_TIME_COL = "finalTime";
    // NOTE: going to get "interested users" through users who have selections in time slot table
    // NOTE: going to get "time slots" from time slots table
    // NOTE: going to get "available time slots" (i.e. what's fed to interested users) from time slots table using event host id

    // variables for the profiles table
    private static final String PROFILE_ID_COL = "profileId";
    private static final String USERNAME_COL = "username";
    private static final String PASSWORD_COL = "password";
    private static final String PICTURE_COL = "picture";
    // NOTE: going to get "pastEvents" and "futureEvents" from events table (querying for </> current datetime) and timeslots table (checking if final time is in selected times)
    // NOTE: going to get "currentlyHosting" from events table using profileId as eventHost
    // NOTE: going to get "messages" from messages table using profileId

    // variables for the timeslots table
    private static final String TIMESLOT_ID_COL = "timeslotId";
    // EVENT_ID_COL (defined above)
    // USERNAME_COL (defined above)
    private static final String SELECTED_TIME_COL = "selectedTime";

    // variables for the messages table
    private static final String MESSAGE_ID_COL = "messageId";
    private static final String SENDER_USERNAME_COL = "sender";
    private static final String RECIPIENT_USERNAME_COL = "recipient";
    private static final String BODY_COL = "body";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Event table
        // Note: preparing to use unix as timestamp
        String createEvents = "CREATE TABLE " + EVENT_TABLE_NAME + " ("
                + EVENT_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EVENT_NAME_COL + " TEXT,"
                + EVENT_HOST_COL + " TEXT,"
                + LATITUDE_COL + " REAL,"
                + LONGITUDE_COL + " REAL,"
                + DEADLINE_COL + " INTEGER,"
                + FINAL_TIME_COL + " INTEGER)";

        // Profile table
        String createProfiles = "CREATE TABLE IF NOT EXISTS " + PROFILE_TABLE_NAME + " ("
                + PROFILE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " TEXT, "
                + PASSWORD_COL + " TEXT, "
                + PICTURE_COL + " BLOB)";

        // Timeslots table
        // Holds selected-as-ok timeslots for each event's interested users
        String createTimeslots = "CREATE TABLE IF NOT EXISTS " + TIMESLOTS_TABLE_NAME + " ("
                + TIMESLOT_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EVENT_ID_COL + " INTEGER, "
                + USERNAME_COL + " TEXT,"
                + SELECTED_TIME_COL + " INTEGER)";

        // Messages table
        String createMessages = "CREATE TABLE IF NOT EXISTS " + MESSAGES_TABLE_NAME + " ("
                + MESSAGE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SENDER_USERNAME_COL + " INTEGER,"
                + RECIPIENT_USERNAME_COL + " TEXT,"
                + BODY_COL + " TEXT)";

        // Creates tables
        db.execSQL(createEvents);
        db.execSQL(createProfiles);
        db.execSQL(createTimeslots);
        db.execSQL(createMessages);
    }

    public void deleteTableContents(String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table);
        db.close();
    }

    // Inserts a new event into the Event table
    public int addNewEvent(String eventName, String eventHost, Double latitude, Double longitude, Long deadline) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String[] args = {"'" + eventName + "'"};
        values.put(EVENT_NAME_COL, eventName);
        values.put(EVENT_HOST_COL, eventHost);
        values.put(LATITUDE_COL, latitude);
        values.put(LONGITUDE_COL, longitude);
        values.put(DEADLINE_COL, deadline);

        //db.insert(EVENT_TABLE_NAME, null, values);
        Long rowID = db.insert(EVENT_TABLE_NAME, null, values);
        Log.d("rowID", String.valueOf(rowID));
        db.close();
        return rowID.intValue();
    }


    // Inserts a new user profile into the Profile table
    public void addNewProfile(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USERNAME_COL, username);
        values.put(PASSWORD_COL, password);

        db.insert(PROFILE_TABLE_NAME, null, values);
        db.close();
    }

    public void uploadProfilePicture(String username, byte[] picture) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PICTURE_COL, picture);

        db.update(PROFILE_TABLE_NAME,values,"username = '"+username+"'",null);
    }

    public byte[] getProfilePicture(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] picture = null;
        String[] args = {"'" + username + "'"};
        Cursor cursorProfile = db.rawQuery("SELECT picture FROM profiles WHERE username=?",
                args);
        if (cursorProfile.moveToFirst()) {

            picture = cursorProfile.getBlob(1);
        }
        cursorProfile.close();
        return picture;
    }

    // Checks to see if a specified username-password combination is in the profile table
    public Boolean verifyProfile(String username, String password) {
        // Checks to see if a specified username-password combination is in the profile table
            SQLiteDatabase db = this.getWritableDatabase();
            Boolean validProfile = Boolean.FALSE;
            String query = "SELECT * FROM profiles WHERE username=\"" + username + "\" AND password=\"" + password + "\"";

            Cursor cursorProfile = db.rawQuery(query,
                    null);
            if (cursorProfile.moveToFirst()) {
                validProfile = Boolean.TRUE;
            }
            cursorProfile.close();
            Log.i("login", String.valueOf(validProfile));
            return validProfile;
        }

    // Returns a specified user's profile
    // Wraps calls to get past events, upcoming events, and messages
    public Profile getPublicProfile(String username) {

        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        ArrayList<Event> pastEvents = pastEvents(username, currentTime);
        ArrayList<Event> futureEvents = futureEvents(username, currentTime);
        ArrayList<Event> currentlyHosting = currentlyHostingEvents(username, currentTime);
        Log.v("currentlyHosting", String.valueOf(currentlyHosting.size()));
        ArrayList<Message> messages = getMessages(username);
        return new Profile(username, null, pastEvents, futureEvents, currentlyHosting, messages);
    }

    // Returns Event object
    @SuppressLint("Range")
    public Event getEventInfo(int eventId) {
        //Long time = getDecidedTime(eventId);
        Log.v("eventId", String.valueOf(eventId));
        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = { String.valueOf(eventId) };
        Event e = getEventInfo(eventId);
        if (getGuestList(eventId).size() == 0) {
            e.makePublic();
        } else
            e.makePrivate();
        Cursor cursorEvents = db.rawQuery("SELECT * FROM events WHERE eventId = ?", args);
        Log.d("Count",String.valueOf(cursorEvents.getCount()));
        if(cursorEvents.moveToFirst()) {
            return new Event(cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_ID_COL)),
                    cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_NAME_COL)),
                    cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_HOST_COL)),
                    cursorEvents.getDouble(cursorEvents.getColumnIndex(LATITUDE_COL)),
                    cursorEvents.getDouble(cursorEvents.getColumnIndex(LONGITUDE_COL)),
                    cursorEvents.getLong(cursorEvents.getColumnIndex(DEADLINE_COL)),
                    cursorEvents.getLong(cursorEvents.getColumnIndex(FINAL_TIME_COL)));
        }
        else return null;
    }

    public void updateEventInfo(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EVENT_NAME_COL, event.getName());
        values.put(LATITUDE_COL, event.getLatitude());
        values.put(LONGITUDE_COL, event.getLongitude());
        values.put(DEADLINE_COL, event.getDeadline());
        values.put(FINAL_TIME_COL, event.getFinalTime());

        db.update(EVENT_TABLE_NAME,values,"eventId=?",new String[]{String.valueOf(event.getId())});
    }

    // Inserts a new availability into Timeslot table
    // NOTE: could change to pass in a list of selected times, and then just perform multiple inserts here
    //edited to take
    public void addNewTimeslot(Integer eventId, String profile, Long selectedTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EVENT_ID_COL, eventId);
        values.put(USERNAME_COL, profile);
        values.put(SELECTED_TIME_COL, selectedTime);

        db.insert(TIMESLOTS_TABLE_NAME, null, values);
        db.close();
        getEventInfo(eventId).makePrivate(); //
    }

    // Inserts a new message into the Message table
    public void addNewMessage(String sender, String recipient, String body) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SENDER_USERNAME_COL, sender);
        values.put(RECIPIENT_USERNAME_COL, recipient);
        values.put(BODY_COL, body);

        db.insert(MESSAGES_TABLE_NAME, null, values);
        db.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS events");
        db.execSQL("DROP TABLE IF EXISTS profiles");
        db.execSQL("DROP TABLE IF EXISTS timeslots");
        db.execSQL("DROP TABLE IF EXISTS messages");
        onCreate(db);
    }

    // Grabs future (either final time hasn't even been decided yet or final time is greater than
    // current time) events a user is hosting (determined by event_host_col == username)
    // Creates ArrayList with those events
    @SuppressLint("Range")
    public ArrayList<Event> currentlyHostingEvents(String username, Long currentTime) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] args = {"'" + username + "'", String.valueOf(currentTime)};
        String query = "SELECT * FROM events WHERE eventHost=\"" + username + "\" AND (finalTime IS NULL OR finalTime > \"" + currentTime + "\")";
        Cursor cursorEvents = db.rawQuery(query, null);
        ArrayList<Event> eventsList = new ArrayList<>();
        if (cursorEvents.moveToFirst()) {
            do {
                //Long time = getDecidedTime(cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_ID_COL)));
                eventsList.add(new Event(cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_ID_COL)),
                        cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_NAME_COL)),
                        cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_HOST_COL)),
                        cursorEvents.getDouble(cursorEvents.getColumnIndex(LATITUDE_COL)),
                        cursorEvents.getDouble(cursorEvents.getColumnIndex(LONGITUDE_COL)),
                        cursorEvents.getLong(cursorEvents.getColumnIndex(DEADLINE_COL)),
                        cursorEvents.getLong(cursorEvents.getColumnIndex(FINAL_TIME_COL))));
            } while (cursorEvents.moveToNext());
        }
        cursorEvents.close();
        return eventsList;
    }

    // Grabs events where a user has timeslots chosen for the event & the event is in the future
    // Having a timeslot is determined by aggregating all of a user's timeslots, grouping them by
    // event_id, then making sure all events are in the future
    // Being in the future is determined by either not having a final time assigned yet or having a
    // final time greater than the current time
    @SuppressLint("Range")
    public ArrayList<Event> futureEvents(String username, Long currentTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {"'" + username + "'"};
        String query = "SELECT * FROM events, timeslots" +
                " WHERE ((events.finalTime IS NOT NULL AND events.finalTime > " + currentTime + ")" +
                " OR (events.finalTime IS NULL AND events.deadline > " + currentTime + "))" +
                " AND events.eventId = timeslots.eventId" +
                " AND timeslots.username = \"" + username + "\"";
        Cursor cursorEvents = db.rawQuery(query, null);

        ArrayList<Event> eventsList = new ArrayList<>();
        if (cursorEvents.moveToFirst()) {
            do {
                //Long time = getDecidedTime(cursorEvents.getInt(1));
                eventsList.add(new Event(cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_ID_COL)),
                        cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_NAME_COL)),
                        cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_HOST_COL)),
                        cursorEvents.getDouble(cursorEvents.getColumnIndex(LATITUDE_COL)),
                        cursorEvents.getDouble(cursorEvents.getColumnIndex(LONGITUDE_COL)),
                        cursorEvents.getLong(cursorEvents.getColumnIndex(DEADLINE_COL)),
                        cursorEvents.getLong(cursorEvents.getColumnIndex(FINAL_TIME_COL))));
            } while (cursorEvents.moveToNext());
        }
        cursorEvents.close();
        return eventsList;
    }

    // want events where a timeslot chosen by the user matched the final_time_col && event is in future
    @SuppressLint("Range")
    public ArrayList<Event> pastEvents(String username, Long currentTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {"'" + username + "'"};

        String query = "SELECT * FROM events, timeslots " +
                "WHERE ((events.finalTime IS NOT NULL AND events.finalTime < " + currentTime + ")" +
                " OR (events.finalTime IS NULL AND events.deadline < " + currentTime + "))" +
                " AND events.eventId = timeslots.eventId" +
                " AND timeslots.username = \"" + username + "\"";
        Cursor cursorEvents = db.rawQuery(query, null);

        ArrayList<Event> eventsList = new ArrayList<>();
        if(cursorEvents != null) {
            if (cursorEvents.moveToFirst()) {
                do {
                    //Long time = getDecidedTime(cursorEvents.getInt(1));
                    eventsList.add(new Event(cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_ID_COL)),
                            cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_NAME_COL)),
                            cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_HOST_COL)),
                            cursorEvents.getDouble(cursorEvents.getColumnIndex(LATITUDE_COL)),
                            cursorEvents.getDouble(cursorEvents.getColumnIndex(LONGITUDE_COL)),
                            cursorEvents.getLong(cursorEvents.getColumnIndex(DEADLINE_COL)),
                            cursorEvents.getLong(cursorEvents.getColumnIndex(FINAL_TIME_COL))));
                } while (cursorEvents.moveToNext());
            }
            cursorEvents.close();
        }
        return eventsList;
    }

    @SuppressLint("Range")
    public ArrayList<Event> getEventsInArea(Double rightUpperLat, Double rightUpperLong,
                                            Double leftLowerLat, Double leftLowerLong, Long currentTime) {
        //getEventsInArea
        SQLiteDatabase db = this.getReadableDatabase();
        String query;

        if (rightUpperLong < 0 && leftLowerLong > 0) {
            query = "SELECT * FROM events WHERE ((finalTime IS NOT NULL" +
                    " AND finalTime >" + currentTime + ")" +
                    " OR finalTime IS NULL)" +
                    " AND latitude >" +  leftLowerLat +
                    " AND latitude <" + rightUpperLat +
                    " AND ((longitude > 0 AND longitude > " + leftLowerLong + ") OR (" +
                    " longitude < 0 AND longitude < " + rightUpperLong + "))";
        } else {
            query = "SELECT * FROM events WHERE ((finalTime IS NOT NULL" +
                    " AND finalTime >" + currentTime + ")" +
                    " OR finalTime IS NULL)" +
                    " AND latitude >" + leftLowerLat +
                    " AND latitude <" + rightUpperLat +
                    " AND longitude >" + leftLowerLong +
                    " AND longitude <" + rightUpperLong;
        }
        Cursor cursorEvents = db.rawQuery(query, null);

        Log.i("", "DBHANDLER -- performed query ");

        ArrayList<Event> eventsList = new ArrayList<>();
        if(cursorEvents != null) {
            if (cursorEvents.moveToFirst()) {
                do {
                    //Long time = getDecidedTime(cursorEvents.getInt(1));
                    eventsList.add(new Event(cursorEvents.getInt(cursorEvents.getColumnIndex(EVENT_ID_COL)),
                            cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_NAME_COL)),
                            cursorEvents.getString(cursorEvents.getColumnIndex(EVENT_HOST_COL)),
                            cursorEvents.getDouble(cursorEvents.getColumnIndex(LATITUDE_COL)),
                            cursorEvents.getDouble(cursorEvents.getColumnIndex(LONGITUDE_COL)),
                            cursorEvents.getLong(cursorEvents.getColumnIndex(DEADLINE_COL)),
                            cursorEvents.getLong(cursorEvents.getColumnIndex(FINAL_TIME_COL))));
                } while (cursorEvents.moveToNext());
            }
            cursorEvents.close();
        }

        return eventsList;
    }

    // return a guest list (users who selected a time slot) for an event
    @SuppressLint("Range")
    public ArrayList<String> getGuestList(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {String.valueOf(eventId)};
        Cursor cursorEvents = db.rawQuery("SELECT username FROM timeslots WHERE eventID=?",
                args);

        ArrayList<String> guestList = new ArrayList<>();
        if(cursorEvents != null) {
            if (cursorEvents.moveToFirst()) {
                do {
                    guestList.add(cursorEvents.getString(cursorEvents.getColumnIndex("username")));
                } while (cursorEvents.moveToNext());
            }
            cursorEvents.close();
        }
        return guestList;
    }

    public void updateEventGuestList(int eventId, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("timeslots","eventId=? AND username=?", new String[]{String.valueOf(eventId), username});
    }

    // Returns a user's messages
    public ArrayList<Message> getMessages(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] args = {"'" + username + "'"};
        String query = "SELECT * FROM messages WHERE recipient=\"" + username + "\"";
        Cursor cursorMessages = db.rawQuery(query, null);
        ArrayList<Message> messageList = new ArrayList<>();
        if (cursorMessages.moveToFirst()) {
            do {
                messageList.add(new Message(cursorMessages.getString(1),
                        cursorMessages.getString(2),
                        cursorMessages.getString(3)));
            } while (cursorMessages.moveToNext());
        }
        cursorMessages.close();
        return messageList;
    }

    // Returns all of a host's possible timeslots allowed for a specific event
    @SuppressLint("Range")
    public ArrayList<Long> getAvailableTimeslots(Integer eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTimeslots = db.rawQuery("SELECT selectedTime FROM timeslots, events WHERE events.eventId= " + eventId +
                        " AND timeslots.eventId=events.eventId AND timeslots.username=events.eventHost", null);

        ArrayList<Long> timeSlots = new ArrayList<>();
        if (cursorTimeslots.moveToFirst()) {
            do {
                timeSlots.add(cursorTimeslots.getLong(cursorTimeslots.getColumnIndex(SELECTED_TIME_COL)));
            } while (cursorTimeslots.moveToNext());
        }
        cursorTimeslots.close();
        return timeSlots;
    }

    // Finds the most commonly-selected timeslot for an event
    @SuppressLint("Range")
    public Long decideOnTime(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT selectedTime FROM timeslots WHERE eventId = " + eventId;
        Cursor cursorTimeslots = db.rawQuery(query,null);
        HashMap<Long, Integer> timeToCount = new HashMap<>();

        if (cursorTimeslots.moveToFirst()) {
            do {
                Long time = cursorTimeslots.getLong(cursorTimeslots.getColumnIndex(SELECTED_TIME_COL));

                if (timeToCount.containsKey(time)) {
                    timeToCount.put(time, timeToCount.get(time) + 1);
                } else {
                    timeToCount.put(time, 1);
                }
            } while (cursorTimeslots.moveToNext());
        }

        Long decidedTime = new Long(0);
        int maxCount = 0;

        for (Long time : timeToCount.keySet()) {
            Integer count = timeToCount.get(time);
            if (count > maxCount) {
                maxCount = count;
                decidedTime = time;
            }
        }

        cursorTimeslots.close();
        return decidedTime;
    }

    // if deadline has passed, get time of event
    // if deadline has passed, call decideOnTime and update Event record with the decided time
    // return time if possible
    @SuppressLint("Range")
    public Long getDecidedTime(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        Event selectedEvent = getEventInfo(eventId);
        if (selectedEvent == null) return new Long(0);

        if (currentTime < selectedEvent.getDeadline()) {
            return new Long(0);
        } else {
            if (selectedEvent.getFinalTime() == 0) {
                selectedEvent.setFinalTime(decideOnTime(eventId));
                updateEventInfo(selectedEvent);
            }
            return selectedEvent.getFinalTime();
        }
    }
}