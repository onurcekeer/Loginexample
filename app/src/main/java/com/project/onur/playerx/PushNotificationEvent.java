package com.project.onur.playerx;

import com.project.onur.playerx.model.User;

/**
 * Created by delaroy on 4/13/17.
 */
public class PushNotificationEvent {
    private User mUser;
    private String mMessage;

    public PushNotificationEvent() {
    }

    public PushNotificationEvent(User user, String message) {
        this.mUser = user;
        this.mMessage = message;
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }
}