package com.carlolj.sailor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.carlolj.sailor.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    ActivityLoginBinding binding;
    Button btnLogIn;
    ImageView ibBack;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        btnLogIn = binding.btnLogIn;
        ibBack = binding.ibBack;
        etUsername = binding.etUsername;
        etPassword = binding.etPassword;

        btnLogIn.setOnClickListener(v -> {
            loginUser(etUsername.getText().toString(), etPassword.getText().toString());
        });

        ibBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        loginInBackground(username, password);
    }

    public void loginInBackground(String username, String password){
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //If the parse exception is null, means that executed correctly
                if (e!=null) {
                    Log.e(TAG, "Issue with login", e);
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Error signing in, check your connection/credentials")
                            .show();
                    return;
                }
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}