package com.carlolj.sailor.controllers;

import android.util.Log;

import com.carlolj.sailor.adapters.FollowAdapter;
import com.carlolj.sailor.models.Follows;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

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
                switch (code) {
                    case FOLLOWING_CODE:
                        if (objects.get(0).getFollowing() != null) {
                            for (int i = 0; i < objects.get(0).getFollowing().size(); i++) {
                                searchFor(objects.get(0).getFollowing().get(i) + "", list, adapter, TAG);
                            }
                        }
                        break;
                    case FOLLOWERS_CODE:
                        if (objects.get(0).getFollowers() != null) {
                            for (int i = 0; i < objects.get(0).getFollowers().size(); i++) {
                                searchFor(objects.get(0).getFollowers().get(i) + "", list, adapter, TAG);
                            }
                        }
                        break;
                }
            }
        });
    }

    /**
     * The searchFor method will search for the userIds inside the ParseUser class in the database and will
     * add a ParseUser object inside the received list, then the FollowAdapter will get notified.
     * @param user the string userId to search in the database
     * @param list a ParseUser list that the adapter will use to populate the RecyclerView
     * @param adapter a FollowAdapter that will show each of the users inside the RecyclerView
     * @param TAG a TAG of the calling Fragment
     */
    public static void searchFor(String user, List<ParseUser> list, FollowAdapter adapter, String TAG) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseUser.class);
        query.whereEqualTo("objectId", user);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Something went wrong catching user");
                }
                if (ParseUser.getCurrentUser().getObjectId().equals(user)) {
                    objects.get(0).setUsername("You");
                }
                list.add(0, objects.get(0));
                adapter.notifyItemInserted(0);
            }
        });
    }
}
