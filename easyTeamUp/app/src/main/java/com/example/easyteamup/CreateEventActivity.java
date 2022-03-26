package com.example.easyteamup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.easyteamup.databinding.ActivityMainBinding;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateEventActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String username;
    private EditText eventNameEdt, eventHostEdt, eventLatEdt, eventLongEdt, eventDeadlineEdt;
    private Button addEventBtn, profileBtn, viewEventButton;
    private DBHandler dbHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // initializing all our variables.
        eventNameEdt = findViewById(R.id.idEdtEventName);
        eventHostEdt = findViewById(R.id.idEdtEventHost);
        eventLatEdt = findViewById(R.id.idEdtEventLatitude);
        eventLongEdt = findViewById(R.id.idEdtEventLongitude);
        eventDeadlineEdt = findViewById(R.id.idEdtDeadline);
        addEventBtn = findViewById(R.id.idBtnCreateEvent);
        viewEventButton = findViewById(R.id.idBtnCreateToViewEvent);
        profileBtn = findViewById(R.id.idBtnCreateToProfile);

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(CreateEventActivity.this);

        viewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, MapsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateEventActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        // below line is to add on click listener for our add course button.
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String eventName = eventNameEdt.getText().toString();
                String eventHost = eventHostEdt.getText().toString();
                String latString = eventLatEdt.getText().toString();
                String longString = eventLongEdt.getText().toString();
                Double eventLat = new Double(latString);
                Double eventLong = new Double(longString);
                String eventDeadline = eventDeadlineEdt.getText().toString();
                // TODO: convert the selected time to unix timestamp
                Long eventDeadlineLong = new Long(eventDeadline);

                // validating if the text fields are empty or not.
                if (eventName.isEmpty() && eventHost.isEmpty() && latString.isEmpty() && longString.isEmpty() && eventDeadline.isEmpty()) {
                    Toast.makeText(CreateEventActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewEvent(eventName, eventHost, eventLat, eventLong, eventDeadlineLong);

                // after adding the data we are displaying a toast message.
                Toast.makeText(CreateEventActivity.this, "Course has been added.", Toast.LENGTH_SHORT).show();
                eventNameEdt.setText("");
                eventHostEdt.setText("");
                eventLatEdt.setText("");
                eventLongEdt.setText("");
                eventDeadlineEdt.setText("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}