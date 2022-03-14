package com.example.easyteamup;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String PASSWORD = "password";
    // NOTE: going to get "pastEvents" and "futureEvents" from events table (querying for </> current datetime) and timeslots table (checking if final time is in selected times)
    // NOTE: going to get "currentlyHosting" from events table using profileId as eventHost
    // NOTE: going to get "messages" from messages table using profileId

    // variables for the timeslots table
    // EVENT_ID_COL (defined above)
    // PROFILE_ID_COL (defined above)
    private static final String SELECTED_TIME_COL = "selectedTime";

    // variables for the messages table
    private static final String MESSAGE_ID_COL = "messageId";
    private static final String SENDER_ID_COL = "senderId";
    private static final String RECIPIENT_ID_COL = "recipientId";
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
                + DEADLINE_COL + " TEXT,"
                + FINAL_TIME_COL + " TEXT)";

        String createProfiles = "CREATE TABLE " + PROFILE_TABLE_NAME + " ("
                + PROFILE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_COL + " TEXT"
                + PASSWORD + "TEXT)";

        String createTimeslots = "CREATE TABLE " + TIMESLOTS_TABLE_NAME + " ("
                + EVENT_ID_COL + " INTEGER PRIMARY KEY, "
                + PROFILE_ID_COL + " INTEGER,"
                + SELECTED_TIME_COL + " TEXT)";

        String createMessages = "CREATE TABLE " + MESSAGES_TABLE_NAME + " ("
                + MESSAGE_ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SENDER_ID_COL + " INTEGER,"
                + RECIPIENT_ID_COL + " INTEGER,"
                + BODY_COL + " TEXT)";

        db.execSQL(createEvents);
        db.execSQL(createProfiles);
        db.execSQL(createTimeslots);
        db.execSQL(createMessages);
    }

    public void addNewEvent(String eventName, String eventHost, Double latitude, Double longitude, String deadline) {

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
        values.put(PASSWORD, password);

        db.insert(PROFILE_TABLE_NAME, null, values);
        db.close();
    }

    // NOTE: could change to pass in a list of selected times, and then just perform multiple inserts here
    public void addNewTimeslot(Integer eventId, Integer profileId, String selectedTime) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EVENT_ID_COL, eventId);
        values.put(PROFILE_ID_COL, profileId);
        values.put(SELECTED_TIME_COL, selectedTime);

        db.insert(TIMESLOTS_TABLE_NAME, null, values);
        db.close();
    }

    public void addNewMessage(Integer senderId, Integer recipientId, String body) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SENDER_ID_COL, senderId);
        values.put(RECIPIENT_ID_COL, recipientId);
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
}