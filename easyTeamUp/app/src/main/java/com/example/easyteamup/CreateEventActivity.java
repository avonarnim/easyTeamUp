package com.example.easyteamup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String username, privacy = "Private";
    private EditText eventNameEdt, eventLatEdt, eventLongEdt, dateTxtDL, timeTxtDL, dateTxtTS1, timeTxtTS1, dateTxtTS2, timeTxtTS2, dateTxtTS3, timeTxtTS3, guestsEdt;
    private Button addEventBtn, profileBtn, viewEventButton, datePickerBtnDL, timePickerBtnDL, datePickerBtnTS1, timePickerBtnTS1, datePickerBtnTS2, timePickerBtnTS2, datePickerBtnTS3, timePickerBtnTS3, btn_guest;
    private DBHandler dbHandler;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ArrayList<String> guests;
    private TextView addedGuests;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

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
        //deadline
        datePickerBtnDL = findViewById(R.id.dl_btn_date);
        timePickerBtnDL = findViewById(R.id.dl_btn_time);
        dateTxtDL = findViewById(R.id.dl_in_date);
        timeTxtDL = findViewById(R.id.dl_in_time);
        //timeslot1
        datePickerBtnTS1 = findViewById(R.id.btn_dateTS1);
        timePickerBtnTS1 = findViewById(R.id.btn_timeTS1);
        dateTxtTS1 = findViewById(R.id.in_dateTS1);
        timeTxtTS1 = findViewById(R.id.in_timeTS1);
        //timeslot2
        datePickerBtnTS2 = findViewById(R.id.btn_dateTS2);
        timePickerBtnTS2 = findViewById(R.id.btn_timeTS2);
        dateTxtTS2 = findViewById(R.id.in_dateTS2);
        timeTxtTS2 = findViewById(R.id.in_timeTS2);
        //timeslot3
        datePickerBtnTS3 = findViewById(R.id.btn_dateTS3);
        timePickerBtnTS3 = findViewById(R.id.btn_timeTS3);
        dateTxtTS3 = findViewById(R.id.in_dateTS3);
        timeTxtTS3 = findViewById(R.id.in_timeTS3);
        addEventBtn = findViewById(R.id.idBtnCreateEvent);
        viewEventButton = findViewById(R.id.idBtnCreateToViewEvent);
        profileBtn = findViewById(R.id.idBtnCreateToProfile);
        //btn_guest = findViewById(R.id.btn_guest);
        //guestsEdt = findViewById(R.id.idEdtEventGuest);
        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(CreateEventActivity.this);
        guests = new ArrayList<>();
        addedGuests = (TextView) findViewById(R.id.text_added_guests);
        listView = findViewById(R.id.list);
        String[] usernames = dbHandler.getAllUsers();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, usernames);
        listView.setAdapter(arrayAdapter);
        SearchView searchView = (SearchView) findViewById(R.id.floating_search_view);
        searchView.setQueryHint("Type Guest Username");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.setQuery(arrayAdapter.getItem(position), false);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!guests.contains(query)) {
                    guests.add(query);
                    Log.i("GUESTS", "added guest " + query + " guests total: " + guests.size());
                }
                searchView.setQuery("", false);
                searchView.clearFocus();
                arrayAdapter.getFilter().filter(null);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equals(""))
                    arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
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
        datePickerBtnTS2.setOnClickListener(new View.OnClickListener() {
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

                                dateTxtTS2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        timePickerBtnTS2.setOnClickListener(new View.OnClickListener() {
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

                                timeTxtTS2.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        datePickerBtnTS3.setOnClickListener(new View.OnClickListener() {
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

                                dateTxtTS3.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        timePickerBtnTS3.setOnClickListener(new View.OnClickListener() {
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

                                timeTxtTS3.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        // below line is to add on click listener for our add event button.
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
                Log.i("CREATE EVENT", "string date deadline: " + dateDL);
                String timeDL = timeTxtDL.getText().toString();
                Log.i("CREATE EVENT", "string time deadline: " + timeDL);
                String dateTS1 = dateTxtTS1.getText().toString();
                String timeTS1 = timeTxtTS1.getText().toString();
                String dateTS2 = dateTxtTS2.getText().toString();
                String timeTS2 = timeTxtTS2.getText().toString();
                String dateTS3 = dateTxtTS3.getText().toString();
                String timeTS3 = timeTxtTS3.getText().toString();

                String eventDeadline = dateDL + " " + timeDL;
                Log.i("CREATE EVENT", "string total deadline: " + eventDeadline);

                String timeSlot1 = dateTS1+ " " + timeTS1;
                String timeSlot2 = dateTS2+ " " + timeTS2;
                String timeSlot3 = dateTS3+ " " + timeTS3;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
                Date parsedDateTimeDL;
                Date parsedDateTimeTS1;
                Date parsedDateTimeTS2;
                Date parsedDateTimeTS3;
                try {
                    parsedDateTimeDL = dateFormat.parse(eventDeadline);
                    Log.i("CREATE EVENT", "parsed datetime deadline: " + parsedDateTimeDL);
                    parsedDateTimeTS1 = dateFormat.parse(timeSlot1);
                    parsedDateTimeTS2 = dateFormat.parse(timeSlot2);
                    parsedDateTimeTS3 = dateFormat.parse(timeSlot3);
                    if (parsedDateTimeDL.compareTo(parsedDateTimeTS1) > 1 || parsedDateTimeDL.compareTo(parsedDateTimeTS2) > 1 || parsedDateTimeDL.compareTo(parsedDateTimeTS3) > 1)
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    Toast.makeText(CreateEventActivity.this, "Make sure the Deadline is before all Timeslots...", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }catch (ParseException e) {
                    Toast.makeText(CreateEventActivity.this, "Encountered an error while parsing the selected date and time..", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
                Long eventDeadlineLong = parsedDateTimeDL.getTime();
                Log.i("CREATE EVENT", "Long deadline: " + String.valueOf(eventDeadlineLong));

                Long timeSlot1Long = parsedDateTimeTS1.getTime();
                Long timeSlot2Long = parsedDateTimeTS2.getTime();
                Long timeSlot3Long = parsedDateTimeTS3.getTime();

                // TODO: convert the selected time to unix timestamp

                // validating if the text fields are empty or not.
                if (eventName.isEmpty() && eventHost.isEmpty() && latString.isEmpty() && longString.isEmpty() && eventDeadline.isEmpty()) {
                    Toast.makeText(CreateEventActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // event to sqlite data and pass all our values to it.
                int id = dbHandler.addNewEvent(eventName, eventHost, eventLat, eventLong, eventDeadlineLong, privacy);
                dbHandler.addNewTimeslot(id, eventHost, timeSlot1Long); //getting event ID once its made
                dbHandler.addNewTimeslot(id, eventHost, timeSlot2Long);
                dbHandler.addNewTimeslot(id, eventHost, timeSlot3Long);
                for (String guest : guests) {
                    Log.i("GUESTS", "adding pt 2");
                    dbHandler.addNewMessage(eventHost, guest, "INVITE " + id + " " + eventName);
                    dbHandler.addGuestToGuestList(id, guest);
                }

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
                dateTxtTS2.setText("");
                timeTxtTS2.setText("");
                dateTxtTS3.setText("");
                timeTxtTS3.setText("");
                guestsEdt.setText("");

                Intent intent = new Intent(CreateEventActivity.this, ProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radio_private:
                if (checked)
                    privacy = "Private";
                    break;
            case R.id.radio_public:
                if (checked)
                    privacy = "Public";
                    break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}

/**
 * <Button
 *         android:layout_width="wrap_content"
 *         android:layout_height="wrap_content"
 *         android:text="Add Guest"
 *         android:id="@+id/btn_guest"
 *         android:layout_below="@+id/idEdtEventGuest"
 *         android:layout_alignLeft="@+id/btn_dateTS2"
 *         android:layout_alignStart="@+id/btn_dateTS2" />
 */