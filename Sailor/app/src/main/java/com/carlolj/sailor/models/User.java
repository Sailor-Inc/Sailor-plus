package com.carlolj.sailor.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("User")
public class User extends ParseObject{

    public User(){ }

    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";

    public void setFollowedBy(List list){
        put(KEY_FOLLOWERS,list);
    }

    public void removeFollow(String id){
        List<String> list = getList(KEY_FOLLOWERS);
        if (list == null)
            list = new ArrayList<>();
        list.remove(id);
        setFollowedBy(list);
    }

    public List getFollowers(){
        return getList(KEY_FOLLOWERS);
    }

    public void addFollower(String id) {
        List<String> list = getList(KEY_FOLLOWERS);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(id);
        setFollowedBy(list);
    }

    public boolean isFollowedByUser() {
        List<String> list = getList(KEY_FOLLOWERS);
        return list != null && list.contains(ParseUser.getCurrentUser().getObjectId());
    }

    public int getFollowersNumber() {
        List<String> list = getList(KEY_FOLLOWERS);
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
