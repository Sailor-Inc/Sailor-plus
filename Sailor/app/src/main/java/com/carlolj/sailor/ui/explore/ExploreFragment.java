package com.carlolj.sailor.ui.explore;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.CreateActivity;
import com.carlolj.sailor.controllers.AlertDialogHelper;
import com.carlolj.sailor.databinding.FragmentExploreBinding;
import com.carlolj.sailor.models.Follows;
import com.carlolj.sailor.models.Location;
import com.carlolj.sailor.models.Post;
import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.SphericalUtil;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ExploreFragment extends Fragment {

    public static final String TAG = "ExploreFragment";
    public static final int LOCATION_REQUEST_CODE = 1;
    private List<Location> locations;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    boolean isOpen, isFiltering = false;
    private FusedLocationProviderClient mLocationClient;
    private FrameLayout infoContainer;
    private LocationDetailsFragment locationDetailsFragment;

    RotateLayout rlTopLocations, rlDistance, rlFriends;
    FloatingActionButton fabAdd, fabMore, fabFilter, fabFriends, fabTopLocations, fabDistance;
    Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise, fabOpen2, fabClose2, fabClockwise2, fabAntiClockwise2;

    private FragmentExploreBinding binding;

    public ExploreFragment() {
    }

    /**
     * When the fragment view is created this method gets called, this method initializes the google map
     *
     * @param inflater           the layout inflater
     * @param container          the ViewGroup container
     * @param savedInstanceState the bundle of the savedInstanceState
     * @return the returned view
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.googleMap);

        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    map = googleMap;
                }
            });
        } else {
            Toast.makeText(getContext(), "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
        return root;
    }


    /**
     * This method gets called when the fragment view is created
     *
     * @param view               the current view
     * @param savedInstanceState the saved instance state
     */
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabMore = binding.fabMore;
        fabAdd = binding.fabAdd;
        fabFilter = binding.fabFilter;
        fabDistance = binding.fabDistance;
        fabFriends = binding.fabFriends;
        fabTopLocations = binding.fabTopLocations;
        rlTopLocations = binding.rlTopLocations;
        rlDistance = binding.rlDistance;
        rlFriends = binding.rlFriends;
        infoContainer = binding.infoContainer;

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim);
        fabClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise);
        fabAntiClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anticlockwise);
        fabOpen2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim2);
        fabClose2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim2);
        fabClockwise2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise2);
        fabAntiClockwise2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anticlockwise2);

        startLocationClient();
        mLocationClient = new FusedLocationProviderClient(getActivity());

        fabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    if (isFiltering) {
                        fabFilter.startAnimation(fabAntiClockwise2);
                        fabFriends.startAnimation(fabClose2);
                        fabTopLocations.startAnimation(fabClose2);
                        fabDistance.startAnimation(fabClose2);
                        rlDistance.startAnimation(fabClose2);
                        rlFriends.startAnimation(fabClose2);
                        rlTopLocations.startAnimation(fabClose2);

                        fabFriends.setClickable(false);
                        fabTopLocations.setClickable(false);
                        fabDistance.setClickable(false);

                        isFiltering = false;
                    }
                    fabMore.startAnimation(fabAntiClockwise);
                    fabAdd.startAnimation(fabClose);
                    fabFilter.startAnimation(fabClose);

                    fabAdd.setClickable(false);
                    fabFilter.setClickable(false);
                    isOpen = false;
                } else {
                    fabMore.startAnimation(fabClockwise);
                    fabAdd.startAnimation(fabOpen);
                    fabFilter.startAnimation(fabOpen);

                    fabAdd.setClickable(true);
                    fabFilter.setClickable(true);
                    isOpen = true;
                }
            }
        });

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen && !isFiltering) {
                    fabFilter.startAnimation(fabClockwise2);
                    fabFriends.startAnimation(fabOpen2);
                    fabTopLocations.startAnimation(fabOpen2);
                    fabDistance.startAnimation(fabOpen2);
                    rlDistance.startAnimation(fabOpen2);
                    rlFriends.startAnimation(fabOpen2);
                    rlTopLocations.startAnimation(fabOpen2);

                    fabFriends.setClickable(true);
                    fabTopLocations.setClickable(true);
                    fabDistance.setClickable(true);

                    isFiltering = true;
                } else {
                    if (isOpen && isFiltering) {
                        fabFilter.startAnimation(fabAntiClockwise2);
                        fabFriends.startAnimation(fabClose2);
                        fabTopLocations.startAnimation(fabClose2);
                        fabDistance.startAnimation(fabClose2);
                        rlDistance.startAnimation(fabClose2);
                        rlFriends.startAnimation(fabClose2);
                        rlTopLocations.startAnimation(fabClose2);

                        fabFriends.setClickable(false);
                        fabTopLocations.setClickable(false);
                        fabDistance.setClickable(false);

                        isFiltering = false;
                    }
                }
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateActivity.class);
                startActivity(intent);
            }
        });

        fabTopLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTopLocations();
            }
        });

        fabFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFriendsLocations();
            }
        });

        fabDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationCheckPermission()) {
                    getCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                }
            }
        });
    }

    /**
     * This method continuously updates the current user location to be capable of using filter by raidus functionalities
     */
    private void startLocationClient() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(60000*5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    /**
     * Call this method to update the current location
     */
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        mLocationClient = new FusedLocationProviderClient(getActivity());
        Task<android.location.Location> task = mLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location != null) {
                    goToLocation(location.getLatitude(), location.getLongitude());
                }
            }
        });
    }

    /**
     * This method gets automatically called when the permissions get accepted/rejected
     * @param requestCode an integer representing the expected code to receive
     * @param permissions the string of permissions
     * @param grantResults the integer telling the grantResults of the operation
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                AlertDialogHelper.alertOnlyTitle(
                        getContext(),
                        "Location permission not accepted!",
                        AlertDialogHelper.ERROR_TYPE);
            }
        }
    }

    /**
     * This method starts an alertDialog to start filtering by radius using the current location ia two parameters: latitude and longitude
     * @param latitude a double representing latitude
     * @param longitude a double representing longitude
     */
    private void goToLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View textenter = inflater.inflate(R.layout.dialog_add, null);
        final EditText userinput = (EditText) textenter.findViewById(R.id.etRadius);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(textenter)
                .setTitle("Filter by radius (1 = 1mi)");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                map.clear();
                int radius = Integer.parseInt(userinput.getText().toString());
                float zoom = 10;
                if (radius >= 2000) {
                    zoom = 3;
                    if (radius >= 4000) {
                        zoom = 1;
                    }
                } else {
                    float calculation = 0;
                    calculation = radius/25;
                    if (calculation >= 4) {
                        calculation = 3+radius/200;
                        if (calculation >= 7) {
                            calculation = (float) (5 + radius/800);
                        }
                    }
                    zoom = zoom - calculation;
                    Log.d("Zoom" , zoom+"");
                }
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
                map.moveCamera(cameraUpdate);

                if (radius > 7500) {
                    radius = 7500;
                    Toast.makeText(getContext(),
                            "Your radius is bigger than allowed, making it 7500miles",
                            Toast.LENGTH_SHORT).show();
                }
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latLng);
                circleOptions.radius(radius*1609);
                circleOptions.strokeColor(Color.BLACK);
                circleOptions.fillColor(0x30ff0000);
                circleOptions.strokeWidth(2);
                map.addCircle(circleOptions);

                getRadiusLocations(latLng, radius);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    /**
     * This method is part of the fragment life cycle and gets called to destrow the current view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * This method will load all the marker positions and set on click listeners to each marker in the map to open a new fragment to see
     * a certain place posts
     */
    protected void loadMap() throws ParseException {
        if (map != null) {
            for (int i = 0; i < locations.size(); i++){
                LatLng markerPosition = new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude());
                String locationUniqueId = locations.get(i).getObjectId();
                map.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .title(locations.get(i).getName())
                        .snippet(locationUniqueId));
            }
            map.getMinZoomLevel();
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    map.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    String locationUniqueId = marker.getSnippet();
                    String locationTitle = marker.getTitle();

                    openLocationDetailsFromUniqueId(locationUniqueId);
                    return false;
                }
            });
        } else {
            Toast.makeText(getContext(), "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This method returns the top 20 most top-ed locations registered in the Sailor+ database
     */
    private void getTopLocations() {
        final SweetAlertDialog pDialog = AlertDialogHelper.alertStartSpin(getContext());
        map.clear();
        if (locations != null) {
            locations.clear();
        }
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.include(Location.KEY_GMAPS_ID);
        query.orderByDescending("topsNumber");
        query.setLimit(20);
        query.findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> receivedLocations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting locations", e);
                    return;
                }
                if (receivedLocations != null) {
                    locations = new ArrayList<>();
                    locations.addAll(receivedLocations);
                    try {
                        loadMap();
                        AlertDialogHelper.alertStopSpin(pDialog);
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "There are no locations! be the first one to add a pin!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Run this method to get the friends visitedLocations, if the user doesn't have friends
     * it will show a Dialog saying that the user is not following anyone
     */
    private void getFriendsLocations() {
        final SweetAlertDialog pDialog = AlertDialogHelper.alertStartSpin(getContext());
        map.clear();
        if (locations != null) {
            locations.clear();
        }
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        query.whereEqualTo(Follows.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<Follows>() {
            @Override
            public void done(Follows object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting follows object", e);
                    AlertDialogHelper.alertStopSpin(pDialog);
                    return;
                }
                List<ParseUser> friends = object.getFollowing();
                if (friends!=null) {
                    HashMap<Location, Integer> friendsLocations = new HashMap<>();
                    for (ParseUser friend : friends) {
                        try {
                            List<Location> locations = friend.fetchIfNeeded().getList("visitedLocations");
                            if (locations != null) {
                                for (Location location : locations) {
                                    friendsLocations.put(location,1);
                                }
                            }
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                    }
                    locations = new ArrayList<>();
                    List<Location> keyList = new ArrayList<Location>(friendsLocations.keySet());
                    Log.d("Showing friends locations" , keyList+"");
                    locations.addAll(keyList);
                    try {
                        loadMap();
                        AlertDialogHelper.alertStopSpin(pDialog);
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                } else {
                    AlertDialogHelper.alertStopSpin(pDialog);
                    AlertDialogHelper.alertOnlyTitle(getContext(),
                            "Sorry, you don't follow anyone :(",
                            AlertDialogHelper.NORMAL_TYPE);
                }
            }
        });
    }

    /**
     * Run this method to ask the user to enter a radius value and filter locations by that radius
     * @param latLngUser the current LatLang object of the user location
     * @param radius the radius to search for locations
     */
    private void getRadiusLocations(LatLng latLngUser, int radius) {
        if (locations != null) {
            locations.clear();
        }
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.include(Location.KEY_GMAPS_ID);
        query.findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> receivedLocations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting locations", e);
                }
                if (receivedLocations != null) {
                    locations = new ArrayList<>();
                    for (Location location : receivedLocations) {
                        LatLng locationLatLng = new LatLng(0,0);
                        try {
                            locationLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        if (isInRadius(latLngUser, locationLatLng, radius)) {
                            locations.add(location);
                        }
                    }
                    try {
                        loadMap();
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "There are no locations! be the first one to add a pin!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Run this method to check between two LatLng objects in a certain radius
     * @param latLngUser the current LatLang object of the user location
     * @param locationLatLng the current LatLang object of the selected location
     * @param radius integer containing the radius to compare
     * @return
     */
    private boolean isInRadius(LatLng latLngUser, LatLng locationLatLng, int radius) {
        if (latLngUser != null && locationLatLng != null) {
            int distance = (int) SphericalUtil.computeDistanceBetween(latLngUser,locationLatLng);
            Log.d("Distance " , distance/1609 + " : " + radius);
            return distance/1609<=radius;
        }
        return false;
    }

    /**
     * Check if the app has the location permissions
     * @return
     */
    private boolean locationCheckPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * Open a new Info fragment with the data of the selected location
     */
    private void openLocationDetailsFromUniqueId(String locationUniqueId) {
        infoContainer.setVisibility(View.VISIBLE);
        ParseQuery<Location> query  =ParseQuery.getQuery(Location.class);
        query.whereEqualTo("objectId", locationUniqueId);
        query.getFirstInBackground(new GetCallback<Location>() {
            @Override
            public void done(Location location, ParseException e) {
                if (e != null) {
                    return;
                }
                if (location != null) {
                    ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                    query.include("location");
                    query.whereEqualTo("location", location);
                    query.getFirstInBackground(new GetCallback<Post>() {
                        @Override
                        public void done(Post object, ParseException e) {
                            if (object == null) {
                                return;
                            }
                            if (e != null) {
                                return;
                            }
                            String imgUrl = object.getLocationImage().getUrl();
                            locationDetailsFragment = new LocationDetailsFragment(location, imgUrl);

                            getChildFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.bottom_up_fragment, R.anim.bottom_down_fragment)
                                    .replace(R.id.infoContainer, locationDetailsFragment)
                                    .commit();
                        }
                    });
                }
            }
        });
    }
}