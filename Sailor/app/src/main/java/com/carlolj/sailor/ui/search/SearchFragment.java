package com.carlolj.sailor.ui.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.carlolj.sailor.R;
import com.carlolj.sailor.adapters.ProfileAdapter;
import com.carlolj.sailor.adapters.SearchLocationsAdapter;
import com.carlolj.sailor.adapters.SearchPostsAdapter;
import com.carlolj.sailor.controllers.AlertDialogHelper;
import com.carlolj.sailor.databinding.FragmentSearchBinding;
import com.carlolj.sailor.models.Location;
import com.carlolj.sailor.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SearchFragment extends Fragment {

    public static final String TAG = "SearchFragment";
    RecyclerView rvPosts, rvLocations;
    FragmentSearchBinding binding;

    protected SearchPostsAdapter postsAdapter;
    protected SearchLocationsAdapter locationsAdapter;
    protected List<Location> allLocations;
    protected List<Post> allPosts;
    protected Button btnAll, btnTop10;
    boolean selectedAll = false, selectedTop10 = true;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            binding = FragmentSearchBinding.inflate(inflater, container, false);
            View root = binding.getRoot();
            return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = binding.rvPosts;
        rvLocations = binding.rvLocations;
        btnAll = binding.btnAll;
        btnTop10 = binding.btnTop10;

        allPosts = new ArrayList<>();
        allLocations = new ArrayList<>();

        postsAdapter = new SearchPostsAdapter(getContext(), allPosts, this);
        locationsAdapter = new SearchLocationsAdapter(getContext(), allLocations, this);

        rvPosts.setAdapter(postsAdapter);
        rvLocations.setAdapter(locationsAdapter);

        LinearLayoutManager layoutManager
            = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        rvLocations.setLayoutManager(layoutManager);
        rvPosts.setLayoutManager(layoutManager2);

        btnTop10.setAlpha((float) .1);
        btnTop10.setScaleX((float) .8);
        btnTop10.setScaleY((float) .8);
        btnTop10.setClickable(false);
        btnAll.setClickable(true);
        getTopLocations();

        btnTop10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAll.setAlpha((float) 1);
                btnAll.setClickable(true);
                btnAll.setScaleX((float) 1);
                btnAll.setScaleY((float) 1);
                btnTop10.setAlpha((float) .1);
                btnTop10.setScaleX((float) .8);
                btnTop10.setScaleY((float) .8);
                btnTop10.setClickable(false);
                btnAll.setClickable(true);
                getTopLocations();
            }
        });
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTop10.setAlpha((float) 1);
                btnTop10.setClickable(true);
                btnTop10.setScaleX((float) 1);
                btnTop10.setScaleY((float) 1);
                btnAll.setAlpha((float) .1);
                btnAll.setScaleX((float) .8);
                btnAll.setScaleY((float) .8);
                btnAll.setClickable(false);
                btnTop10.setClickable(true);
                getAllLocations();
            }
        });
        getTopPosts();
    }

    private void getAllLocations() {
        final SweetAlertDialog pDialog = AlertDialogHelper.alertStartSpin(getContext());
        if (allLocations != null) {
            allLocations.clear();
        }
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.include(Location.KEY_GMAPS_ID);
        query.orderByDescending("topsNumber");
        query.findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> receivedLocations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting locations", e);
                    return;
                }
                if (receivedLocations != null) {
                    allLocations.addAll(receivedLocations);
                    locationsAdapter.notifyDataSetChanged();
                    AlertDialogHelper.alertStopSpin(pDialog);
                } else {
                    Toast.makeText(getContext(), "There are no locations! be the first one to add a pin!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getTopLocations() {
        final SweetAlertDialog pDialog = AlertDialogHelper.alertStartSpin(getContext());
        if (allLocations != null) {
            allLocations.clear();
        }
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.include(Location.KEY_GMAPS_ID);
        query.orderByDescending("topsNumber");
        query.include("location");
        query.setLimit(10);
        query.findInBackground(new FindCallback<Location>() {
            @Override
            public void done(List<Location> receivedLocations, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting locations", e);
                    return;
                }
                if (receivedLocations != null) {
                    allLocations.addAll(receivedLocations);
                    locationsAdapter.notifyDataSetChanged();
                    AlertDialogHelper.alertStopSpin(pDialog);
                } else {
                    Toast.makeText(getContext(), "There are no locations! be the first one to add a pin!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getTopPosts() {
        final SweetAlertDialog pDialog = AlertDialogHelper.alertStartSpin(getContext());
        if (allPosts != null) {
            allPosts.clear();
        }
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        query.orderByDescending("toppedBy");
        query.setLimit(10);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                if (receivedPosts != null) {
                    allPosts.addAll(receivedPosts);
                    postsAdapter.notifyDataSetChanged();
                    AlertDialogHelper.alertStopSpin(pDialog);
                } else {
                    Toast.makeText(getContext(), "There are no posts! be the first one to add a post!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}