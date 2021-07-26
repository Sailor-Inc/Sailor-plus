package com.carlolj.sailor.ui.feed;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carlolj.sailor.adapters.FeedAdapter;
import com.carlolj.sailor.adapters.PostAdapter;
import com.carlolj.sailor.databinding.FragmentFeedBinding;
import com.carlolj.sailor.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";
    private FragmentFeedBinding binding;
    protected List<Post> allPosts;

    RecyclerView rvPosts;
    FeedAdapter adapter;


    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        allPosts = new ArrayList<>();
        adapter = new FeedAdapter(getContext(), allPosts);

        rvPosts = binding.rvPosts;

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPostsOf();
        return root;
    }

    private void loadPostsOf() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("objectId");
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e("PinActivity", "Issue with getting locations", e);
                }
                if (receivedPosts != null) {
                    allPosts.addAll(receivedPosts);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "There are no posts, that's weird..." , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}