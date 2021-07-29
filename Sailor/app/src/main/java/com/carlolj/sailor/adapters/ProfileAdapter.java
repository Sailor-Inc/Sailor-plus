package com.carlolj.sailor.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.controllers.DetailsHelper;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.feed.DetailFragment;
import com.carlolj.sailor.ui.profile.ProfileFragment;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;
    ProfileFragment fragment;

    public ProfileAdapter(Context context, List<Post> posts, ProfileFragment fragment) {
        this.context = context;
        this.posts = posts;
        this.fragment = fragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_image, parent, false);
        return new ProfileAdapter.ViewHolder(view);
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
        ImageView ivPostImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
        }

        public void bind(Post post) {
            ParseFile image = post.getLocationImage();
            if (image != null) {
                Glide.with(ivPostImage.getContext())
                        .load(image.getUrl())
                        .into(ivPostImage);
            }
            ivPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DetailsHelper.openPostDetailFragment(getAdapterPosition(), v.findViewById(R.id.ivPostImage), post, fragment);
                }
            });
        }
    }
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    /**
     * This method will open a new detailed activity for a selected post
     * @param post the post that the user is trying to see
     * @param ivPostImage the Image of the post
     */
    public void openDetailedView(Post post, ImageView ivPostImage){
        AppCompatActivity activity = (AppCompatActivity) context;

        Fragment fragment = new DetailFragment(post);

        ((AppCompatActivity) context).getSupportFragmentManager();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flContainer, fragment)
                .commit();
    }
}
