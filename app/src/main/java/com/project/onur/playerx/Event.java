package com.project.onur.playerx;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by onur on 30.9.2017 at 01:54.
 */

public class Event {

    private String eventID;
    private String userID;
    private int category;
    private String title;
    private String description;
    private LatLng location;
    private Date dateTime;

    public Event(String mEventID, String mUserID, int mCategory, String mTitle, String mDescription, LatLng mLocation, Date mDateTime){

        eventID = mEventID;
        userID = mUserID;
        category = mCategory;

        title = mTitle;
        description = mDescription;
        location = mLocation;
        dateTime = mDateTime;

    }

    public Event(){
        //default cons.
    }


    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }


}
