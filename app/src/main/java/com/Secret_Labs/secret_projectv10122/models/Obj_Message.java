package com.Secret_Labs.secret_projectv10122.models;

public class Obj_Message {

    //Class attributes
    private String sender;
    private String datetime;
    private String message;
    private boolean yourself;

    public Obj_Message(String sender, String datetime, String message, boolean yourself) {
        this.sender = sender;
        this.datetime = datetime;
        this.message = message;
        this.yourself = yourself;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isYourself() {
        return yourself;
    }

    public void setYourself(boolean yourself) {
        this.yourself = yourself;
    }
}
