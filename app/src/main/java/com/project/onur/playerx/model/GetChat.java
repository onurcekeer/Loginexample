package com.project.onur.playerx.model;

/**
 * Created by onur on 28.10.2017 at 01:30.
 */

public class GetChat {

    public String senderUid;
    public String receiverUid;
    public String message;
    public long timestamp;

    public GetChat() {

    }

    public GetChat(String senderUid, String receiverUid, String message, long timestamp) {

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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}