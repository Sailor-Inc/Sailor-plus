package com.carlolj.sailor.controllers;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.MainActivity;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.feed.DetailFragment;

public class DetailsHelper {

    /**
     * function to open the Feed detail fragment
     * @param position Post list position
     */
    public static void openPostDetailFragment(int position, View view, Post post, Fragment fragment) {
        if (fragment.getActivity() instanceof MainActivity) {
            DetailFragment detailFragment = new DetailFragment(post);
            Bundle bundle = new Bundle();
            bundle.putString("transitionName", "transition" + position);

            detailFragment.setArguments(bundle);
            ((MainActivity) fragment.getActivity()).showFragmentWithTransition(fragment, detailFragment, view, "transition" + position);
        }
    }
}
