package com.carlolj.sailor.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.BitmapScaler;
import com.carlolj.sailor.BuildConfig;
import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.ActivityCreateBinding;
import com.carlolj.sailor.databinding.ActivityLoginBinding;
import com.carlolj.sailor.models.Location;
import com.carlolj.sailor.models.Post;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to create a new post with a location selected via google places api
 */
public class CreateActivity extends AppCompatActivity {

    private static final int RESULT_PICK = 1;
    private static final int RESULT_TAKE = 2;
    private static final int ACCEPT_CAMERA = 20;
    private static final String TAG = "CreateActivity";
    ParseFile newProfileImage;

    ActivityCreateBinding binding;
    EditText etGooglePlacesSearch, etCaption;
    ImageView ivPostImage;
    Button btnNext;
    String id;
    double latitude = 0,longitude = 0;

    /**
     * When the activity is created we initialize all the components inside and the onClick methods
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
        btnNext = binding.btnNext;
        etCaption = binding.etCaption;

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude != 0 && longitude != 0 && id != null && newProfileImage != null && !etCaption.getText().toString().equals("")){
                    onSubmit();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill all the post fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Initialize google places
        Places.initialize(getApplicationContext(), BuildConfig.GMAPS_KEY);
        //Set on click listener in our post image
        ivPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseProfilePicture();
            }
        });
        //Set edittext non focusable
        etGooglePlacesSearch.setFocusable(false);
        //Set on click listener on the Google places edit text
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
     * This method gets called when the user wants to create a new Post of a certain location,
     * the method will do a query to check if the selected location already exists in the database,
     * in case the location exist it will create a new post associated to that location, in case
     * the location doesn't exist it will create a new location and associate a post to that new location
     */
    private void onSubmit() {
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.whereEqualTo("gmapsId", id);
        query.findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> objects, ParseException e) {
                if (e != null) {
                    //In case the callback gets an error
                    return;
                }
                if (objects.size() != 0) {
                    if (objects.size() == 1) {
                        setPostAndUpdateLocation(objects.get(0));
                    }
                    return;
                } else {
                    createNewLocationAndPost();
                }
            }
        });
    }

    /**
     * This method searches for a repeated google map id to add a visitedLocation inside the current user
     * @param locationCheck a Location object that we want to compare inside the curret user visitedLocations
     * @throws ParseException a Parse Exception
     */
    private void checkVisitedLocations(Location locationCheck) throws ParseException {
        List<Location> visitedLocations = ParseUser.getCurrentUser().getList("visitedLocations");
        if (visitedLocations != null) {
            int count = 0;
            boolean found = false;
            while (count<visitedLocations.size() && !found) {
                if (visitedLocations.get(count).fetchIfNeeded().get("gmapsId").equals(locationCheck.getMapsId())) {
                    found = true;
                }
                count++;
            }
            if (!found) {
                addUserNewLocation(locationCheck);
            }
        } else {
            addUserNewLocation(locationCheck);
        }
    }

    /**
     * This method adds a location object inside current user visitedLocations field
     * @param location the location object to be inserted
     */
    private void addUserNewLocation(Location location) {
        ParseUser.getCurrentUser().add("visitedLocations", location);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Location was not added to visitedLocations");
                }
                Log.d(TAG, "Location added to visitedLocations");
            }
        });
    }

    /**
     * When we get an activity result this method gets automatically called
     * @param requestCode the code we want to receive three different types supported 1(Pick photo),2(Take a photo),100(Get google place data)
     * @param resultCode the result thrown by the activity result (error or ok)
     * @param data data of the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();

                    Bitmap selectedImage = loadFromUri(selectedImageUri);
                    loadNewPhoto(selectedImage);
                } else { // Result was a failure
                    Toast.makeText(CreateActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");

                    loadNewPhoto(bitmapImage);
                } else { // Result was a failure
                    Toast.makeText(CreateActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
                break;
            case 100:
                if (resultCode == RESULT_OK) {
                    //When the result is success
                    //Initialize place
                    Place place = Autocomplete.getPlaceFromIntent(data);

                    id = place.getId();
                    latitude = place.getLatLng().latitude;
                    longitude = place.getLatLng().longitude;

                    etGooglePlacesSearch.setText(place.getName());
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    //When there is an error
                    //Initialize status
                    Status status = Autocomplete.getStatusFromIntent(data);
                    //Display toast
                    Toast.makeText(getApplicationContext(), status.getStatusMessage()
                            ,Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    /**
     * This method opens a alertDialog in the current activity and the user is capable of selecting
     * to take a photo or pick a photo from gallery
     */
    private void chooseProfilePicture() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateActivity.this);
        builder.setTitle(R.string.choose_camera_option)
                .setItems(R.array.camera_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                if (checkAndRequestPermissions()) {
                                    takePictureFromCamera();
                                    dialog.cancel();
                                }
                                break;
                            default:
                                takePictureFromGallery();
                                dialog.cancel();
                                break;
                        }
                    }
                });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method will check if the phone has the permission to access to the camera, if not
     * it will ask the user to accept the permissions
     * @return true if the app has camera permissions false if the user rejected the permission
     */
    private boolean checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(CreateActivity.this
                        ,new String[]{Manifest.permission.CAMERA}
                        ,ACCEPT_CAMERA);
                return false;
            }
        }
        return true;
    }

    /**
     * This method gets automatically called when the request permission dialog gets rejected or accepted
     * @param requestCode a static variable in the app defined by the programmer
     * @param permissions
     * @param grantResults if the permission is granted
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCEPT_CAMERA && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePictureFromCamera();
        } else {
            Toast.makeText(CreateActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method will launch a camera activity on the current activity and wait for a result using
     * start activity for result
     */
    private void takePictureFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(CreateActivity.this.getPackageManager()) != null) {
            startActivityForResult(takePicture, RESULT_TAKE);
        }
    }

    /**
     * This method will launch a gallery activity on the current activity and wait for a result using
     * start activity for result
     */
    private void takePictureFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, RESULT_PICK);
    }

    /**
     * This method receives a photo Uri and returns the Bitmap of the selected image
     * @param photoUri Uri of the selected photo
     * @return an image bitmap of the selected photo uri
     */
    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if (Build.VERSION.SDK_INT > 27) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(CreateActivity.this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(CreateActivity.this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * This method needs a Bitmap of an image to resize, compress and upload to the internet using Parse
     * once the file is uploaded the user can add the photo to his post
     * @param selectedImage a bitmap image te upload
     */
    public void loadNewPhoto(Bitmap selectedImage) {
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(selectedImage, 800);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        byte[] byteArray = stream.toByteArray();

        newProfileImage = new ParseFile("post.png", byteArray);

        newProfileImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Post image wasn't updated", e);
                    return;
                }
            }
        });

        ivPostImage.setBackground(null);
        Glide.with(getApplicationContext()).load(byteArray).circleCrop().into(ivPostImage);
    }

    /**
     * this method creates a new location and then creates a post referencing that new location, after
     * creating the post it automatically adds that post to the created location id
     */
    private void createNewLocationAndPost() {
        Location location = new Location();
        List emptyList = new ArrayList();
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setName(etGooglePlacesSearch.getText().toString());
        location.setMapsId(id);
        location.setLocationPostBy(emptyList);
        location.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Toast.makeText(CreateActivity.this, "Location was not created!" + e, Toast.LENGTH_SHORT).show();
                    return;
                }
                setPostAndUpdateLocation(location);
            }
        });
    }

    /**
     * This method creates a new post and adds that post to a received parse location object
     * @param location a location object already existent in The Parse Database
     */
    private void setPostAndUpdateLocation(Location location) {
        Post post = new Post();
        List emptyList = new ArrayList();
        post.setAuthor(ParseUser.getCurrentUser());
        post.setLocation(location);
        post.setLocationImage(newProfileImage);
        post.setCaption(etCaption.getText().toString());
        post.setToppedBy(emptyList);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(CreateActivity.this, "Post wasn't created!", Toast.LENGTH_SHORT).show();
                    return;
                }
                location.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Toast.makeText(CreateActivity.this, "Repeated location not refreshed!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        List list = null;
                        try {
                            list = location.getLocationPostBy();
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        list.add(post.getObjectId());
                        location.setLocationPostBy(list);
                        location.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Toast.makeText(CreateActivity.this, "Failed to insert post", Toast.LENGTH_SHORT).show();
                                }
                                try {
                                    checkVisitedLocations(location);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                                Toast.makeText(CreateActivity.this, "Your post was added to the location", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}