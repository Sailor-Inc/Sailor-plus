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
import java.util.List;

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
        query.setLimit(20);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e("PinActivity", "Issue with getting locations", e);
                }
                if (receivedPosts != null) {
                    for (Post receivedPost : receivedPosts){
                        try {
                            Log.d("PinFragment", receivedPost.getLocation().fetchIfNeeded().get("gmapsId")+ " : " + locationUniqueId);
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                    }
                    allPosts.addAll(receivedPosts);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "There are no posts, that's weird..." , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}