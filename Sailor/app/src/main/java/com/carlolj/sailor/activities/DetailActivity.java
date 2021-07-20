package com.carlolj.sailor.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carlolj.sailor.R;
import com.carlolj.sailor.models.Post;
import com.parse.ParseException;

import org.jetbrains.annotations.Nullable;
import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;

public class DetailActivity extends AppCompatActivity {

    public final static String EXTRA_POST = "post";
    ImageView ivPostImage, ivProfilePicture;
    TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ivPostImage = findViewById(R.id.ivPostImage);
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUsername = findViewById(R.id.tvUsername);
        String username = getIntent().getStringExtra("username");

        Post post = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_POST));

        Glide.with(getApplicationContext())
                .load(post.getLocationImage().getUrl())
                .into(ivPostImage);

        tvUsername.setText(username);

        try {
            Glide.with(getApplicationContext())
                    .load(post.getAuthor().fetchIfNeeded().getParseFile("profilePicture").getUrl())
                    .circleCrop()
                    .into(ivProfilePicture);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void goBack(View view) {
        supportFinishAfterTransition();
    }
}