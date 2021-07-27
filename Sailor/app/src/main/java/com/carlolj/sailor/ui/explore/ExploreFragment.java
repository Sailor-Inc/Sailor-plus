package com.carlolj.sailor.ui.explore;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.CreateActivity;
import com.carlolj.sailor.activities.MainActivity;
import com.carlolj.sailor.databinding.FragmentExploreBinding;
import com.carlolj.sailor.models.Follows;
import com.carlolj.sailor.models.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ExploreFragment extends Fragment {

    public static final String TAG = "ExploreFragment";
    private List<Location> locations;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    boolean isPermissionGranted, isOpen, isFiltering = false;
    private FusedLocationProviderClient mLocationClient;

    FloatingActionButton fabAdd, fabMore, fabFilter, fabFriends, fabTopLocations, fabDistance;
    Animation fabOpen, fabClose, fabClockwise, fabAntiClockwise, fabOpen2, fabClose2, fabClockwise2, fabAntiClockwise2;

    private FragmentExploreBinding binding;

    public ExploreFragment() {
    }

    /**
     * When the fragment view is created this method gets called, this method initializes the google map
     * @param inflater the layout inflater
     * @param container the ViewGroup container
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
     * @param view the current view
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

        fabOpen = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim);
        fabClose = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim);
        fabClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise);
        fabAntiClockwise = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anticlockwise);
        fabOpen2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_open_anim2);
        fabClose2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_close_anim2);
        fabClockwise2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_clockwise2);
        fabAntiClockwise2 = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_anticlockwise2);

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
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }
            }
        });
    }

    private void getCurrentLocation() {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude,longitude);
        Log.d("Hello", "I'm here'");
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        map.moveCamera(cameraUpdate);
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
                    String locationUniqueId = marker.getSnippet();
                    String locationTitle = marker.getTitle();

                    Fragment fragment = PinFragment.newInstance(locationUniqueId, locationTitle);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .addToBackStack(null)
                            .commit();

                    Log.d("ExploreFragment ", locationUniqueId);
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
                }
                if (receivedLocations != null) {
                    locations = new ArrayList<>();
                    locations.addAll(receivedLocations);
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

    private void getFriendsLocations() {
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
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean locationCheckPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }
}