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
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private Button signUpBtn;
    private EditText usernameEdt;
    private EditText passwordEdt;
    private Button logInBtn;
    private EditText usernameLoginEdt;
    private EditText passwordLoginEdt;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpBtn = findViewById(R.id.signUpButton);
        usernameEdt = findViewById(R.id.username);
        passwordEdt = findViewById(R.id.password);

        logInBtn = findViewById(R.id.logInButton);
        usernameLoginEdt = findViewById(R.id.usernameLogIn);
        passwordLoginEdt = findViewById(R.id.passwordLogIn);

        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = new DBHandler(SignUpActivity.this);

        // below line is to add on click listener for our add course button.
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String username = usernameEdt.getText().toString();
                String password = passwordEdt.getText().toString();

                // validating if the text fields are empty or not.
                if (username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                dbHandler.addNewProfile(username, password);

                // after adding the data we are displaying a toast message.
                Toast.makeText(SignUpActivity.this, "Profile has been created!", Toast.LENGTH_SHORT).show();
                usernameEdt.setText("");
                passwordEdt.setText("");

                // go to profile page
                Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                intent.putExtra("username",username);
                intent.putExtra("password", password);
                startActivity(intent);
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // below line is to get data from all edit text fields.
                String username = usernameLoginEdt.getText().toString();
                String password = passwordLoginEdt.getText().toString();

                // validating if the text fields are empty or not.
                if (username.isEmpty() && password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }

                // on below line we are calling a method to add new
                // course to sqlite data and pass all our values to it.
                if (dbHandler.verifyProfile(username, password)) {
                    // after validating the profile we are displaying a toast message.
                    Toast.makeText(SignUpActivity.this, "You've been logged in!", Toast.LENGTH_SHORT).show();
                    usernameEdt.setText("");
                    passwordEdt.setText("");

                    // go to profile page
                    Intent intent = new Intent(SignUpActivity.this, ProfileActivity.class);
                    intent.putExtra("username",username);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, "Please re-enter your username and password!", Toast.LENGTH_SHORT).show();
                    usernameEdt.setText("");
                    passwordEdt.setText("");
                }
            }
        });
    }
}