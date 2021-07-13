package com.carlolj.sailor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.carlolj.sailor.databinding.ActivityRegisterBinding;
import com.carlolj.sailor.databinding.ActivityStartBinding;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    Button btnNext;
    ImageView ibBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        btnNext = binding.btnNext;
        ibBack = binding.ibBack;

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}