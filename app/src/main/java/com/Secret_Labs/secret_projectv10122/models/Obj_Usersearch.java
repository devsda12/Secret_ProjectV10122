package com.Secret_Labs.secret_projectv10122.models;

public class Obj_Usersearch {

    //The data fields
    private String username;
    private String userquote;
    private String userAccId;

    public Obj_Usersearch(String username, String userquote, String userAccId) {
        this.username = username;
        this.userquote = userquote;
        this.userAccId = userAccId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserquote() {
        return userquote;
    }

    public void setUserquote(String userquote) {
        this.userquote = userquote;
    }

    public String getUserAccId() {
        return userAccId;
    }

    public void setUserAccId(String userAccId) {
        this.userAccId = userAccId;
    }
}
