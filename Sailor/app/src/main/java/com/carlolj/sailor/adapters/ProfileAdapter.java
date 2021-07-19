package com.carlolj.sailor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.models.Post;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;

    public ProfileAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
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
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivPostImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);

            ivPostImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //Intent ii = new Intent(itemView.getContext(), DetailsActivity.class);
                    //ii.putExtra("Post", Parcels.wrap(posts.get(position)));
                    //context.startActivity(ii);
                }
            });
        }

        public void bind(Post post) {
            ParseFile image = post.getLocationImage();
            if (image != null) {
                Glide.with(ivPostImage.getContext())
                        .load(image.getUrl())
                        .into(ivPostImage);
            }
        }
    }
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }
}
