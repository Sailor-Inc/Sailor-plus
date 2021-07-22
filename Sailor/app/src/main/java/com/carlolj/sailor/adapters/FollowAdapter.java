package com.carlolj.sailor.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.carlolj.sailor.R;
import com.carlolj.sailor.activities.MainActivity;
import com.carlolj.sailor.ui.profile.ProfileFragment;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {


    List<ParseUser> followers;
    Context context;

    /**
     * Follow adapter's constructor
     * @param context the context of the calling fragment
     * @param followers a ParseUser list
     */
    public FollowAdapter(Context context, List<ParseUser> followers){
        this.followers = followers;
        this.context = context;
    }

    /**
     * This method runs when the ViewHolder is getting created
     * @param parent the ViewGroup
     * @param viewType an int of the viewType
     * @return an inflated ViewHolder
     */
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_follow, parent, false);
        return new FollowAdapter.ViewHolder(view);
    }

    /**
     * This method runs when the Viewholder is binded
     * @param holder current ViewHolder
     * @param position an int of the current position
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        ParseUser user = followers.get(position);
        holder.bind(user);
    }

    /**
     * This method gets the followers list size
     * @return an int of the size
     */
    @Override
    public int getItemCount() {
        return followers.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView ivProfilePicture;
        TextView tvUsername;

        /**
         * Constructor of the ViewHolder
         * @param itemView the current View
         */
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }

        /**
         * When the ViewHolder is binded this method gets called, this will load the image and the username of each of
         * the "followers" or "following" and assign an onClick method that will start a new Fragment when the profile
         * picture is pressed
         * @param user the current ParseUser
         */
        public void bind(ParseUser user) {
            Glide.with(context).load(user.getParseFile("profilePicture").getUrl()).circleCrop().into(ivProfilePicture);
            tvUsername.setText(user.getUsername());
            if (!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                ivProfilePicture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment fragment = new ProfileFragment(user);
                        ((MainActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.flContainer, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            } else {
                ivProfilePicture.setAlpha((float) .5);
            }
        }
    }
}
