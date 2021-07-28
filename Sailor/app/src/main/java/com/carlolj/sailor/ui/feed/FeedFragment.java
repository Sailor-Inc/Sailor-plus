package com.carlolj.sailor.ui.feed;

import android.graphics.Movie;
import android.os.Build;
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
import androidx.transition.TransitionInflater;

import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.MainActivity;
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
        adapter = new FeedAdapter(getContext(), allPosts, this);

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

    /**
     * function to open the Feed detail fragment
     * @param position Movie list position
     */
    public void openMovieDetailFragment(int position, View view, Post post) {
        if (getActivity() instanceof MainActivity) {
            DetailFragment detailFragment = new DetailFragment(post);
            Bundle bundle = new Bundle();
            bundle.putString("transitionName", "transition" + position);

            detailFragment.setArguments(bundle);
            ((MainActivity) getActivity()).showFragmentWithTransition(this, detailFragment, view, "transition" + position);
        }
    }
}