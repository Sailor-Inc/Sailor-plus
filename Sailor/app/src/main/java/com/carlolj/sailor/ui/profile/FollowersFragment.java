package com.carlolj.sailor.ui.profile;

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

import com.carlolj.sailor.R;
import com.carlolj.sailor.adapters.FollowAdapter;
import com.carlolj.sailor.databinding.FragmentFollowersBinding;
import com.carlolj.sailor.databinding.FragmentFollowsBinding;
import com.carlolj.sailor.models.Follows;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FollowersFragment extends Fragment {

    FragmentFollowersBinding binding;
    protected FollowAdapter followAdapter;
    protected List<ParseUser> allFollowers;
    public static final String TAG = "FollowersFragment";

    String userId;
    RecyclerView rvFollowers;

    public FollowersFragment(String userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFollowers = binding.rvFollowers;

        allFollowers = new ArrayList<>();
        followAdapter = new FollowAdapter(getContext(), allFollowers);

        rvFollowers.setAdapter(followAdapter);
        rvFollowers.setLayoutManager(new LinearLayoutManager(getContext()));
        queryFollowers();
    }

    private void queryFollowers(){
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        query.include("followers");
        query.whereEqualTo("userId", userId);
        query.findInBackground(new FindCallback<Follows>() {
            @Override
            public void done(List<Follows> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Something went wrong catching followers");
                }
                for (int i = 0; i < objects.get(0).getFollowers().size(); i++) {
                    searchFor(objects.get(0).getFollowers().get(i));
                }
            }
        });
    }

    private void searchFor(String s) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("objectId", s);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Something went wrong catching user");
                }
                allFollowers.add(objects.get(0));
                followAdapter.notifyDataSetChanged();
            }
        });
    }
}