package com.Secret_Labs.secret_projectv10122.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {


    //Default constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, DatabaseInfo.DBNAME, null, DatabaseInfo.DBVERSION);
    }

    //Oncreate will be called when there will be an object created of the DatabaseHelper. When the database already exists this code will not be executed
    @Override
    public void onCreate(SQLiteDatabase db){
        String createAccTable = "CREATE TABLE " + DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME + "("
                + DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + " TEXT PRIMARY KEY,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_USERNAME_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_PASSWORD_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_REMEMBERLOGIN_COLUMN + " INTEGER,"
                + DatabaseInfo.Sapp_Table_Acc.ACC_LASTLOGIN_COLUMN + " TEXT" + ")";

        db.execSQL(createAccTable);

        String createConvTable = "CREATE TABLE " + DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME + "("
                + DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN + " TEXT PRIMARY KEY,"
                + DatabaseInfo.Sapp_Table_Conv.CONV_ACC_ID_COLUMN + " TEXT,"
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

    //Start of acc_Table functions

    //Method to add a singular account to the database
    public boolean addAccount(Obj_AccountInfo accountInfo){
        //Get reference to the database
        SQLiteDatabase db = this.getWritableDatabase();

        //Converting rememberlogin to integer
        int tempRememberLogin = 0;
        if(accountInfo.getAcc_RememberLogin()){
            tempRememberLogin = 1;
        }

        //Create content values object and add the values into it
        ContentValues addableValues = new ContentValues();

        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN, accountInfo.getAcc_Id());
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_USERNAME_COLUMN, accountInfo.getAcc_Username());
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_PASSWORD_COLUMN, accountInfo.getAcc_Password());
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_REMEMBERLOGIN_COLUMN, tempRememberLogin);
        addableValues.put(DatabaseInfo.Sapp_Table_Acc.ACC_LASTLOGIN_COLUMN, accountInfo.getAcc_Last_Login());

        //Inserting data into database and getting result code back
        long result = db.insert(DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME, null, addableValues);

        db.close();

        //Returning if the data was inserted succesfully for the activity to handle further
        if(result == -1){
            return false;
        } else{
            return true;
        }
    }

    //Method to remove singular account from the database
    public boolean removeAccount(String accIdToRemove){
        //Get reference to the database
        SQLiteDatabase db = this.getWritableDatabase();

        //Deleting the data from the database with the account ID
        long result = db.delete(DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME, DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + "=?", new String[]{accIdToRemove});

        db.close();

        //Returning if the row was deleted successfully
        if(result == -1){
            return false;
        } else{
            return true;
        }
    }

    //Method to return a list of accounts for a recyclerview
    public List<Obj_AccountInfo> fetchAllStoredAccounts(){
        //Get a reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        //Executing the query on the database
        Cursor result = db.rawQuery("SELECT * FROM " + DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME, null);

        //Putting the individual rows in a list of acc info objects
        List<Obj_AccountInfo> accList = new ArrayList<>();
        while(result.moveToNext()){
            //Converting int to boolean
            boolean tempRememberlogin = false;
            if(result.getInt(3) == 1){
                tempRememberlogin = true;
            }
            accList.add(new Obj_AccountInfo(result.getString(0), result.getString(1), result.getString(2), tempRememberlogin, result.getString(4)));
        }

        //Closing and returning
        result.close();
        db.close();
        return accList;

    }

    //End of acc_Table functions

    //Start of conv_Table functions
    public boolean addConvThumbnails(List<Obj_ConvInfo> convInfoList){
        //Getting reference to the database
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        //First checking if entry already exists
        for(int j = 0; j < convInfoList.size(); j++){
            Cursor tempSelectResult = dbRead.rawQuery("SELECT " + DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN + " FROM " + DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME + " WHERE " + DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN + " = ?", new String[]{convInfoList.get(j).getConv_Id()});
            if(tempSelectResult.getCount() > 0){
                ContentValues tempContentValues = new ContentValues();
                tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_COLUMN, convInfoList.get(j).getConvLast_Message());
                tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_DATE_COLUMN, convInfoList.get(j).getConvLast_MessageDate());
                long tempResult = dbWrite.update(DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME, tempContentValues, DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN + " = ?", new String[]{convInfoList.get(j).getConv_Id()});
                if(tempResult == -1){
                    tempSelectResult.close();
                    dbRead.close();
                    dbWrite.close();
                    return false;
                }

                //Removing the item which was updated
                convInfoList.remove(j);
            }
        }

        //Now looping through the list and adding every conversation to the database
        for(int i = 0; i < convInfoList.size(); i++){
            ContentValues tempContentValues = new ContentValues();
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN, convInfoList.get(i).getConv_Id());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_ACC_ID_COLUMN, convInfoList.get(i).getConvAcc_Id());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_PARTNER_ID_COLUMN, convInfoList.get(i).getConvPartner_Id());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_PARTNER_USERNAME_COLUMN, convInfoList.get(i).getConvPartner_Username());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_COLUMN, convInfoList.get(i).getConvLast_Message());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_DATE_COLUMN, convInfoList.get(i).getConvLast_MessageDate());
            long tempResult = dbWrite.insert(DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME, null, tempContentValues);
            if(tempResult == -1){
                dbRead.close();
                dbWrite.close();
                return false;
            }
        }

        //Returning true to sign the insertion was successful
        dbRead.close();
        dbWrite.close();
        return true;
    }

    //Fetch all convs by acc_Id
    public List<Obj_ConvInfo> fetchAllConvThumbnails(String acc_Id){
        //Getting reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        //Executing query on the database
        Cursor result = db.rawQuery("SELECT * FROM " + DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME + " WHERE " + DatabaseInfo.Sapp_Table_Conv.CONV_ACC_ID_COLUMN + " = ?", new String[]{acc_Id});

        //Putting results in new list
        List<Obj_ConvInfo> convInfoList = new ArrayList<>();
        while(result.moveToNext()){
            convInfoList.add(new Obj_ConvInfo(result.getString(0), result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5)));
        }

        //Closing and returning
        result.close();
        db.close();
        return convInfoList;
    }
}
