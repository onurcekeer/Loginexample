package com.project.onur.playerx.model;

import android.location.Location;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by onur on 24.7.2017 at 23:34.
 */
@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class User implements Serializable {

    private String UserID;
    private String Email;
    private String Username;
    private String ProfilURL;
    private String Password;
    private String FcmToken;
    private int Range;

    public User(String userID, String email, String username, String profilURL, int range, String password, String fcmToken) {
        UserID = userID;
        Email = email;

        Username = username;
        ProfilURL = profilURL;
        Range = range;
        Password = password;
        FcmToken = fcmToken;
    }

    public User(){
        //default cons.
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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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

    public String getProfilURL() {
        return ProfilURL;
    }

    public void setProfilURL(String profilURL) {
        ProfilURL = profilURL;
    }

    public int getRange() {
        return Range;
    }

    public void setRange(int range) {
        Range = range;
    }

    public String getFcmToken() {
        return FcmToken;
    }

    public void setFcmToken(String fcmToken) {
        FcmToken = fcmToken;
    }

}
