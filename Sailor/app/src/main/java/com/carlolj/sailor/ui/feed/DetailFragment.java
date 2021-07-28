package com.carlolj.sailor.ui.feed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.FragmentDetailBinding;
import com.carlolj.sailor.databinding.FragmentPinBinding;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.explore.PinFragment;
import com.carlolj.sailor.ui.profile.ProfileFragment;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import javax.security.auth.callback.Callback;

public class DetailFragment extends Fragment {

    ImageView ivPostImage, ivProfilePicture, ivBack;
    TextView tvUsername, tvVisitLocation;
    FragmentDetailBinding binding;
    ImageButton btnBack;
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
        btnBack = binding.btnBack;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        Bundle b = getArguments();
        if (b != null) {
            String transitionName = b.getString("transitionName");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivPostImage.setTransitionName(transitionName);
            }
        }
        postponeEnterTransition();

        Glide.with(getActivity())
                .load(post.getLocationImage().getUrl())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        startPostponedEnterTransition();
                        return false;
                    }
                })
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