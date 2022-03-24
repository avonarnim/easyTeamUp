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
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    LinearLayout page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHandler = new DBHandler(ProfileActivity.this);
        page = findViewById(R.id.linearLayout);

        TextView titleText = new TextView(this);
        titleText.setText("Your Profile");
        page.addView(titleText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String username = extras.getString("username");
            TextView usernameText = new TextView(this);
            usernameText.setText("Username: " + username);
            page.addView(usernameText);

            Profile prof = dbHandler.getPublicProfile(username);

            // TODO: insert view for profile picture

            TextView pastText = new TextView(this);
            pastText.setText("Past Events");
            page.addView(pastText);

            // insert views for past events
            // https://medium.com/mindorks/creating-dynamic-layouts-in-android-d4008b72f2d
            // https://stackoverflow.com/questions/3328757/how-to-click-or-tap-on-a-textview-text
            List<Event> pastEvents = prof.getPastEvents();
            for (int i = 0; i < pastEvents.size(); i++) {
                TextView textView = new TextView(this);
                String eventName = pastEvents.get(i).getName();
                int eventId = pastEvents.get(i).getId();
                textView.setText(eventName);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileActivity.this, EventPageActivity.class);
                        // pass eventId and username to the event activity
                        intent.putExtra("eventId",eventId);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                });
                page.addView(textView);
            }

            TextView attendingText = new TextView(this);
            attendingText.setText("Upcoming Events (Attending)");
            page.addView(attendingText);

            // insert views for upcoming attending events
            List<Event> futureEvents = prof.getFutureEvents();
            for (int i = 0; i < futureEvents.size(); i++) {
                TextView textView = new TextView(this);
                String eventName = futureEvents.get(i).getName();
                int eventId = futureEvents.get(i).getId();
                textView.setText(eventName);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileActivity.this, EventPageActivity.class);
                        // pass eventId and username to the event activity
                        intent.putExtra("eventId",eventId);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                });
                page.addView(textView);
            }

            TextView hostingText = new TextView(this);
            hostingText.setText("Upcoming Events (Hosting)");
            page.addView(hostingText);

            // insert views for upcoming hosting events
            List<Event> currentlyHosting = prof.getCurrentlyHosting();
            for (int i = 0; i < currentlyHosting.size(); i++) {
                TextView textView = new TextView(this);
                String eventName = currentlyHosting.get(i).getName();
                int eventId = currentlyHosting.get(i).getId();
                textView.setText(eventName);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ProfileActivity.this, EventPageActivity.class);
                        // pass eventId and username to the event activity
                        intent.putExtra("eventId",eventId);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                });
                page.addView(textView);
            }

            // insert views for all Messages
            TextView messagesText = new TextView(this);
            messagesText.setText("Messages");
            page.addView(messagesText);

            ArrayList<Message> messages = dbHandler.getMessages(username);
            for (int i = 0; i < messages.size(); i++) {
                TextView textView = new TextView(this);
                textView.setText("FROM: " + messages.get(i).getSender() + "\n" + messages.get(i).getBody());
                page.addView(textView);
            }
        }
    }
}