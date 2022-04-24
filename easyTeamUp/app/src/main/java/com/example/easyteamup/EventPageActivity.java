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

            Log.i("EVENTS", "event host: " + eventInfo.getHost());
            Log.i("EVENTS", "cur user: " + username);
            Log.i("EVENTS", "deadline : " + eventInfo.getDeadline());
            Log.i("EVENTS", "cur time : " + System.currentTimeMillis());

            // if user is the host of this event AND event deadline has not passed, they are able to edit (EditText) the event
            if((eventInfo.getHost().equals(username)) && (eventInfo.getDeadline() < System.currentTimeMillis())) {
                TextView eventIdText = new TextView(this);
                eventIdText.setText("Event ID: " + String.valueOf(eventId));
                page.addView(eventIdText);

                TextView host = new TextView(this);
                host.setText("Host:");
                page.addView(host);
                TextView hostText = new TextView(this);
                hostText.setText(eventInfo.getHost());
                page.addView(hostText);

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
                        Event updatedEvent = new Event(eventId, name, eventInfo.getHost(), latitude, longitude, deadline, finalTime);
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
            }
            // user is not the host
            else if (!eventInfo.getHost().equals(username)) {
                List<String> guestList = dbHandler.getGuestList(eventId);
                // loop through guest list and see if user is a guest
                boolean onList = false;
                for(int i = 0; i < guestList.size(); i++) {
                    Log.d("guest list", guestList.get(i));
                    if(guestList.get(i).equals(username)) {
                        onList = true;
                    }
                }
                // user is on the guest list
                if (onList) {
                    TextView eventIdText = new TextView(this);
                    eventIdText.setText(String.valueOf(eventId));
                    page.addView(eventIdText);

                    TextView nameText = new TextView(this);
                    nameText.setText("Event Name: " + eventInfo.getName());
                    page.addView(nameText);

                    TextView hostText = new TextView(this);
                    hostText.setText("Host: " + eventInfo.getHost());
                    page.addView(hostText);

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

                            Intent intent = new Intent(EventPageActivity.this, ProfileActivity.class);
                            intent.putExtra("username",username);
                            startActivity(intent);
                        }
                    });
                }

                // User is not the host and not on guest list so they cannot edit event details (TextView)
                else {
                    TextView eventIdText = new TextView(this);
                    Log.d("event id error spot", String.valueOf(eventId));
                    eventIdText.setText(String.valueOf(eventId));
                    Log.d("event id after", String.valueOf(eventId));
                    page.addView(eventIdText);

                    TextView nameText = new TextView(this);
                    nameText.setText("Event Name: " + eventInfo.getName());
                    page.addView(nameText);

                    TextView hostText = new TextView(this);
                    hostText.setText("Host: " + eventInfo.getHost());
                    page.addView(hostText);

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
                }
            }
        }
    }
}