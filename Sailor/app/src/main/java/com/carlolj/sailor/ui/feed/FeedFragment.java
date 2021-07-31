package com.carlolj.sailor.ui.feed;

import android.graphics.Movie;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.carlolj.sailor.models.Follows;
import com.carlolj.sailor.models.Post;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";
    private FragmentFeedBinding binding;
    protected List<Post> allPosts;

    RecyclerView rvPosts;
    FeedAdapter adapter;
    TextView tvNoFriends;

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

        tvNoFriends = binding.tvNoFriends;
        rvPosts = binding.rvPosts;

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        loadPostsOfFollowing();
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

    private void loadPostsOfFollowing() {
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<Follows>() {
            @Override
            public void done(Follows object, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "error getting followers of current user account");
                }
                if (object != null) {
                    List<ParseUser> friends = new ArrayList<>();
                    friends = object.getFollowing();
                    if (friends != null && friends.size()>0) {
                        tvNoFriends.setVisibility(View.GONE);
                        List<String> friendsIds = new ArrayList<>();
                        for (ParseUser friend : friends) {
                            friendsIds.add(friend.getObjectId());
                        }
                        loadPostsToppedByFriends(friends);
                        loadFriendsPosts(friendsIds);
                    } else {
                        tvNoFriends.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void loadPostsToppedByFriends(List<ParseUser> friends) {
        HashMap<String, Post> nonRepeatedPosts = new HashMap<String, Post>();
        for (ParseUser friend : friends) {
            try {
                List<Post> friendToppedPosts = friend.fetchIfNeeded().getList("toppedPosts");
                if (friendToppedPosts != null) {
                    for (Post post : friendToppedPosts) {
                        Log.d("This friend likes the post of: ", " : " + friend.getUsername()+"");
                        nonRepeatedPosts.put(post.getObjectId(), post);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        for (Post post : nonRepeatedPosts.values()) {
            post.setTypeOfRecommendation(2);
            allPosts.add(post);
        }
    }

    /**
     * Get the posts with specified query filtering
     */
    private void loadFriendsPosts(List<String> friendsIds) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        if (!friendsIds.isEmpty()) {
            query.whereContainedIn(Post.KEY_AUTHOR, friendsIds);
        }
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting friends posts", e);
                }
                if (receivedPosts != null) {
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