package com.project.onur.playerx.model;

import java.util.Map;

/**
 * Created by onur on 28.10.2017 at 01:30.
 */

public class Chat {

    public String senderUid;
    public String receiverUid;
    public String message;
    public Map<String, String> timestamp;

    public Chat() {

    }

    public Chat(String senderUid, String receiverUid, String message, Map<String, String> timestamp) {

        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.message = message;
        this.timestamp = timestamp;

    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Map<String, String> timestamp) {
        this.timestamp = timestamp;
    }
}