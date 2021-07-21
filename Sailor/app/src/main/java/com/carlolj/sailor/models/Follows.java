package com.carlolj.sailor.models;

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
    public List<String> getFollowers() {
        return getList(KEY_FOLLOWERS);
    }

    public int getFollowersNumber() {
        List<String> list = getList(KEY_FOLLOWERS);
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public void addFollower(String id) {
        List<String> list = getList(KEY_FOLLOWERS);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(id);
        setFollowers(list);
    }

    public void setFollowers(List<String> list) {
        put(KEY_FOLLOWERS, list);
    }

    //Getter, setter and helpers for following
    public List<String> getFollowing() {
        return getList(KEY_FOLLOWING);
    }

    public int getFollowingNumber() {
        List<String> list = getList(KEY_FOLLOWING);
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public void addFollowing(String id) {
        List<String> list = getList(KEY_FOLLOWING);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(id);
        setFollowing(list);
    }

    public void setFollowing(List<String> list) {
        put(KEY_FOLLOWING, list);
    }

    public boolean isFollowed() {
        List<String> list = getList(KEY_FOLLOWERS);
        return list != null && list.contains(ParseUser.getCurrentUser().getObjectId());
    }

    public void removeFollower(String id){
        List<String> list = getList(KEY_FOLLOWERS);
        if (list == null)
            list = new ArrayList<>();
        list.remove(id);
        setFollowers(list);
    }

    public void removeFollowing(String id){
        List<String> list = getList(KEY_FOLLOWING);
        if (list == null)
            list = new ArrayList<>();
        list.remove(id);
        setFollowing(list);
    }
}
