package com.carlolj.sailor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.carlolj.sailor.databinding.ActivityRegisterBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ActivityRegisterBinding binding;
    Button btnNext;
    ImageView ibBack;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        btnNext = binding.btnNext;
        ibBack = binding.ibBack;
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUpUser(etUsername.getText().toString(), etPassword.getText().toString());
            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void logUpUser(String username, String password) {
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    loginInBackground(username,password);
                } else {
                    Log.e(TAG, "Issue registering", e);
                    Toast.makeText(RegisterActivity.this, "Error registering, check your connection/credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginInBackground(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //If the parse exception is null, means that executed correctly
                if (e!=null) {
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                Toast.makeText(RegisterActivity.this, "Welcome! " + username, Toast.LENGTH_SHORT).show();
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}