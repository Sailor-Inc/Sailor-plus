package com.carlolj.sailor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carlolj.sailor.R;
import com.carlolj.sailor.adapters.PostAdapter;
import com.carlolj.sailor.databinding.ActivityPinBinding;
import com.carlolj.sailor.databinding.ActivityRegisterBinding;
import com.carlolj.sailor.databinding.FragmentExploreBinding;
import com.carlolj.sailor.models.Location;
import com.carlolj.sailor.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PinActivity extends AppCompatActivity {

    protected List<Post> allPosts;
    PostAdapter adapter;
    ActivityPinBinding binding;
    TextView tvTitle;
    RecyclerView rvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPinBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        allPosts = new ArrayList<>();
        adapter = new PostAdapter(PinActivity.this, allPosts);

        tvTitle = binding.tvTitle;
        rvPosts = binding.rvPosts;

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(PinActivity.this));

        String locationUniqueId = getIntent().getStringExtra("locationUniqueId");
        String locationTitle = getIntent().getStringExtra("locationTitle");

        tvTitle.setText(locationTitle);
        loadPostsOf(locationUniqueId);
    }

    private void loadPostsOf(String locationUniqueId) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereContains("location", locationUniqueId);
        query.setLimit(20);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> receivedPosts, ParseException e) {
                if (e != null) {
                    Log.e("PinActivity", "Issue with getting locations", e);
                }
                if (receivedPosts != null) {
                    allPosts.addAll(receivedPosts);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(PinActivity.this, "There are no posts, that's weird...!" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}