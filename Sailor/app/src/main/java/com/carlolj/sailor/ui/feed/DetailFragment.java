package com.carlolj.sailor.ui.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.FragmentDetailBinding;
import com.carlolj.sailor.databinding.FragmentPinBinding;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.explore.PinFragment;
import com.parse.ParseException;

import org.parceler.Parcels;

public class DetailFragment extends Fragment {

    ImageView ivPostImage, ivProfilePicture;
    TextView tvUsername, tvVisitLocation;
    FragmentDetailBinding binding;
    Post post;

    public DetailFragment(Post post) {
        this.post = post;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ivPostImage = binding.ivPostImage;
        ivProfilePicture = binding.ivProfilePicture;
        tvUsername = binding.tvUsername;
        tvVisitLocation = binding.tvVisitLocation;

        setEnterTransition("1");

        Glide.with(getActivity())
                .load(post.getLocationImage().getUrl())
                .into(ivPostImage);

        tvUsername.setText(post.getAuthor().getUsername());

        tvVisitLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationUniqueId = "";
                String locationTitle = "";

                try {
                    locationUniqueId = (String) post.getLocation().fetchIfNeeded().getObjectId();
                    locationTitle = (String) post.getLocation().fetchIfNeeded().get("name");
                    Fragment fragment = PinFragment.newInstance(locationUniqueId, locationTitle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flContainer, fragment)
                            .commit();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Glide.with(getActivity())
                    .load(post.getAuthor().fetchIfNeeded().getParseFile("profilePicture").getUrl())
                    .circleCrop()
                    .into(ivProfilePicture);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return root;
    }

}