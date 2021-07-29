package com.carlolj.sailor.ui.explore;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.MainActivity;
import com.carlolj.sailor.adapters.PostAdapter;
import com.carlolj.sailor.databinding.FragmentPinBinding;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.feed.DetailFragment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    TextView tvTitle, tvFilter;
    RecyclerView rvPosts;
    FragmentPinBinding binding;
    String locationUniqueId, locationTitle;
    ParseQuery<Post> query;

    boolean[] selectedPostType;
    ArrayList<Integer> postList = new ArrayList<>();
    String[] postTypes = {"Landscape", "Mountain" , "Protected nature reserve", "Nature",
                          "Must visit", "Museum", "Amusement Park", "Attraction", "Beach",
                          "Restaurant", "Country", "State", "Island"};
    List<String> listSelectedPostCategories = new ArrayList<>();

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

        selectedPostType = new boolean[postTypes.length];
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext(), allPosts, this);

        tvTitle = binding.tvTitle;
        rvPosts = binding.rvPosts;
        tvFilter = binding.tvFilter;

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        tvTitle.setText(locationTitle);
        loadPostsOf(locationUniqueId);

        tvFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        getContext()
                );
                builder.setTitle("Select Filtering");
                builder.setCancelable(true);
                builder.setMultiChoiceItems(postTypes, selectedPostType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            //When checkbox is selected
                            postList.add(which);
                            Collections.sort(postList);
                        } else {
                            postList.remove(postList.indexOf(which));
                        }
                    }
                });
                builder.setPositiveButton("FILTER", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        listSelectedPostCategories = new ArrayList<>();
                        //use for loop
                        for (int i = 0; i < postList.size(); i++) {
                            //Concat array value
                            stringBuilder.append(postTypes[postList.get(i)]);
                            listSelectedPostCategories.add(postTypes[postList.get(i)]);
                            if (i != postList.size()-1) {
                                stringBuilder.append(", ");
                            }
                        }
                        tvFilter.setText(stringBuilder.toString());
                        allPosts.clear();
                        queryPosts();
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setNeutralButton("CLEAN", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0 ; i < selectedPostType.length ; i++) {
                            selectedPostType[i] = false;
                            postList.clear();
                            tvFilter.setText("");
                        }
                    }
                });
                builder.show();
            }
        });

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

    /**
     * Set up the query with all the data inside the selected post types
     */
    private void setupQueryItems() {
        query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_AUTHOR);
        if(!listSelectedPostCategories.isEmpty())
            query.whereContainedIn(Post.KEY_POST_TYPE, listSelectedPostCategories);
    }

    /**
     * Get the posts with specified query filtering
     */
    private void queryPosts() {
        setupQueryItems();
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e("PinActivity", "Issue with getting locations", e);
                }
                Log.d("PinActivity", ""+receivedPosts);
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