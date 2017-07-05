package com.example.geniusplaza.geniusplazachatapp.POJO;

import java.util.Date;

/**
 * Created by geniusplaza on 6/30/17.
 */

public class Chat  {
    public String sender;
    public String receiver;
    public String message;
    public long timestamp;
    public String imageURL;
    public Chat(){

    }
    public Chat(String sender, String receiver,String imageURL, int trial){
        this.imageURL = imageURL;
        this.sender = sender;
        this.receiver = receiver;
        this.message = null;
        timestamp = new Date().getTime();
    }
    public Chat(String sender, String receiver, String message){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        timestamp = new Date().getTime();
        this.imageURL = null;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}