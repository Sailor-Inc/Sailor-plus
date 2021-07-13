package com.carlolj.sailor.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.FragmentExploreBinding;
import com.carlolj.sailor.databinding.FragmentFeedBinding;

import org.jetbrains.annotations.NotNull;

public class FeedFragment extends Fragment {

    public static final String TAG = "FeedFragment";
    private FragmentFeedBinding binding;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}