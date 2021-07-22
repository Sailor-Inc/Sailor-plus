package com.carlolj.sailor.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.models.Location;
import com.carlolj.sailor.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {


    List<ParseUser> followers;
    String userId;
    Context context;

    public FollowAdapter(Context context, List<ParseUser> followers){
        this.followers = followers;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow, parent, false);
        return new FollowAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ParseUser user = followers.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView ivProfilePicture;
        TextView tvUsername;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }

        public void bind(ParseUser user) {
            Glide.with(context).load(user.getParseFile("profilePicture").getUrl()).circleCrop().into(ivProfilePicture);
            tvUsername.setText(user.getUsername());
        }
    }
}
