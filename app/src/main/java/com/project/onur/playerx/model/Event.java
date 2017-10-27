package com.project.onur.playerx.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by onur on 30.9.2017 at 01:54.
 */
@SuppressWarnings("serial")
public class Event implements Serializable {

    private String eventID;
    private String userID;
    private int category;
    private String title;
    private String description;
    private LatLon location;
    private String username;
    private String profileURL;
    private Date dateTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public Event(String mEventID, String mUserID, int mCategory, String mTitle, String mDescription, LatLon mLocation, Date mDateTime, String mUsername, String mProfileURL){

        eventID = mEventID;
        userID = mUserID;
        category = mCategory;
        username = mUsername;
        profileURL = mProfileURL;

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

    public LatLon getLocation() {
        return location;
    }

    public void setLocation(LatLon location) {
        this.location = location;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }


}
