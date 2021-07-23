package com.carlolj.sailor.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.carlolj.sailor.R;
import com.carlolj.sailor.adapters.FollowAdapter;
import com.carlolj.sailor.adapters.ProfileAdapter;
import com.carlolj.sailor.adapters.SectionPagerAdapter;
import com.carlolj.sailor.databinding.FragmentFollowsBinding;
import com.carlolj.sailor.databinding.FragmentProfileBinding;
import com.carlolj.sailor.models.Follows;
import com.carlolj.sailor.models.Post;
import com.google.android.material.tabs.TabLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FollowsFragment extends Fragment {

    private FragmentFollowsBinding binding;
    public static final String TAG = "FollowsFragment";
    ViewPager viewPager;
    TabLayout tabLayout;
    ParseUser userId;
    ImageButton ibBack;
    TextView tvUsername;

    public FollowsFragment(ParseUser userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFollowsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibBack = binding.ibBack;
        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;
        tvUsername = binding.tvUsername;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileFragment(userId);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .commit();
            }
        });
        tvUsername.setText(userId.getUsername());
    }

    /**
     * This method will load the fragments inside the viewPager
     * @param viewPager a ViewPager object
     */
    private void setUpViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new FollowersFragment(userId), "Followers");
        adapter.addFragment(new FollowingFragment(userId), "Following");

        viewPager.setAdapter(adapter);
    }
}