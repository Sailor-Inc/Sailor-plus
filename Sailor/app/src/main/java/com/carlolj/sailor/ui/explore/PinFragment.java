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
                    HashMap<Post, Integer> map = new HashMap<Post, Integer>();
                    //Create the hashmap for each post
                    for (Post receivedPost : receivedPosts) {
                        map.put(receivedPost, receivedPost.getTopsNumber());
                    }
                    //HashMap<Post, Integer> sortedAscendingMap = sortHashMapAscendingNew(map);
                    HashMap<Post, Integer> sortedDescendingMap = sortHashMapDescending(map);
                    //List<Post> sortedPostObjects = new ArrayList<>(sortedAscendingMap.keySet());
                    List<Post> sortedDescendingPostObjects = new ArrayList<>(sortedDescendingMap.keySet());

                    int maxLength = sortedDescendingPostObjects.size();
                    if (maxLength > 50) {
                        allPosts.addAll(sortedDescendingPostObjects.subList(0,50));
                    } else {
                        allPosts.addAll(sortedDescendingPostObjects);
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "There are no posts, that's weird..." , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public LinkedHashMap<Post, Integer> sortHashMapAscendingOld(
            HashMap<Post, Integer> passedMap) {
        List<Post> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        //Collections.sort(mapKeys);

        LinkedHashMap<Post, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Integer val = valueIt.next();
            Iterator<Post> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Post key = keyIt.next();
                Integer comp1 = passedMap.get(key);
                Integer comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    public LinkedHashMap<Post, Integer> sortHashMapDescending(
            HashMap<Post, Integer> unSortedMap){
        LinkedHashMap<Post, Integer> reverseSortedMap = new LinkedHashMap<>();

        //Use Comparator.reverseOrder() for reverse ordering
        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        return reverseSortedMap;
    }

    public LinkedHashMap<Post, Integer> sortHashMapAscendingNew(
            HashMap<Post, Integer> unSortedMap){
        LinkedHashMap<Post, Integer> sortedMap = new LinkedHashMap<>();

        unSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        return sortedMap;
    }
}