package com.example.easyteamup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.easyteamup.databinding.ActivityMainBinding;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EventPageActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    LinearLayout page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        dbHandler = new DBHandler(EventPageActivity.this);
        page = findViewById(R.id.linearLayout);

        TextView titleText = new TextView(this);
        titleText.setText("Event");
        page.addView(titleText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int eventId = extras.getInt("eventId");
            String username = extras.getString("username");
            Event eventInfo = dbHandler.getEventInfo(eventId);
            ArrayList<Long> availableTimeslots = dbHandler.getAvailableTimeslots(eventId);

            Log.i("EVENTS", "event host: " + eventInfo.getHost());
            Log.i("EVENTS", "cur user: " + username);
            Log.i("EVENTS", "deadline : " + eventInfo.getDeadline());
            Log.i("EVENTS", "cur time : " + System.currentTimeMillis());

            TextView eventIdText = new TextView(this);
            eventIdText.setText("Event ID: " + String.valueOf(eventId));
            page.addView(eventIdText);

            TextView hostText = new TextView(this);
            hostText.setText("Host: " + eventInfo.getHost());
            page.addView(hostText);

            // if user is the host of this event AND event deadline has not passed, they are able to edit (EditText) the event
            if((eventInfo.getHost().equals(username)) && (eventInfo.getDeadline() > (Long.valueOf(System.currentTimeMillis()) / 100L))) {

                TextView name = new TextView(this);
                name.setText("Event Name:");
                page.addView(name);
                EditText nameText = new EditText(this);
                nameText.setText(eventInfo.getName());
                page.addView(nameText);

                TextView longitude = new TextView(this);
                longitude.setText("Longitude:");
                page.addView(longitude);
                EditText longitudeText = new EditText(this);
                longitudeText.setText(String.valueOf(eventInfo.getLongitude()));
                page.addView(longitudeText);

                TextView latitude = new TextView(this);
                latitude.setText("Latitude:");
                page.addView(latitude);
                EditText latitudeText = new EditText(this);
                latitudeText.setText(String.valueOf(eventInfo.getLatitude()));
                page.addView(latitudeText);

                TextView deadline = new TextView(this);
                deadline.setText("Deadline:");
                page.addView(deadline);
                EditText deadlineText = new EditText(this);
                deadlineText.setText(String.valueOf(eventInfo.getDeadline()));
                page.addView(deadlineText);

                TextView finalTime = new TextView(this);
                finalTime.setText("Final Time:");
                page.addView(finalTime);
                EditText finalTimeText = new EditText(this);
                finalTimeText.setText(String.valueOf(eventInfo.getFinalTime()));
                page.addView(finalTimeText);

                Button saveBtn = new Button(this);
                saveBtn.setText("Save Changes");
                saveBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                page.addView(saveBtn);

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameText.getText().toString();
                        float longitude = Float.valueOf(longitudeText.getText().toString());
                        float latitude = Float.valueOf(latitudeText.getText().toString());
                        long deadline = Long.valueOf(deadlineText.getText().toString());
                        long finalTime = Long.valueOf(finalTimeText.getText().toString());
                        Event updatedEvent = new Event(eventId, name, eventInfo.getHost(), latitude, longitude, deadline, finalTime, eventInfo.getType());
                        // send updated event info to the database
                        dbHandler.updateEventInfo(updatedEvent);

                        // loop through guest list and send them all messages
                        List<String> guestList = dbHandler.getGuestList(eventId);
                        for(int i = 0; i < guestList.size(); i++) {
                            dbHandler.addNewMessage(eventInfo.getHost(), guestList.get(i), "Changes have been made to event: " + name);
                        }

                        Intent intent = new Intent(EventPageActivity.this, ProfileActivity.class);
                        intent.putExtra("username",username);
                        startActivity(intent);
                    }
                });
            } else {
                // Unmodifiable information is displayed if
                // (1) the user is not the host
                // (2) the user is the host and the deadline has passed
                TextView nameText = new TextView(this);
                nameText.setText("Event Name: " + eventInfo.getName());
                page.addView(nameText);

                TextView longitudeText = new TextView(this);
                longitudeText.setText("Longitude: " + eventInfo.getLongitude());
                page.addView(longitudeText);

                TextView latitudeText = new TextView(this);
                latitudeText.setText("Latitude: " + eventInfo.getLatitude());
                page.addView(latitudeText);

                TextView deadlineText = new TextView(this);
                deadlineText.setText("Deadline: " + eventInfo.getDeadline());
                page.addView(deadlineText);

                TextView finalTimeText = new TextView(this);
                finalTimeText.setText("Final Time: " + eventInfo.getFinalTime());
                page.addView(finalTimeText);

                // if user is not the host
                if (!eventInfo.getHost().equals(username)) {
                    boolean onList = dbHandler.userOnGuestList(username, eventId);

                    if (eventInfo.getDeadline() > (Long.valueOf(System.currentTimeMillis()) / 100L) && ((eventInfo.getType().equals("Private") && onList) || eventInfo.getType().equals("Public"))) {
                        // if it's not too late to sign up for a timeslot,
                        // (1) private events' guest list users may sign up for the event
                        // (2) anyone can sign up for a public event
                        TextView proposedTimes = new TextView(this);
                        proposedTimes.setText("Proposed times: ");
                        page.addView(proposedTimes);

                        TextView timeslot1Text = new TextView(this);
                        timeslot1Text.setText("" + availableTimeslots.get(0));
                        page.addView(timeslot1Text);

                        timeslot1Text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String timeslot = timeslot1Text.getText().toString();
                                dbHandler.addNewTimeslot(eventId, username, Long.getLong(timeslot));
                                Toast.makeText(EventPageActivity.this, "Timeslot " + timeslot + " has been added.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        TextView timeslot2Text = new TextView(this);
                        timeslot2Text.setText("" + availableTimeslots.get(0));
                        page.addView(timeslot2Text);

                        timeslot2Text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String timeslot = timeslot2Text.getText().toString();
                                dbHandler.addNewTimeslot(eventId, username, Long.getLong(timeslot));
                                Toast.makeText(EventPageActivity.this, "Timeslot " + timeslot + " has been added.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        TextView timeslot3Text = new TextView(this);
                        timeslot3Text.setText("" + availableTimeslots.get(2));
                        page.addView(timeslot3Text);

                        timeslot3Text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String timeslot = timeslot3Text.getText().toString();
                                dbHandler.addNewTimeslot(eventId, username, Long.getLong(timeslot));
                                Toast.makeText(EventPageActivity.this, "Timeslot " + timeslot + " has been added.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    // people on a guest list can always withdraw
                    if (onList) {

                        Button withdrawBtn = new Button(this);
                        withdrawBtn.setText("Withdraw");
                        withdrawBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        page.addView(withdrawBtn);

                        withdrawBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // update event guest list and remove this user
                                dbHandler.updateEventGuestList(eventId, username);
                                // Send message to host
                                dbHandler.addNewMessage(username, eventInfo.getHost(), "User " + username + " has withdrawn from your event " + eventInfo.getName());

                                Log.i("WITHDRAW 1", "sent message");
                                Intent intent = new Intent(EventPageActivity.this, ProfileActivity.class);
                                intent.putExtra("username",username);
                                startActivity(intent);
                            }
                        });
                    }
                }
            }
        }
    }
}