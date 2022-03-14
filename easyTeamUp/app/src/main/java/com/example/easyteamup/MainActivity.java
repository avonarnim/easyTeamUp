package com.example.easyteamup;

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

import com.example.easyteamup.databinding.ActivityMainBinding;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private EditText eventNameEdt, eventHostEdt, eventLatEdt, eventLongEdt, eventDeadlineEdt;
    private Button addEventBtn;
    private Button signUpBtn;
    private DBHandler dbHandler;

    public Button viewEventButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing all our variables.
        eventNameEdt = findViewById(R.id.idEdtEventName);
        eventHostEdt = findViewById(R.id.idEdtEventHost);
        eventLatEdt = findViewById(R.id.idEdtEventLatitude);
        eventLongEdt = findViewById(R.id.idEdtEventLongitude);
        eventDeadlineEdt = findViewById(R.id.idEdtDeadline);
        addEventBtn = findViewById(R.id.idBtnAddCourse);
        viewEventButton = findViewById(R.id.idBtnCreateToViewEvent);
        signUpBtn = findViewById(R.id.idBtnSignUp);

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(MainActivity.this);

        viewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
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

                // validating if the text fields are empty or not.
                if (eventName.isEmpty() && eventHost.isEmpty() && latString.isEmpty() && longString.isEmpty() && eventDeadline.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewEvent(eventName, eventHost, eventLat, eventLong, eventDeadline);

                // after adding the data we are displaying a toast message.
                Toast.makeText(MainActivity.this, "Course has been added.", Toast.LENGTH_SHORT).show();
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