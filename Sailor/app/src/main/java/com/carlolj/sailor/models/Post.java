package com.carlolj.sailor.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {

    public Post() { }

    public static final String KEY_AUTHOR = "author";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_LOCATION_IMAGE = "locationImage";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_TOPPED_BY = "toppedBy";

    //Getter and setter for author
    public ParseUser getAuthor(){
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser author){
        put(KEY_AUTHOR,user);
    }

    //Getter and setter for location
    public ParseObject getLocation(){
        return getParseObject(KEY_LOCATION);
    }

    public void setLocation(ParseObject location){
        put(KEY_LOCATION, location);
    }

    //Getter and setter for location image
    public ParseFile getLocationImage(){
        return getParseFile(KEY_LOCATION_IMAGE);
    }

    public void setLocationImage(ParseFile image){
        put(KEY_LOCATION_IMAGE,image);
    }

    //Getter and setter for caption
    public void setCaption(String description){
        put(KEY_CAPTION,description);
    }

    public String getCaption(){
        return getString(KEY_CAPTION);
    }

    //Getter, setter and helpers for toppedBy
    public void setToppedBy(List list){
        put(KEY_TOPPED_BY,list);
    }

    public List getToppedBy(){
        return getList(KEY_TOPPED_BY);
    }

    public void addTop(String id) {
        List<String> list = getList(KEY_TOPPED_BY);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(id);
        setToppedBy(list);
    }

    public boolean isToppedByUser() {
        List<String> list = getList(KEY_TOPPED_BY);
        return list != null && list.contains(ParseUser.getCurrentUser().getObjectId());
    }

    public int getTopsNumber() {
        List<String> list = getList(KEY_TOPPED_BY);
        if (list != null)
            return list.size();
        else
            return 0;
    }
}
