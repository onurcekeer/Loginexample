package com.project.onur.playerx;

import android.location.Location;
import android.net.Uri;

/**
 * Created by onur on 24.7.2017 at 23:34.
 */

public class User {

    private String UserID;
    private String Email;
    private String Username;
    private Location LastLocation;
    private Uri ProfilURL;
    private int Range;

    public User(String userID, String email, String username, Location lastLocation, Uri profilURL, int range) {
        UserID = userID;
        Email = email;
        Username = username;
        LastLocation = lastLocation;
        ProfilURL = profilURL;
        Range = range;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public Location getLastLocation() {
        return LastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        LastLocation = lastLocation;
    }

    public Uri getProfilURL() {
        return ProfilURL;
    }

    public void setProfilURL(Uri profilURL) {
        ProfilURL = profilURL;
    }

    public int getRange() {
        return Range;
    }

    public void setRange(int range) {
        Range = range;
    }
}
