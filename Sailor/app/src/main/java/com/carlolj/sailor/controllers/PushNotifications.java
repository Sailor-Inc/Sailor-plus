package com.carlolj.sailor.controllers;

import android.util.Log;

import com.carlolj.sailor.R;
import com.carlolj.sailor.models.Post;
import com.carlolj.sailor.models.User;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class PushNotifications {

    public static final int CODE_STARTED = 1;
    public static final int CODE_STOPPED = 2;
    public static final int CODE_NEW_TOP = 3;
    public static final int CODE_DELETED_TOP = 4;

    public static void startPushService() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId","433345465046");
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
    }

    /**
     * This method finds a user inside the database and starts a push query inside the current database installation users
     * @param userToSearch userTo search in the db installation table
     * @return returns a ParseQuery with the installation user object found in the db
     */
    private static ParseQuery findUserPushQuery(ParseUser userToSearch) {
        //First we need to access the ParseUser of the new following user inside the database
        ParseQuery userQuery = ParseUser.getQuery();
        String objectId = userToSearch.getObjectId();
        userQuery.whereEqualTo("objectId", objectId);

        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user", userQuery);
        return pushQuery;
    }

    /**
     * This method finds a user and fills the required data to send a notification about a new follow
     * @param userToSearch the ParseUser you want to send a notification
     * @param code the int code that tells if a user has started/stopped following the userToSearch
     */
    public static void sendNewFollowsNotification(ParseUser userToSearch, int code) {
        ParsePush push = new ParsePush();
        push.setQuery(findUserPushQuery(userToSearch));
        String originUser = ParseUser.getCurrentUser().getUsername();
        switch (code) {
            case CODE_STARTED:
                push.setMessage(originUser+" has started following you!");
                break;
            case CODE_STOPPED:
                push.setMessage(originUser+" has stopped following you");
                break;
        }
        sendFinishedPush(push);
    }
    
    /**
     * Run this method when the push is ready to be sent
     * @param push a Parsepush object with all the data filled
     */
    public static void sendFinishedPush(ParsePush push) {
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i("PushNotifications", e.getMessage());
                    return;
                }
            }
        });
    }
}
