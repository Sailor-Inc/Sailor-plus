package com.carlolj.sailor.adapters;

import android.content.Context;
import android.media.Image;
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

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.carlolj.sailor.R;
import com.carlolj.sailor.controllers.DetailsHelper;
import com.carlolj.sailor.controllers.PostHelper;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.ui.explore.PinFragment;
import com.carlolj.sailor.ui.profile.ProfileFragment;
import com.parse.ParseException;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.List;

/**
 * The Post adapter class allows the recycler view to work as expected and populate the view with the
 * list of posts
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    private Context context;
    private List<Post> posts;
    private PinFragment fragment;

    /**
     * The constructor of the adapter which creates a new adapter with the specified values
     * @param context the context where the adapter will run
     * @param posts the list of posts that the the recycler view will store
     */
    public PostAdapter(Context context, List<Post> posts, PinFragment fragment) {
        this.context = context;
        this.posts = posts;
        this.fragment = fragment;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.ivPostImage.setTransitionName("transition" + position);
        }

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
        TextView tvTops, tvUsername, tvDate, tvCaption, tvCategory;
        LottieAnimationView topAnimation;
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
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            topAnimation = itemView.findViewById(R.id.topAnimation);

            itemView.setOnClickListener(this);
        }

        /**
         * This method gets called when the item is pressed to open a detailed view
         * @param v te current view
         */
        @Override
        public void onClick(View v) {
            DetailsHelper.openPostDetailFragment(getAdapterPosition(), v.findViewById(R.id.ivPostImage), posts.get(getAdapterPosition()), fragment);
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
            try {
                tvCaption.setText(post.getCaption());
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
                    top(post, ivTops, tvTops, context, topAnimation);
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
                        top(post, ivTops, tvTops, context, topAnimation);
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

    public void clear(){
        posts.clear();
        notifyDataSetChanged();
    }

    private void top(Post post, ImageView ivTops, TextView tvTops, Context context, LottieAnimationView topAnim) {
        topAnim.playAnimation();
        topAnim.setVisibility(View.VISIBLE);
        PostHelper.animationPlayListener(topAnim);
        PostHelper.TopPost(post, ivTops, tvTops, context);
    }
}
