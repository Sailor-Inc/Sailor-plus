package com.carlolj.sailor.ui.profile;

import android.media.Image;
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
import android.widget.ImageView;

import com.carlolj.sailor.adapters.FollowAdapter;
import com.carlolj.sailor.controllers.FollowsHelper;
import com.carlolj.sailor.databinding.FragmentFollowingBinding;
import com.carlolj.sailor.models.Follows;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FollowingFragment extends Fragment {

    private final static int FOLLOWING_CODE = 1;
    FragmentFollowingBinding binding;
    public FollowAdapter followingAdapter;
    public List<ParseUser> allFollowing;
    public static final String TAG = "FollowingFragment";

    ParseUser userId;
    RecyclerView rvFollowing;

    public FollowingFragment(ParseUser userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFollowingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFollowing = binding.rvFollowing;
        allFollowing = new ArrayList<>();
        followingAdapter = new FollowAdapter(getContext(), allFollowing);

        rvFollowing.setAdapter(followingAdapter);
        rvFollowing.setLayoutManager(new LinearLayoutManager(getContext()));
        FollowsHelper.queryFollowers(allFollowing, followingAdapter, userId, TAG, FOLLOWING_CODE);
    }
}