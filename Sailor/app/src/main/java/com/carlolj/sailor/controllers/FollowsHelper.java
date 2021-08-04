package com.carlolj.sailor.controllers;

import android.util.Log;

import com.carlolj.sailor.adapters.FollowAdapter;
import com.carlolj.sailor.models.Follows;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class FollowsHelper {

    private final static int FOLLOWING_CODE = 1;
    private final static int FOLLOWERS_CODE = 2;

    /**
     * The queryFollowers method executes a query to find the "following" or "follower" lists for a specific userId
     * then the method executes searchFor method to find each of the users inside the list
     * @param list a ParseUser list that the adapter will use to populate the RecyclerView
     * @param adapter a FollowAdapter that will show each of the users inside the RecyclerView
     * @param parseUser a ParseUser object of the selected account
     * @param TAG a TAG of the calling Fragment
     * @param code a Unique code to execute different queries to get "followers" or "following" users
     */
    public static void queryFollowers(List<ParseUser> list, FollowAdapter adapter, ParseUser parseUser, String TAG, int code){
        list.clear();
        ParseQuery<Follows> query = ParseQuery.getQuery(Follows.class);
        switch (code) {
            case FOLLOWING_CODE:
                query.include("following");
                break;
            case FOLLOWERS_CODE:
                query.include("followers");
                break;
        }
        query.whereEqualTo("userId", parseUser.getObjectId());
        query.findInBackground(new FindCallback<Follows>() {
            @Override
            public void done(List<Follows> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Something went wrong catching followers");
                }
                List<ParseUser> follows = null;
                switch (code) {
                    case FOLLOWING_CODE:
                        follows = objects.get(0).getFollowing();
                        break;
                    case FOLLOWERS_CODE:
                        follows = objects.get(0).getFollowers();
                        break;
                }
                if (follows != null) {
                    list.addAll(follows);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
