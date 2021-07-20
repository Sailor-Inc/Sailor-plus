package com.carlolj.sailor.ui.explore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.CreateActivity;
import com.carlolj.sailor.databinding.FragmentExploreBinding;
import com.carlolj.sailor.models.Location;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExploreFragment extends Fragment{

    public static final String TAG = "ExploreFragment";
    private List<Location> locations;
    private GoogleMap map;
    private SupportMapFragment mapFragment;

    FloatingActionButton fabAdd;
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
                    getTopLocations(googleMap);
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

        fabAdd = binding.fabAdd;

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateActivity.class);
                startActivity(intent);
            }
        });
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
    protected void loadMap() {
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
     * This method returns the top locations registered in the Sailor+ database
     * @param googleMap a initialized google map.
     */
    private void getTopLocations(GoogleMap googleMap) {
        map = googleMap;
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.include(Location.KEY_GMAPS_ID);
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
                    loadMap();
                } else {
                    Toast.makeText(getContext(), "There are no locations! be the first one to add a pin!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}