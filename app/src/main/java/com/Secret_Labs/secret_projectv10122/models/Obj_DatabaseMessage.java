package com.Secret_Labs.secret_projectv10122.models;

public class Obj_DatabaseMessage {

    String conv_Id;
    String sender;
    String receiver;
    String message;
    String datetime;

    public Obj_DatabaseMessage(String conv_Id, String sender, String receiver, String message, String datetime) {
        this.conv_Id = conv_Id;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.datetime = datetime;
    }

    public String getConv_Id() {
        return conv_Id;
    }

    public void setConv_Id(String conv_Id) {
        this.conv_Id = conv_Id;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
