package com.example.easyteamup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "easyTeamUpDB";

    // below int is our database version
    private static final int DB_VERSION = 1;

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
    // NOTE: going to get "pastEvents" and "futureEvents" from events table (querying for </> current datetime) and timeslots table (checking if final time is in selected times)
    // NOTE: going to get "currentlyHosting" from events table using profileId as eventHost
    // NOTE: going to get "messages" from messages table using profileId

    // variables for the timeslots table
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
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String createEvents = "CREATE TABLE " + EVENT_TABLE_NAME + " ("
                + EVENT_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EVENT_NAME_COL + " TEXT,"
                + EVENT_HOST_COL + " TEXT,"
                + LATITUDE_COL + " REAL,"
                + LONGITUDE_COL + " REAL,"
                + DEADLINE_COL + " INTEGER,"
                + FINAL_TIME_COL + " INTEGER)";

        String createProfiles = "CREATE TABLE " + PROFILE_TABLE_NAME + " ("
                + PROFILE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " TEXT"
                + PASSWORD_COL + "TEXT)";

        String createTimeslots = "CREATE TABLE " + TIMESLOTS_TABLE_NAME + " ("
                + EVENT_ID_COL + " INTEGER PRIMARY KEY, "
                + USERNAME_COL + " TEXT,"
                + SELECTED_TIME_COL + " INTEGER)";

        String createMessages = "CREATE TABLE " + MESSAGES_TABLE_NAME + " ("
                + MESSAGE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SENDER_USERNAME_COL + " INTEGER,"
                + RECIPIENT_USERNAME_COL + " TEXT,"
                + BODY_COL + " TEXT)";

        db.execSQL(createEvents);
        db.execSQL(createProfiles);
        db.execSQL(createTimeslots);
        db.execSQL(createMessages);
    }

    public void addNewEvent(String eventName, String eventHost, Double latitude, Double longitude, Long deadline) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EVENT_NAME_COL, eventName);
        values.put(EVENT_HOST_COL, eventHost);
        values.put(LATITUDE_COL, latitude);
        values.put(LONGITUDE_COL, longitude);
        values.put(DEADLINE_COL, deadline);

        db.insert(EVENT_TABLE_NAME, null, values);
        db.close();
    }

    public void addNewProfile(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(USERNAME_COL, username);
        values.put(PASSWORD_COL, password);

        db.insert(PROFILE_TABLE_NAME, null, values);
        db.close();
    }

    public Boolean verifyProfile(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Boolean validProfile = Boolean.FALSE;

        Cursor cursorProfile = db.rawQuery("SELECT * " +
                        " FROM " + PROFILE_TABLE_NAME +
                        " WHERE " + USERNAME_COL + "=" + username +
                        " AND " + PASSWORD_COL + "=" + password,
                new String[]{"data"});
        if (cursorProfile.moveToFirst()) {
            validProfile = Boolean.TRUE;
        }
        cursorProfile.close();
        return validProfile;
    }

    // get "pastEvents" and "futureEvents" from events table (querying for </> current datetime) and timeslots table (checking if final time is in selected times)
    // get "messages" from messages table using profileId
    public Profile getPublicProfile(String username) {

        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        ArrayList<Event> pastEvents = pastEvents(username, currentTime);
        ArrayList<Event> futureEvents = futureEvents(username, currentTime);
        ArrayList<Event> currentlyHosting = currentlyHostingEvents(username, currentTime);
        ArrayList<Message> messages = getMessages(username);

        return new Profile(username, pastEvents, futureEvents, currentlyHosting, messages);
    }

    // NOTE: could change to pass in a list of selected times, and then just perform multiple inserts here
    public void addNewTimeslot(Integer eventId, Integer profileId, Long selectedTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EVENT_ID_COL, eventId);
        values.put(PROFILE_ID_COL, profileId);
        values.put(SELECTED_TIME_COL, selectedTime);

        db.insert(TIMESLOTS_TABLE_NAME, null, values);
        db.close();
    }

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
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TIMESLOTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Event> currentlyHostingEvents(String username, Long currentTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorEvents = db.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME +
                " WHERE " + EVENT_HOST_COL + " = " + username +
                " AND " + DEADLINE_COL + " < " + currentTime +
                " AND (" + FINAL_TIME_COL + " IS NULL OR " + FINAL_TIME_COL + " > " + currentTime + ")",
                new String[]{"data"});
        ArrayList<Event> eventsList = new ArrayList<>();
        if (cursorEvents.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                eventsList.add(new Event(cursorEvents.getInt(1),
                        cursorEvents.getString(2),
                        cursorEvents.getString(3),
                        cursorEvents.getFloat(4),
                        cursorEvents.getFloat(5),
                        cursorEvents.getLong(6),
                        cursorEvents.getLong(7)));
            } while (cursorEvents.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorEvents.close();
        return eventsList;
    }

    // want events where a user has timeslots chosen for the event & the event is in the future
    public ArrayList<Event> futureEvents(String username, Long currentTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorEvents = db.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME +
                    " WHERE ((" + FINAL_TIME_COL + " IS NOT NULL" +
                    " AND " + FINAL_TIME_COL + " > " + currentTime + ")" +
                    " OR " + FINAL_TIME_COL + " IS NULL)" +
                    " AND " + EVENT_ID_COL + " IN (SELECT DISTINCT(" + EVENT_ID_COL + ") FROM " + TIMESLOTS_TABLE_NAME +
                    " WHERE " + USERNAME_COL + " = " + username +")",
                new String[]{"data"});

        ArrayList<Event> eventsList = new ArrayList<>();
        if (cursorEvents.moveToFirst()) {
            do {
                eventsList.add(new Event(cursorEvents.getInt(1),
                    cursorEvents.getString(2),
                    cursorEvents.getString(3),
                    cursorEvents.getFloat(4),
                    cursorEvents.getFloat(5),
                    cursorEvents.getLong(6),
                    cursorEvents.getLong(7)));
            } while (cursorEvents.moveToNext());
        }
        cursorEvents.close();
        return eventsList;
    }

    // want events where a timeslot chosen by the user matched the final_time_col && event is in future
    public ArrayList<Event> pastEvents(String username, Long currentTime) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorEvents = db.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME +
                    " WHERE " + FINAL_TIME_COL + " IS NOT NULL" +
                    " AND " + FINAL_TIME_COL + " < " + currentTime +
                    " AND " + EVENT_ID_COL + " IN (SELECT DISTINCT(" + EVENT_ID_COL + ") FROM " + TIMESLOTS_TABLE_NAME +
                    " WHERE " + USERNAME_COL + " = " + username +")",
                new String[]{"data"});

        ArrayList<Event> eventsList = new ArrayList<>();
        if (cursorEvents.moveToFirst()) {
            do {
                eventsList.add(new Event(cursorEvents.getInt(1),
                        cursorEvents.getString(2),
                        cursorEvents.getString(3),
                        cursorEvents.getFloat(4),
                        cursorEvents.getFloat(5),
                        cursorEvents.getLong(6),
                        cursorEvents.getLong(7)));
            } while (cursorEvents.moveToNext());
        }
        cursorEvents.close();
        return eventsList;
    }

    public ArrayList<Message> getMessages(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorMessages = db.rawQuery("SELECT * FROM " + MESSAGES_TABLE_NAME +
                " WHERE " + RECIPIENT_USERNAME_COL + "=" + username,
                new String[]{"data"});
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

    public ArrayList<Long> getAvailableTimeslots(String eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTimeslots = db.rawQuery("SELECT " + SELECTED_TIME_COL +
                        " FROM " + TIMESLOTS_TABLE_NAME + "," + EVENT_TABLE_NAME +
                        " WHERE " + EVENT_TABLE_NAME + "." + EVENT_ID_COL + "=" + eventId +
                        " AND " + TIMESLOTS_TABLE_NAME + "." + EVENT_ID_COL + "=" + EVENT_TABLE_NAME + "." + EVENT_ID_COL +
                        " AND " + TIMESLOTS_TABLE_NAME + "." + PROFILE_ID_COL + "=" + EVENT_TABLE_NAME + "." + EVENT_HOST_COL,
                new String[]{"data"});
        ArrayList<Long> timeSlots = new ArrayList<>();
        if (cursorTimeslots.moveToFirst()) {
            do {
                timeSlots.add(cursorTimeslots.getLong(1));
            } while (cursorTimeslots.moveToNext());
        }
        cursorTimeslots.close();
        return timeSlots;
    }

    public Long decideOnTime(String eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorTimeslots = db.rawQuery("SELECT " + SELECTED_TIME_COL + ", COUNT(" + SELECTED_TIME_COL + ") FROM " + TIMESLOTS_TABLE_NAME +
                        " WHERE " + TIMESLOTS_TABLE_NAME + "." + EVENT_ID_COL + "=" + EVENT_TABLE_NAME + "." + EVENT_ID_COL,
                new String[]{"data"});
        ArrayList<Long> timeSlots = new ArrayList<>();
        Long decidedTime = new Long(0);
        int maxCount = 0;
        if (cursorTimeslots.moveToFirst()) {
            do {
                int count = cursorTimeslots.getInt(1);
                if (count > maxCount) {
                    maxCount = count;
                    String decidedTimeString = cursorTimeslots.getString(0);
                    decidedTime = new Long(decidedTimeString);
                }
            } while (cursorTimeslots.moveToNext());
        }
        cursorTimeslots.close();
        return decidedTime;
    }

    // if deadline has passed, get time of event
    // if deadline has passed, call decideOnTime and update Event record with the decided time
    // return time if possible
    public Long getDecidedTime(String eventId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Long currentTime = new Long(System.currentTimeMillis() / 100L);
        Event selectedEvent;

        Cursor cursorEvent = db.rawQuery("SELECT " + DEADLINE_COL + ", " + FINAL_TIME_COL + " FROM " + EVENT_TABLE_NAME +
                " WHERE " + EVENT_ID_COL + " = " + eventId,
                new String[]{"data"});
        if (cursorEvent.moveToFirst()) {
            selectedEvent = new Event(cursorEvent.getInt(1),
                    cursorEvent.getString(2),
                    cursorEvent.getString(3),
                    cursorEvent.getFloat(4),
                    cursorEvent.getFloat(5),
                    new Long(cursorEvent.getString(6)),
                    new Long(cursorEvent.getString(7)));
        } else {
            return new Long(0);
        }

        if (currentTime > selectedEvent.getDeadline()) {
            return new Long(0);
        } else {
            if (selectedEvent.getFinalTime() == 0) {
                decideOnTime(eventId);
                return getDecidedTime(eventId);
            } else {
                return selectedEvent.getFinalTime();
            }
        }
    }
}