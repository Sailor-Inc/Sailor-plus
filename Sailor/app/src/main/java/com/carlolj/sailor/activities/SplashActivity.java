package com.carlolj.sailor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.ActivityMainBinding;
import com.carlolj.sailor.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 2200;
    Animation topAnimation, bottomAnimation;
    ActivitySplashBinding binding;
    ImageView ivBoat;
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ivBoat = binding.ivBoat;
        tvTitle = binding.tvTitle;

        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        ivBoat.setAnimation(topAnimation);
        tvTitle.setAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}