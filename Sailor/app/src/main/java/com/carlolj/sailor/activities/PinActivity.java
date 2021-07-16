package com.carlolj.sailor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.ActivityPinBinding;
import com.carlolj.sailor.databinding.ActivityRegisterBinding;
import com.carlolj.sailor.databinding.FragmentExploreBinding;

public class PinActivity extends AppCompatActivity {

    ActivityPinBinding binding;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        tvTitle = binding.tvTitle;

        String locationUniqueId = getIntent().getStringExtra("locationUniqueId");
        String locationTitle = getIntent().getStringExtra("locationTitle");

        tvTitle.setText(locationTitle);

    }
}