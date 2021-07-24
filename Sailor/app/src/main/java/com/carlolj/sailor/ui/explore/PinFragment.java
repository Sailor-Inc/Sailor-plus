package com.carlolj.sailor.ui.explore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.carlolj.sailor.adapters.PostAdapter;
import com.carlolj.sailor.databinding.FragmentPinBinding;
import com.carlolj.sailor.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PinFragment extends Fragment {

    public final static String UNIQUE_ID = "locationUniqueId";
    public final static String LOCATION_TITLE = "locationTitle";
    protected List<Post> allPosts;
    PostAdapter adapter;
    TextView tvTitle;
    RecyclerView rvPosts;
    FragmentPinBinding binding;
    String locationUniqueId, locationTitle;


    public PinFragment() {

    }

    public static PinFragment newInstance(String locationUniqueId, String locationTitle) {
        PinFragment fragment = new PinFragment();
        Bundle args = new Bundle();
        args.putString(UNIQUE_ID, locationUniqueId);
        args.putString(LOCATION_TITLE, locationTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            locationUniqueId = getArguments().getString(UNIQUE_ID);
            locationTitle = getArguments().getString(LOCATION_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPinBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts);

        tvTitle = binding.tvTitle;
        rvPosts = binding.rvPosts;

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        tvTitle.setText(locationTitle);
        loadPostsOf(locationUniqueId);

        return root;
    }

    private void loadPostsOf(String locationUniqueId) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereContains("location", locationUniqueId);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e("PinActivity", "Issue with getting locations", e);
                }
                if (receivedPosts != null) {
                    // Before Java 8, sorting a collection would involve creating an anonymous inner class for the
                    // Comparator used in the sort, with the introduction of lambdas, we can bypass the
                    // anonymous inner class and achieve the same result with simple, functional semantics,
                    // and we can further simplify the expression by not specifying the type definitions
                    // – the compiler is capable of inferring these on its own (see a1 and a2 objects doesn't have a type):
                    Collections.sort(receivedPosts, (a1, a2) -> a2.getTopsNumber()-a1.getTopsNumber());
                    int maxLength = receivedPosts.size();
                    if (maxLength > 50) {
                        allPosts.addAll(receivedPosts.subList(0,50));
                    } else {
                        allPosts.addAll(receivedPosts);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "There are no posts, that's weird..." , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}