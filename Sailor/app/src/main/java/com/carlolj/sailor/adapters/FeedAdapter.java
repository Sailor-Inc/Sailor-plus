package com.carlolj.sailor.adapters;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.carlolj.sailor.R;
import com.carlolj.sailor.controllers.DetailsHelper;
import com.carlolj.sailor.controllers.PostHelper;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.feed.FeedFragment;
import com.carlolj.sailor.ui.profile.ProfileFragment;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    List<Post> posts;
    Context context;
    private FeedFragment fragment;

    public FeedAdapter(Context context, List<Post> posts, FeedFragment fragment) {
        this.context = context;
        this.posts = posts;
        this.fragment = fragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);
        return new FeedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Post post = posts.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.ivPostImage.setTransitionName("transition" + position);
        }

        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfilePicture, ivPostImage, ivTops;
        TextView tvTops, tvUsername, tvDate, tvCaption, tvCategory;
        int i = 0;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvTops = itemView.findViewById(R.id.tvTops);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivTops = itemView.findViewById(R.id.ivTops);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }

        public void bind(Post post) {
            PostHelper.getTopsCount(post, tvTops);
            PostHelper.getTopState(post, ivTops);
            tvTops.setText(Integer.toString(post.getTopsNumber()));
            tvDate.setText(PostHelper.calculateTimeAgo(post.getCreatedAt()));
            tvCaption.setText(post.getCaption());
            tvCategory.setText("Category: " + post.getPostType());

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
                                DetailsHelper.openPostDetailFragment(getAdapterPosition(), v.findViewById(R.id.ivPostImage), post, fragment);
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
}
