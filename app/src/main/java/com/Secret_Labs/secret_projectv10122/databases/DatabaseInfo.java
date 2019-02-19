package com.Secret_Labs.secret_projectv10122.databases;

public class DatabaseInfo {

    public class Sapp_Acc_DB{
        //Main variables of the database
        public static final String DBNAME = "Sapp_Acc_DB";
        public static final String TABLE_NAME = "Acc_Table";
        public static final int DBVERSION = 1;

        //Column names of the database
        public static final String ACC_ID_COLUMN = "acc_Id";
        public static final String ACC_USERNAME_COLUMN = "acc_Username";
        public static final String ACC_PASSWORD_COLUMN = "acc_Password";
        public static final String ACC_REMEMBERLOGIN_COLUMN = "acc_Rememberlogin";
        public static final String ACC_LASTLOGIN_COLUMN = "acc_Lastlogin";
    }

}
