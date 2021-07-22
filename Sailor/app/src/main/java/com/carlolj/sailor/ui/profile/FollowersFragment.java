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
import com.carlolj.sailor.controllers.FollowsHelper;
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

    private final static int FOLLOWERS_CODE = 2;
    FragmentFollowersBinding binding;
    protected FollowAdapter followAdapter;
    protected List<ParseUser> allFollowers;
    public static final String TAG = "FollowersFragment";

    ParseUser userId;
    RecyclerView rvFollowers;

    public FollowersFragment(ParseUser userId) {
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
        FollowsHelper.queryFollowers(allFollowers, followAdapter, userId, TAG, FOLLOWERS_CODE);
    }
}