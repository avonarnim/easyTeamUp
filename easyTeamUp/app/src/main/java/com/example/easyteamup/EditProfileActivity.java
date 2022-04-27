package com.example.easyteamup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    LinearLayout page;
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(com.example.easyteamup.EditProfileActivity.this);
        page = findViewById(R.id.linearLayout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("username");
            password = extras.getString("password");

            TextView user = new TextView(this);
            user.setText("Username:");
            page.addView(user);
            EditText usernameText = new EditText(this);
            usernameText.setText(username);
            page.addView(usernameText);

            TextView passwordTitle = new TextView(this);
            passwordTitle.setText("Password:");
            page.addView(passwordTitle);
            EditText passwordText = new EditText(this);
            passwordText.setText(String.valueOf(password));
            page.addView(passwordText);

            Button saveChanges = new Button(this);
            saveChanges.setText("Save Changes");
            saveChanges.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            saveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO
                    String newUsername = usernameText.getText().toString();
                    String newPassword = passwordText.getText().toString();
                    // send updated event info to the database
                    dbHandler.updateProfile(username, newUsername, newPassword);

                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    intent.putExtra("username",newUsername);
                    startActivity(intent);
                }
            });
            page.addView(saveChanges);
        }
    }
}



