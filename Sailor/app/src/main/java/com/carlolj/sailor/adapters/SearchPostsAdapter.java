package com.carlolj.sailor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.models.Location;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.search.SearchFragment;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchPostsAdapter extends RecyclerView.Adapter<SearchPostsAdapter.ViewHolder> {

    Context context;
    protected List<Post> allPosts;
    SearchFragment fragment;


    public SearchPostsAdapter(Context context, List<Post> allPosts, SearchFragment fragment) {
        this.context = context;
        this.allPosts = allPosts;
        this.fragment = fragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_post, parent, false);
        return new SearchPostsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Post post = allPosts.get(position);
        try {
            holder.bind(post);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return allPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPostImage, ivProfilePicture;
        TextView tvUsername, tvLocation, tvTops;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTops = itemView.findViewById(R.id.tvTops);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
        }

        public void bind(Post post) throws ParseException {
            Glide.with(context).load(post.getLocationImage().getUrl()).into(ivPostImage);
            Glide.with(context).load(post.getAuthor().fetchIfNeeded().
                    getParseFile("profilePicture").getUrl()).
                    circleCrop().into(ivProfilePicture);
            tvUsername.setText(post.getAuthor().fetchIfNeeded().getUsername());
            tvLocation.setText(post.getLocation().fetchIfNeeded().getString("name"));
            tvTops.setText(Integer.toString(post.getTopsNumber()));
        }
    }
}
