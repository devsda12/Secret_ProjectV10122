package com.Secret_Labs.secret_projectv10122;

public class Obj_AccountInfo {

    //Defining the data fields of the account info object
    private int acc_Id;
    private String acc_Username;
    private String acc_Password;
    private Boolean acc_RememberLogin;
    private String acc_Last_Login;

    //The constructor
    public Obj_AccountInfo(int acc_Id, String acc_Username, String acc_Password, Boolean acc_RememberLogin, String acc_Last_Login){
        this.acc_Id = acc_Id;
        this.acc_Username = acc_Username;
        this.acc_Password = acc_Password;
        this.acc_RememberLogin = acc_RememberLogin;
        this.acc_Last_Login = acc_Last_Login;
    }

    public int getAcc_Id() {
        return acc_Id;
    }

    public void setAcc_Id(int acc_Id) {
        this.acc_Id = acc_Id;
    }

    public String getAcc_Username() {
        return acc_Username;
    }

    public void setAcc_Username(String acc_Username) {
        this.acc_Username = acc_Username;
    }

    public String getAcc_Password() {
        return acc_Password;
    }

    public void setAcc_Password(String acc_Password) {
        this.acc_Password = acc_Password;
    }

    public Boolean getAcc_RememberLogin() {
        return acc_RememberLogin;
    }

    public void setAcc_RememberLogin(Boolean acc_RememberLogin) {
        this.acc_RememberLogin = acc_RememberLogin;
    }

    public String getAcc_Last_Login() {
        return acc_Last_Login;
    }

    public void setAcc_Last_Login(String acc_Last_Login) {
        this.acc_Last_Login = acc_Last_Login;
    }
}
