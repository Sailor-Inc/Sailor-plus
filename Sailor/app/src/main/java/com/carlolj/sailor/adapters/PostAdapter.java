package com.carlolj.sailor.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Parcel;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.DetailActivity;
import com.carlolj.sailor.activities.MainActivity;
import com.carlolj.sailor.controllers.PostHelper;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.profile.ProfileFragment;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

/**
 * The Post adapter class allows the recycler view to work as expected and populate the view with the
 * list of posts
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;

    /**
     * The constructor of the adapter which creates a new adapter with the specified values
     * @param context the context where the adapter will run
     * @param posts the list of posts that the the recycler view will store
     */
    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    /**
     * When the ViewHolder is created this method inflates the ViewHolder with a xml layout prebuilt file
     * @param parent the parent ViewGroup
     * @param viewType the ViewHolder viewType
     * @return an inflated view with the specified layout
     */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    /**
     * This method executes when the ViewHolder is binded to load a different post
     * @param holder the current ViewHolder
     * @param position the current item position
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    /**
     * This method return the size of the list of posts
     * @return
     */
    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfilePicture, ivPostImage, ivTops;
        TextView tvTops, tvUsername, tvDate;
        int i = 0;

        /**
         * This method is called when we create a new ViewHolder
         * @param itemView the current item in the view
         */
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvTops = itemView.findViewById(R.id.tvTops);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivTops = itemView.findViewById(R.id.ivTops);

            itemView.setOnClickListener(this);
        }

        /**
         * This method gets called when the item is pressed to open a detailed view
         * @param v te current view
         */
        @Override
        public void onClick(View v) {
            openDetailedView(posts.get(getAdapterPosition()), ivProfilePicture, ivPostImage, tvUsername);
        }

        /**
         * This method gets called when the onBindViewHolder has a new item to load
         * @param post the received post object
         */
        public void bind(Post post) {
            PostHelper.getTopsCount(post, tvTops);
            PostHelper.getTopState(post, ivTops);
            tvTops.setText(Integer.toString(post.getTopsNumber()));
            tvDate.setText(PostHelper.calculateTimeAgo(post.getCreatedAt()));

            ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) context;
                    Fragment fragment;
                    fragment = new ProfileFragment(post.getAuthor());
                    ((AppCompatActivity) context).getSupportFragmentManager();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).commit();
                }
            });

            //On click listener in the image view of the triangle
            ivTops.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostHelper.TopPost(post, ivTops, tvTops, context);
                }
            });

            //On click listener in the post image which has the logic to detect if the user pressed twice in a short amount of time
            //or pressed a single time
            ivPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (i < 2) {
                        i++;
                    }
                    Handler handler = new Handler();
                    Runnable runn = new Runnable() {
                        @Override
                        public void run() {
                            if (i == 1) {
                                openDetailedView(post, ivProfilePicture, ivPostImage, tvUsername);
                            }
                            i = 0;
                        }
                    };
                    if (i == 1) {
                        handler.postDelayed(runn, 200);
                    } else if (i == 2) {
                        PostHelper.TopPost(post, ivTops, tvTops, context);
                    }
                }
            });

            int tops = post.getTopsNumber();
            if (tops != 0) {
                tvTops.setText(String.valueOf(tops));
            } else {
                tvTops.setText("No tops");
            }
            try {
                tvUsername.setText(post.getAuthor().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Glide.with(context).load(post.getAuthor().fetchIfNeeded().getParseFile("profilePicture").getUrl())
                        .circleCrop()
                        .into(ivProfilePicture);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            RequestOptions mediaOptions = new RequestOptions();
            mediaOptions = mediaOptions.transforms(new CenterCrop(), new RoundedCorners(20));
            Glide.with(context)
                    .load(post.getLocationImage().getUrl())
                    .apply(mediaOptions)
                    .into(ivPostImage);
        }
    }

    /**
     * This method will open a new detailed activity for a selected post
     * @param post the post that the user is trying to see
     * @param ivProfilePicture the profile picture of the author of the post
     * @param ivPostImage the Image of the post
     * @param tvUsername the username of the author
     */
    public void openDetailedView(Post post, ImageView ivProfilePicture, ImageView ivPostImage, TextView tvUsername) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POST, Parcels.wrap(post));
        intent.putExtra("username", tvUsername.getText().toString());
        Pair<View, String> p1 = Pair.create((View)ivProfilePicture, "profile");
        Pair<View, String> p2 = Pair.create((View)ivPostImage, "image");
        Pair<View, String> p3 = Pair.create((View)tvUsername, "username");
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
        context.startActivity(intent, options.toBundle());
    }
}
