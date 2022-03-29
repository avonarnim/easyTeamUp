package com.example.easyteamup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String username;
    private EditText eventNameEdt, eventLatEdt, eventLongEdt, dateTxtDL, timeTxtDL, dateTxtTS1, timeTxtTS1;
    private Button addEventBtn, profileBtn, viewEventButton, datePickerBtnDL, timePickerBtnDL, datePickerBtnTS1, timePickerBtnTS1;
    private DBHandler dbHandler;
    private int mYear, mMonth, mDay, mHour, mMinute;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
        }

        // initializing all our variables.
        eventNameEdt = findViewById(R.id.idEdtEventName);
        eventLatEdt = findViewById(R.id.idEdtEventLatitude);
        eventLongEdt = findViewById(R.id.idEdtEventLongitude);
        datePickerBtnDL = findViewById(R.id.dl_btn_date);
        timePickerBtnDL = findViewById(R.id.dl_btn_time);
        dateTxtDL = findViewById(R.id.dl_in_date);
        timeTxtDL = findViewById(R.id.dl_in_time);
        datePickerBtnTS1 = findViewById(R.id.btn_dateTS1);
        timePickerBtnTS1 = findViewById(R.id.btn_timeTS1);
        dateTxtTS1 = findViewById(R.id.in_dateTS1);
        timeTxtTS1 = findViewById(R.id.in_timeTS1);
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

        datePickerBtnDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateTxtDL.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        timePickerBtnDL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                timeTxtDL.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        datePickerBtnTS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateTxtTS1.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        timePickerBtnTS1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                timeTxtTS1.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        // below line is to add on click listener for our add course button.
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String eventName = eventNameEdt.getText().toString();
                String eventHost = username;
                String latString = eventLatEdt.getText().toString();
                String longString = eventLongEdt.getText().toString();
                Double eventLat = new Double(latString);
                Double eventLong = new Double(longString);
                String dateDL = dateTxtDL.getText().toString();
                String timeDL = timeTxtDL.getText().toString();
                String dateTS1 = dateTxtTS1.getText().toString();
                String timeTS1 = timeTxtTS1.getText().toString();

                String eventDeadline = dateDL + " " + timeDL;
                String timeSlot1 = dateTS1+ " " + timeTS1;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-m-yyyy hh:mm");
                Date parsedDateTimeDL;
                Date parsedDateTimeTS1;
                try {
                    parsedDateTimeDL = dateFormat.parse(eventDeadline);
                    parsedDateTimeTS1 = dateFormat.parse(timeSlot1);
                } catch (ParseException e) {
                    Toast.makeText(CreateEventActivity.this, "Encountered an error while parsing the selected date and time..", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                Long eventDeadlineLong = parsedDateTimeDL.getTime();
                Long timeSlot1Long = parsedDateTimeTS1.getTime();

                // TODO: convert the selected time to unix timestamp

                // validating if the text fields are empty or not.
                if (eventName.isEmpty() && eventHost.isEmpty() && latString.isEmpty() && longString.isEmpty() && eventDeadline.isEmpty()) {
                    Toast.makeText(CreateEventActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                int id = dbHandler.addNewEvent(eventName, eventHost, eventLat, eventLong, eventDeadlineLong);
//                dbHandler.addNewTimeslot(id, eventHost, timeSlot1Long); //getting event ID once its made
                // after adding the data we are displaying a toast message.
                Toast.makeText(CreateEventActivity.this, "Event has been added.", Toast.LENGTH_SHORT).show();
                eventNameEdt.setText("");
                eventLatEdt.setText("");
                eventLongEdt.setText("");
                dateTxtDL.setText("");
                timeTxtDL.setText("");
                dateTxtTS1.setText("");
                timeTxtTS1.setText("");

                Intent intent = new Intent(CreateEventActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
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
