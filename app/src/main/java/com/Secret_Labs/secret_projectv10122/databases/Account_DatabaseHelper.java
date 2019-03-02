package com.Secret_Labs.secret_projectv10122.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
import com.android.volley.toolbox.StringRequest;

public class Account_DatabaseHelper extends SQLiteOpenHelper {


    //Default constructor
    public Account_DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseInfo.DBNAME, null, DatabaseInfo.DBVERSION);
    }

    //Oncreate will be called when there will be an object created of the Account_DatabaseHelper. When the database already exists this code will not be executed
    @Override
    public void onCreate(SQLiteDatabase db){
        String createAccTable = "CREATE TABLE " + DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME + "("
                + DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + " INTEGER PRIMARY KEY,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_USERNAME_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_PASSWORD_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_REMEMBERLOGIN_COLUMN + " INTEGER,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_LASTLOGIN_COLUMN + " TEXT" + ")";

        db.execSQL(createAccTable);

        String createConvTable = "CREATE TABLE " + DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME + "("
                + DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN + " INTEGER PRIMARY KEY,"
                + DatabaseInfo.Sapp_Table_Conv.CONV_ACC_ID_COLUMN + " INTEGER,"
                + DatabaseInfo.Sapp_Table_Conv.CONV_PARTNER_ID_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Conv.CONV_PARTNER_USERNAME_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_DATE_COLUMN + " TEXT,"
                + "FOREIGN KEY(" + DatabaseInfo.Sapp_Table_Conv.CONV_ACC_ID_COLUMN + ") REFERENCES " + DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME + "(" + DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + ")" +  ")";

        db.execSQL(createConvTable);
    }

    //Onupgrade will be called when the database version changes and so all the tables will be dropped and a new table will be created
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        //Drop if table exists
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME);
        onCreate(db);
    }

    //Method to add a singular account to the database
    public boolean addAccount(Obj_AccountInfo accountInfo){
        //Get reference to the database
        SQLiteDatabase db = this.getWritableDatabase();

        //Create content values object and add the values into it
        ContentValues addableValues = new ContentValues();

        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN, accountInfo.getAcc_Id());
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_USERNAME_COLUMN, accountInfo.getAcc_Username());
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_PASSWORD_COLUMN, accountInfo.getAcc_Password());
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_REMEMBERLOGIN_COLUMN, accountInfo.getAcc_RememberLogin());
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_LASTLOGIN_COLUMN, accountInfo.getAcc_Last_Login());

        //Inserting data into database and getting result code back
        long result = db.insert(DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME, null, addableValues);

        //Returning if the data was inserted succesfully for the activity to handle further
        if(result == -1){
            return false;
        } else{
            return true;
        }
    }
}
