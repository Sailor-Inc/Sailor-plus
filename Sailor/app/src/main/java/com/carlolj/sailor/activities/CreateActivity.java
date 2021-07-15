package com.carlolj.sailor.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.carlolj.sailor.BuildConfig;
import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.ActivityCreateBinding;
import com.carlolj.sailor.databinding.ActivityLoginBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class CreateActivity extends AppCompatActivity {

    ActivityCreateBinding binding;
    EditText etGooglePlacesSearch;
    ImageView ivPostImage;
    String id;

    /**
     * When the activity is created we initialize all the components inside
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        etGooglePlacesSearch = binding.etGooglePlacesSearch;
        ivPostImage = binding.ivPostImage;

        //Initialize google places
        Places.initialize(getApplicationContext(), BuildConfig.GMAPS_KEY);
        ivPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //Set edittext non focusable
        etGooglePlacesSearch.setFocusable(false);
        etGooglePlacesSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ID);
                //Create intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        ,fieldList).build(CreateActivity.this);
                //Start activity result
                startActivityForResult(intent, 100);
            }
        });
    }

    /**
     * When we get an activity result this method gets automatically called
     * @param requestCode the code we want to receive
     * @param resultCode the result thrown by the activity result (error or ok)
     * @param data data of the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //When the result is success
            //Initialize place
            Place place = Autocomplete.getPlaceFromIntent(data);
            id = place.getId();
            etGooglePlacesSearch.setText(place.getName());
        }else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            //When there is an error
            //Initialize status
            Status status = Autocomplete.getStatusFromIntent(data);
            //Display toast
            Toast.makeText(getApplicationContext(), status.getStatusMessage()
                    ,Toast.LENGTH_SHORT).show();
        }
    }
}