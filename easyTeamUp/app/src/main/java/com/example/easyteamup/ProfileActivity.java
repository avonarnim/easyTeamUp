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
//            TextView usernameText = (TextView)findViewById(R.id.profileUsername);
            TextView usernameText = new TextView(this);
            usernameText.setText(username);
            page.addView(usernameText);

            dbHandler.getPublicProfile(username);

            // TODO: insert view for profile picture

            TextView pastText = new TextView(this);
            pastText.setText("Past Events");
            page.addView(pastText);

            // TODO: insert views for past events

            TextView attendingText = new TextView(this);
            attendingText.setText("Upcoming Events (Attending)");
            page.addView(attendingText);

            // TODO: insert views for upcoming attending events

            TextView hostingText = new TextView(this);
            hostingText.setText("Upcoming Events (Hosting)");
            page.addView(hostingText);

            // TODO: insert views for upcoming hosting events

        }
    }
}