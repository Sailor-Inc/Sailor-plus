package com.carlolj.sailor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.carlolj.sailor.R;
import com.carlolj.sailor.models.Post;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;

    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfilePicture, ivPostImage;
        TextView tvTops, tvUsername;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvTops = itemView.findViewById(R.id.tvTops);
            tvUsername = itemView.findViewById(R.id.tvUsername);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "i was clicked", Toast.LENGTH_SHORT);
        }

        public void bind(Post post) {
            try {
                tvUsername.setText(post.getAuthor().fetchIfNeeded().getUsername());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvTops.setText(Integer.toString(post.getTopsNumber()));

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
