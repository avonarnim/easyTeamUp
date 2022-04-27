package com.example.easyteamup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    LinearLayout page;
    final int SELECT_PICTURE = 1;
    ImageView profilePic;
    String username;
    String password;
    byte[] picture;

    void selectImage() {
        // create an instance of the
        // intent of the type image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(this.getContentResolver(), imageUri));
                    } else {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    }
                    profilePic.setImageBitmap(bitmap);
                    dbHandler.uploadProfilePicture(username, picture);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHandler = new DBHandler(ProfileActivity.this);
        page = findViewById(R.id.linearLayout);

        //TODO: remove
        //dbHandler.addNewEvent("event", "test", 128.6, 128.6, 5000L);

        TextView titleText = new TextView(this);
        titleText.setText("Your Profile");
        page.addView(titleText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            password = extras.getString("password");
            TextView usernameText = new TextView(this);
            usernameText.setText("Username: " + username);
            page.addView(usernameText);

            Profile prof = dbHandler.getPublicProfile(username);

            // insert view for profile picture
            picture = dbHandler.getProfilePicture(username);
            profilePic = new ImageView(this);
            page.addView(profilePic);
            // show profile photo if one has been uploaded
            if (picture != null) {
                // show image
                Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                profilePic.setImageBitmap(bitmap);
            }
            // button to upload profile photo
            else {
                Button imageBtn = new Button(this);
                imageBtn.setText("Upload Profile Picture");
                imageBtn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                page.addView(imageBtn);

                imageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                        page.removeView(imageBtn);
                    }
                });
            }

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
                        intent.putExtra("eventId", eventId);
                        Log.v("eventId", String.valueOf(eventId));
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
            Log.i("WITHDRAW", "num messages: " + messages.size());
            for (int i = 0; i < messages.size(); i++) {
                TextView textView = new TextView(this);
                String msg = messages.get(i).getBody();
                if (msg.substring(0,6).equals("INVITE")) {
                    Log.i("INVITE 1", msg);
                    Integer instanceOfSpace = msg.indexOf(" ");
                    Integer instanceOfSpace2 = msg.indexOf(" ", instanceOfSpace+1);
                    Log.i("INVITE 1b", instanceOfSpace + " " + instanceOfSpace2);
                    int eventId = Integer.valueOf(msg.substring(instanceOfSpace+1, instanceOfSpace2));
                    Log.i("INVITE 2", "" + eventId);
                    String eventName = msg.substring(instanceOfSpace2+1);
                    Log.i("INVITE 3", eventName);
                    textView.setText("FROM: " + messages.get(i).getSender() + "\nCome to " + eventName);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ProfileActivity.this, EventPageActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("eventId", eventId);
                            startActivity(intent);
                        }
                    });
                } else {
                    textView.setText("FROM: " + messages.get(i).getSender() + "\n" + messages.get(i).getBody());
                }
                page.addView(textView);
            }

            TextView createEvent = new TextView(this);
            createEvent.setText("Create Event");
            createEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, CreateEventActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            });
            page.addView(createEvent);

            TextView viewMaps = new TextView(this);
            viewMaps.setText("View Map");
            viewMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            });
            page.addView(viewMaps);

            TextView signOut = new TextView(this);
            signOut.setText("Sign Out");
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, SignUpActivity.class);
                    intent.removeExtra("username");
                    startActivity(intent);
                }
            });
            page.addView(signOut);


            Button editProfile = new Button(this);
            editProfile.setText("Edit Profile");
            editProfile.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    startActivity(intent);
                }
            });
            page.addView(editProfile);
        }
    }
}