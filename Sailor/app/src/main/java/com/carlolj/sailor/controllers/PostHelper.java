package com.carlolj.sailor.controllers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carlolj.sailor.R;
import com.carlolj.sailor.models.Location;
import com.carlolj.sailor.models.Post;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostHelper {
    public static final String TAG = "PostHelper";
    public static final int ADD_TOP = 1;
    public static final int REMOVE_TOP = 2;
    public static final int CODE_NEW_TOP = 3;
    public static final int CODE_DELETED_TOP = 4;
    /**
     * This method allows the current user to top/untop a post
     * @param post the post object top check
     * @param btnTop the imageview of the button top
     * @param tvTopsNumber the text view containing the number of tops
     * @param context the context of the current post
     */
    public static void TopPost(Post post, ImageView btnTop, TextView tvTopsNumber, Context context) {
        ParseUser user = ParseUser.getCurrentUser();
        if (!post.isToppedByUser()) {
            post.addTop(user.getObjectId());
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving, e");
                        Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "Post save was successful!");
                    btnTop.setBackgroundResource(R.drawable.triangle);
                    tvTopsNumber.setText(String.valueOf(post.getTopsNumber()));
                    setTopsLocationNumber(ADD_TOP, post);
                }
            });
        } else {
            post.removeTop(user.getObjectId());
            post.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving, e");
                        Toast.makeText(context, "Error while saving!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "Post save was successful!");
                    btnTop.setBackgroundResource(R.drawable.triangle_outline);
                    tvTopsNumber.setText(String.valueOf(post.getTopsNumber()));
                    setTopsLocationNumber(REMOVE_TOP, post);
                }
            });
        }
    }

    /**
     * This method changes the drawable of the top if it's topped by the user
     * @param post the post to check
     * @param ivTop the image view of the top
     */
    public static void getTopState(Post post, ImageView ivTop) {
        if (post.isToppedByUser())
            ivTop.setBackgroundResource(R.drawable.triangle);
        else
            ivTop.setBackgroundResource(R.drawable.triangle_outline);
    }

    /**
     * This method sets the number of tops of a certain post in a TextView
     * @param post a post object
     * @param tvTopsNumber the textview to update the tops number
     */
    public static void getTopsCount(Post post, TextView tvTopsNumber) {
        tvTopsNumber.setText(String.valueOf(post.getTopsNumber()));
    }

    /**
     * This function calculates the time passed since a certain time in the past and the current system time
     * @param createdAt the date in the past
     * @return a string with the resulting time
     */
    public static String calculateTimeAgo(Date createdAt) {
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + "h ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + "d ago";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }
        return "";
    }

    private static void setTopsLocationNumber(int code, Post post) {
        ParseQuery<Location> query = ParseQuery.getQuery(Location.class);
        query.whereEqualTo("objectId", post.getLocation().getObjectId());
        query.getFirstInBackground(new GetCallback<Location>() {
            @Override
            public void done(Location object, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Error getting location object : " + e);
                }
                int topsNumber = 0;
                try {
                    topsNumber = object.getTopsNumber();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                switch (code) {
                    case ADD_TOP:
                        topsNumber += 1;
                        break;
                    case REMOVE_TOP:
                        topsNumber -= 1;
                        break;
                }
                object.put("topsNumber", topsNumber);
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.d(TAG, "Error updating location object : " + e);
                        }
                        List<Post> toppedPosts = null;
                        try {
                            toppedPosts = ParseUser.getCurrentUser().fetchIfNeeded().getList("toppedPosts");
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        if (toppedPosts == null) {
                            toppedPosts = new ArrayList<>();
                        }
                        switch (code) {
                            case ADD_TOP:
                                PushNotifications.sendNewLikeNotification(post, PushNotifications.CODE_NEW_TOP);
                                toppedPosts.add(post);
                                break;
                            case REMOVE_TOP:
                                PushNotifications.sendNewLikeNotification(post, PushNotifications.CODE_DELETED_TOP);
                                toppedPosts.remove(post);
                                break;
                        }
                        ParseUser.getCurrentUser().put("toppedPosts", toppedPosts);
                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Error adding to toppedPosts list");
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}