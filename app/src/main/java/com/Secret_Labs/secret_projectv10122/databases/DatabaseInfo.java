package com.Secret_Labs.secret_projectv10122.databases;

public class DatabaseInfo {

    //Main variables of the database
    public static final String DBNAME = "Sapp_Acc_DB";
    public static final int DBVERSION = 1;

    //Main table properties for stored accounts on the device
    public class Sapp_Table_Acc{
        //Main variables of the table
        public static final String ACC_TABLE_NAME = "Acc_Table";

        //Column names of the table
        public static final String ACC_ID_COLUMN = "acc_Id";
        public static final String ACC_USERNAME_COLUMN = "acc_Username";
        public static final String ACC_PASSWORD_COLUMN = "acc_Password";
        public static final String ACC_REMEMBERLOGIN_COLUMN = "acc_Rememberlogin";
        public static final String ACC_LASTLOGIN_COLUMN = "acc_Lastlogin";
    }

    //Secondary table properties for the conversation overview table
    public class Sapp_Table_Conv{
        //Main variables for the table
        public static final String CONV_TABLE_NAME = "Conv_Table";

        //Column names for the table
        public static final String CONV_ID_COLUMN = "conv_Id";
        public static final String CONV_ACC_ID_COLUMN = "conv_Acc_Id";
        public static final String CONV_PARTNER_ID_COLUMN = "conv_Partner_Id";
        public static final String CONV_PARTNER_USERNAME_COLUMN = "conv_Partner_Username";
        public static final String CONV_LAST_MESSAGE_COLUMN = "conv_Last_Message";
        public static final String CONV_LAST_MESSAGE_DATE_COLUMN = "conv_Last_Message_Date";
    }

}
