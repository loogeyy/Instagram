package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {
    private EditText etNewUsername;
    private EditText etNewPassword;
    private Button btnSignUp;
    private Button btnReturn;

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnReturn = findViewById(R.id.btnReturn);

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "return to log in screen");
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick log in button");
                String username = etNewUsername.getText().toString();
                String password = etNewPassword.getText().toString();
                createUser(username, password);

            }
        });


    }

    private void createUser(String username, String password) {
        Log.i(TAG, "Attempting to create user " + username + "...");
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        //initialized liked array here
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(SignUpActivity.this, "Error on sign-up!", Toast.LENGTH_SHORT);
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                // Navigate to main activity upon successful sign-in
                goNextActivity();
                Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_SHORT);
            }
        });
    }

    private void goNextActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish(); // user cannot go back to log-in screen
    }
}