package com.carlolj.sailor.ui.explore;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.databinding.FragmentExploreBinding;
import com.carlolj.sailor.databinding.FragmentLocationDetailsBinding;
import com.carlolj.sailor.models.Location;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

public class LocationDetailsFragment extends Fragment {

    Location location;
    FragmentLocationDetailsBinding binding;
    ImageView ivLocationImage, ivBack;
    TextView tvLocationName, tvLocationCountry, tvTops;
    Button btnVisit;
    String imgUrl;

    public LocationDetailsFragment(Location location, String imgUrl) {
        this.location = location;
        this.imgUrl = imgUrl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivLocationImage = binding.ivLocationImage;
        tvLocationName = binding.tvLocationName;
        tvLocationCountry = binding.tvLocationCountry;
        tvTops = binding.tvTops;
        btnVisit = binding.btnVisit;
        ivBack = binding.ivBack;

        Glide.with(getContext()).load(imgUrl).into(ivLocationImage);
        try {
            tvLocationName.setText(location.getName());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvLocationCountry.setText(location.getCountry());
        try {
            tvTops.setText(String.valueOf(location.getTopsNumber()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
        btnVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                try {
                    fragment = PinFragment.newInstance(location.getObjectId(), location.getName());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContainer, fragment)
                        .addToBackStack(null)
                        .commit();
                closeFragment();
            }
        });
    }

    /**
     * Close the fragment
     */
    private void closeFragment() {
        getParentFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_down_fragment,R.anim.bottom_down_fragment)
                .remove(this)
                .commit();
    }
}