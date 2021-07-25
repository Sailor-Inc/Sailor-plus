package com.carlolj.sailor.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Follows")
public class Follows extends ParseObject {

    public Follows() { }

    public static final String KEY_USER_ID = "userId";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";

    //Getter and setters for user id
    public String getUserId() {
        return getString(KEY_USER_ID);
    }

    public void setUserId(String userId) {
        put(KEY_USER_ID, userId);
    }

    //Getter, setter and helpers for followers
    public List<ParseUser> getFollowers() {
        return getList(KEY_FOLLOWERS);
    }

    public int getFollowersNumber() {
        List<String> list = getList(KEY_FOLLOWERS);
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public void addFollower(ParseUser user) {
        List<ParseUser> list = getList(KEY_FOLLOWERS);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(user);
        setFollowers(list);
    }

    public void setFollowers(List<ParseUser> list) {
        put(KEY_FOLLOWERS, list);
    }

    //Getter, setter and helpers for following
    public List<ParseUser> getFollowing() {
        return getList(KEY_FOLLOWING);
    }

    public int getFollowingNumber() {
        List<ParseUser> list = getList(KEY_FOLLOWING);
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public void addFollowing(ParseUser user) {
        List<ParseUser> list = getList(KEY_FOLLOWING);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(user);
        setFollowing(list);
    }

    public void setFollowing(List<ParseUser> list) {
        put(KEY_FOLLOWING, list);
    }

    public boolean isFollowed() {
        List<ParseUser> list = getList(KEY_FOLLOWERS);
        return list != null && containsUser(list, ParseUser.getCurrentUser());
    }

    private boolean containsUser(List<ParseUser> list, ParseUser user) {
        for (ParseUser parseUser : list) {
            if (parseUser.hasSameId(user)) return true;
        }
        return false;

    }

    public void removeFollower(ParseUser user){
        List<ParseUser> list = getList(KEY_FOLLOWERS);
        if (list == null)
            list = new ArrayList<>();
        list.remove(user);
        setFollowers(list);
    }

    public void removeFollowing(ParseUser user){
        List<ParseUser> list = getList(KEY_FOLLOWING);
        if (list == null)
            list = new ArrayList<>();
        list.remove(user);
        setFollowing(list);
    }
}
