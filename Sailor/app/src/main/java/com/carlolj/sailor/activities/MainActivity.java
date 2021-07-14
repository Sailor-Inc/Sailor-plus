package com.carlolj.sailor.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.carlolj.sailor.R;
import com.carlolj.sailor.ui.feed.FeedFragment;
import com.carlolj.sailor.ui.explore.ExploreFragment;
import com.carlolj.sailor.ui.profile.ProfileFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.carlolj.sailor.databinding.ActivityMainBinding;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    final FragmentManager fragmentManager = getSupportFragmentManager();

    final Fragment feed = new FeedFragment();
    final Fragment explore = new ExploreFragment();
    final Fragment profile = new ProfileFragment(ParseUser.getCurrentUser());

    private ActivityMainBinding binding;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bottomNavigation = findViewById(R.id.bottomNavigation);
        if (isServicesOK()) {
            bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.action_home:
                            fragment = feed;
                            break;
                        case R.id.action_compose:
                            //Check if google services can provide maps to the user
                            if (isServicesOK()) {
                                fragment = explore;
                            } else {
                                fragment = feed;
                            }
                            break;
                        default:
                            fragment = profile;
                            break;
                    }
                    fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                    return true;
                }
            });
            bottomNavigation.setSelectedItemId(R.id.action_home);
        }
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Sorry your device does not support google play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}