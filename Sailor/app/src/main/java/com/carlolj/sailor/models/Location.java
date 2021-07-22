package com.carlolj.sailor.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Location")
public class Location extends ParseObject {

    public Location() { }

    public static final String KEY_GMAPS_ID = "gmapsId";
    public static final String KEY_POSTS = "locationPosts";
    public static final String KEY_NAME = "name";
    public static final String KEY_LAT = "latitude";
    public static final String KEY_LONG = "longitude";
    public static final String KEY_TOPS = "topsNumber";

    //Getter and setters for google maps id
    public String getMapsId() {
        return getString(KEY_GMAPS_ID);
    }

    public void setMapsId(String mapsId) {
        put(KEY_GMAPS_ID, mapsId);
    }

    //Getter, setter and helpers for Posts
    public List<String> getLocationPosts() {
        return getList(KEY_POSTS);
    }

    public int getLocationsPostsNumber() {
        List<String> list = getList(KEY_POSTS);
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public void addLocationPost(String id) {
        List<String> list = getList(KEY_POSTS);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(id);
        setLocationPostBy(list);
    }

    public void setLocationPostBy(List<String> list) {
        put(KEY_POSTS, list);
    }

    public List getLocationPostBy() {
        return getList(KEY_POSTS);
    }

    //Getter and setter for name
    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    //Getter and setter for latitude
    public double getLatitude() {
        return getDouble(KEY_LAT);
    }

    public void setLatitude(double latitude) {
        put(KEY_LAT, latitude);
    }

    //Getter and setter for latitude
    public double getLongitude() {
        return getDouble(KEY_LONG);
    }

    public void setLongitude(double longitude) {
        put(KEY_LONG, longitude);
    }

    public int getTopsNumber() {
        return getInt(KEY_TOPS);
    }
}
