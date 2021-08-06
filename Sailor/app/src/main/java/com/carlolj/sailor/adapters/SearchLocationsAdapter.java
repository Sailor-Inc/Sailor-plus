package com.carlolj.sailor.adapters;

import android.content.Context;
import android.util.Log;
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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SearchLocationsAdapter extends RecyclerView.Adapter<SearchLocationsAdapter.ViewHolder> {

    Context context;
    protected List<Location> allLocations;
    SearchFragment fragment;


    public SearchLocationsAdapter(Context context, List<Location> allLocations, SearchFragment fragment) {
        this.context = context;
        this.allLocations = allLocations;
        this.fragment = fragment;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_location, parent, false);
        return new SearchLocationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Location location = allLocations.get(position);
        try {
            holder.bind(location);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return allLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView locationImage;
        TextView tvLocation, tvLocationCountry, tvTops;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            locationImage = itemView.findViewById(R.id.ivLocationImage);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvLocationCountry = itemView.findViewById(R.id.tvLocationCountry);
            tvTops = itemView.findViewById(R.id.tvTops);
        }

        public void bind(Location location) throws ParseException {
            tvLocation.setText(location.getName());
            tvTops.setText(Integer.toString(location.getTopsNumber()));
            loadImage(location, locationImage);
        }
    }

    private void loadImage(Location location, ImageView image) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("location");
        query.whereEqualTo("location", location);
        query.getFirstInBackground(new GetCallback<Post>() {
            @Override
            public void done(Post object, ParseException e) {
                if (object == null) {
                    return;
                }
                if (e != null) {
                    return;
                }
                Glide.with(context).load(object.getLocationImage().getUrl()).into(image);
            }
        });
    }
}
