package com.example.easyteamup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

            // if user is the host of this event AND event deadline has not passed, they are able to edit (EditText) the event
            if((eventInfo.getHost() == username) && (eventInfo.getDeadline() < System.currentTimeMillis())) {
                EditText eventIdText = new EditText(this);
                eventIdText.setText(eventId);
                page.addView(eventIdText);

                TextView hostText = new TextView(this);
                hostText.setText("Host: " + eventInfo.getHost());
                page.addView(hostText);

                EditText nameText = new EditText(this);
                nameText.setText("Event Name: " + eventInfo.getName());
                page.addView(nameText);

                EditText longitudeText = new EditText(this);
                longitudeText.setText("Longitude: " + eventInfo.getLongitude());
                page.addView(longitudeText);

                EditText latitudeText = new EditText(this);
                latitudeText.setText("Latitude: " + eventInfo.getLatitude());
                page.addView(latitudeText);

                EditText deadlineText = new EditText(this);
                deadlineText.setText("Deadline: " + eventInfo.getDeadline());
                page.addView(deadlineText);

                EditText finalTimeText = new EditText(this);
                finalTimeText.setText("Final Time: " + eventInfo.getFinalTime());
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

                        // TODO: loop through guest list and send them all messages
                        dbHandler.addNewMessage(eventInfo.getHost(), eventInfo.getHost(), "Changes have been made to event: " + name);
                    }
                });
            }
            else {
                // User is not the host and cannot edit event details (TextView)
                TextView eventIdText = new TextView(this);
                eventIdText.setText(eventId);
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