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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static java.util.stream.Collectors.toCollection;

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";
    private FragmentFeedBinding binding;
    protected List<Post> allPosts;

    RecyclerView rvPosts;
    FeedAdapter adapter;
    TextView tvNoFriends;
    SwipeRefreshLayout swipeContainer;
    HashMap<String, Post> nonRepeatedPosts = new HashMap<String, Post>();
    int count = 0, friendCount = 0;

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

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeContainer = binding.swipeContainer;
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPostsOfFollowing();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    /**
     * Call this function to get the feed that shows the posts of the people you're following +
     * give recommendation of posts based on the likes your friends have given, the method gets
     * all of the current friends user ids, and then searches foe each of their toppedPosts
     */
    private void loadPostsOfFollowing() {
        allPosts.clear();
        adapter.notifyDataSetChanged();
        count = 0;
        friendCount = 0;
        nonRepeatedPosts.clear();
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
        query.getFirstInBackground(new GetCallback<Follows>() {
            @Override
            public void done(Follows object, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "error getting followers of current user account");
                    swipeContainer.setRefreshing(false);
                }
                swipeContainer.setRefreshing(false);
                if (object != null) {
                    List<ParseUser> friends = new ArrayList<>();
                    friends = object.getFollowing();
                    if (friends != null && friends.size()>0) {
                        tvNoFriends.setVisibility(View.GONE);
                        List<String> friendsIds = new ArrayList<>();
                        for (ParseUser friend : friends) {
                            friendsIds.add(friend.getObjectId());
                        }
                        loadPostsToppedByFriends(friends, friendsIds);
                    } else {
                        tvNoFriends.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    /**
     * This method gives recommendation of posts based on the likes your friends have given and then calls the function
     * @param friends A ParseUser list of the users the current user is following call -getFolllowing()- of the Follows object of the current user
     * @param friendsIds a String list containing all of the friendsIds to load their posts
     */
    private void loadPostsToppedByFriends(List<ParseUser> friends, List<String> friendsIds) {
        for (ParseUser friend : friends) {
            try {
                List<Post> friendToppedPosts = friend.fetchIfNeeded().getList("toppedPosts");
                if (friendToppedPosts != null) {
                    for (Post post : friendToppedPosts) {
                        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                        query.whereEqualTo("objectId", post.getObjectId());
                        query.getFirstInBackground(new GetCallback<Post>() {
                            @Override
                            public void done(Post object, ParseException e) {
                                if (object == null) {
                                    return;
                                }
                                if (e != null) {
                                    Log.d(TAG, "Error getting liked posts of friends "+e);
                                    return;
                                }
                                nonRepeatedPosts.put(object.getObjectId(), object);
                                count++;
                                if (count == friendToppedPosts.size()) {
                                    for (Post post : nonRepeatedPosts.values()) {
                                        post.setTypeOfRecommendation(2);
                                    }
                                    friendCount++;
                                }
                                if (friendCount == friends.size()) {
                                    loadFriendsPosts(friendsIds);
                                }
                            }
                        });
                    }
                } else {
                    friendCount++;
                    if (friendCount == friends.size()) {
                        loadFriendsPosts(friendsIds);
                    }
                    Log.d("True ", friendCount + " " +friends.size() + "");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the posts of userIds received using a query and a hashmap to avoid repeating posts
     * @param friendsIds A list containing all the ids of the users to get their posts
     */
    private void loadFriendsPosts(List<String> friendsIds) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        if (!friendsIds.isEmpty()) {
            query.whereContainedIn(Post.KEY_AUTHOR, friendsIds);
            query.whereNotEqualTo(Post.KEY_AUTHOR, ParseUser.getCurrentUser().getObjectId());
        }
        query.orderByDescending(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting friends posts", e);
                }
                if (receivedPosts != null) {
                    for (Post post : receivedPosts) {
                        post.setTypeOfRecommendation(0);
                        nonRepeatedPosts.put(post.getObjectId(), post);
                    }
                    ArrayList<Post> newList = nonRepeatedPosts.values().stream()
                            .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                            .collect(toCollection(ArrayList::new));
                    int maxLength = newList.size() + nonRepeatedPosts.values().size();
                    if (maxLength > 50) {
                        allPosts.addAll(newList.subList(0,50));
                    } else {
                        allPosts.addAll(newList);
                    }
                    adapter.notifyDataSetChanged();
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