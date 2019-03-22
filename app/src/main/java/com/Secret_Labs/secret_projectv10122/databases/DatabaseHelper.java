package com.Secret_Labs.secret_projectv10122.databases;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.Secret_Labs.secret_projectv10122.LoginActivity;
import com.Secret_Labs.secret_projectv10122.message_volley.MessageVolleys;
import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_Message;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Iterator;
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
                + DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_SENDER_COLUMN + " TEXT,"
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
        //First checking if the account is already present in the database
        boolean accInDB = checkIfAccInDB(accountInfo.getAcc_Id());

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

        //If the data is already in the database update the given data
        long result;
        if(accInDB){
            result = db.update(DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME, addableValues, DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + " = ?", new String[]{accountInfo.getAcc_Id()});
        } else{
            //Inserting data into database and getting result code back
            result = db.insert(DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME, null, addableValues);
        }

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

    //Method to just check if the given acc_Id is present in the database
    public boolean checkIfAccInDB(String checkableID){
        //Get a reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        //Executing query on the database
        Cursor result = db.rawQuery("SELECT " + DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + " FROM " + DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME + " WHERE " + DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + " = ?", new String[]{checkableID});
        result.moveToFirst();

        //Returning false if there are no results
        if(result.getCount() == 0){
            result.close();
            return false;
        } else {
            result.close();
            return true;
        }
    }

    //Method to check if the current active acc_Id is present in the local DB and returning the stored username and password
    public String[] checkIfActiveAccInDB(String activeAccId){
        //Get a reference to the database
        SQLiteDatabase db = this.getReadableDatabase();

        //Executing the query on the database
        Cursor result = db.rawQuery("SELECT "+ DatabaseInfo.Sapp_Table_Acc.ACC_USERNAME_COLUMN + ", " + DatabaseInfo.Sapp_Table_Acc.ACC_PASSWORD_COLUMN +" FROM " + DatabaseInfo.Sapp_Table_Acc.ACC_TABLE_NAME + " WHERE " + DatabaseInfo.Sapp_Table_Acc.ACC_ID_COLUMN + " = ?", new String[]{activeAccId});
        result.moveToFirst();

        //Returning empty array if there are no results
        if(result.getCount() == 0){
            result.close();
            return new String[]{null, null};
        }

        //If there are results returning the username and password for the given acc_Id
        String[] returnString = new String[]{result.getString(0), result.getString(1)};
        result.close();
        return returnString;
    }

    //End of acc_Table functions

    //Start of conv_Table functions
    public boolean addConvThumbnails(List<Obj_ConvInfo> convInfoList){
        //Getting reference to the database
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        //List to keep items that need to be removed
        List<Obj_ConvInfo> removelist = new ArrayList<>();

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

                //Adding index of updated item to removelist
                removelist.add(convInfoList.get(j));
            }
        }

        //Now removing updated items from the list
        for(int p = 0; p < removelist.size(); p++){
            convInfoList.remove(removelist.get(p));
        }

        //Now looping through the list and adding every remaining conversation to the database
        for(int i = 0; i < convInfoList.size(); i++){
            ContentValues tempContentValues = new ContentValues();
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN, convInfoList.get(i).getConv_Id());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_ACC_ID_COLUMN, convInfoList.get(i).getConvAcc_Id());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_PARTNER_ID_COLUMN, convInfoList.get(i).getConvPartner_Id());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_PARTNER_USERNAME_COLUMN, convInfoList.get(i).getConvPartner_Username());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_COLUMN, convInfoList.get(i).getConvLast_Message());
            tempContentValues.put(DatabaseInfo.Sapp_Table_Conv.CONV_LAST_MESSAGE_SENDER_COLUMN, convInfoList.get(i).getConvLast_MessageSender());
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
            convInfoList.add(new Obj_ConvInfo(result.getString(0), result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5), result.getString(6)));
        }

        //Closing and returning
        result.close();
        db.close();
        return convInfoList;
    }

    //End of conv_Table functions

    //Start of the message (Conv(x)) table functions

    //Function to check if conv table exists
    public boolean checkIfTableExists(SQLiteDatabase dbRead, String tablename){
        //First checking if the table already exists
        Cursor alltables = dbRead.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        while (alltables.moveToNext()) {
            if (alltables.getString(0).equals(tablename)) {
                alltables.close();
                return true;
            }
        }

        //Returning false when table is not yet present
        alltables.close();
        return false;
    }

    //Function to create table when it does not yet exists
    public int createTableIfExists(String tablename){
        //Getting reference to the database
        SQLiteDatabase dbWrite = this.getWritableDatabase();
        SQLiteDatabase dbRead = this.getReadableDatabase();

        //Checking if the table exists
        if(checkIfTableExists(dbRead, tablename)){
            Cursor checkIfempty = dbRead.rawQuery("SELECT " + DatabaseInfo.Sapp_Table_Convx.CONVX_ID_COLUMN + " FROM [" + tablename + "]", null);
            if(checkIfempty.getCount() == 0){
                //Returning code 2 to tell the caller the table does not have to be created but is empty
                return 2;
            } else {
                //Returning code 1 to tell the caller the table does not have to be created because it already exists and is not empty
                return 1;
            }
        }

        //If the table does not exist it should be created
        String tempCreateTableQuery = "CREATE TABLE [" + tablename + "] ("
                + DatabaseInfo.Sapp_Table_Convx.CONVX_ID_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Convx.CONVX_SENDER_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Convx.CONVX_RECEIVER_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Convx.CONVX_MESSAGE_COLUMN + " TEXT,"
                + DatabaseInfo.Sapp_Table_Convx.CONVX_DATETIME_COLUMN + " TEXT,"
                + "FOREIGN KEY(" + DatabaseInfo.Sapp_Table_Convx.CONVX_ID_COLUMN + ") REFERENCES " + DatabaseInfo.Sapp_Table_Conv.CONV_TABLE_NAME + "(" + DatabaseInfo.Sapp_Table_Conv.CONV_ID_COLUMN + "))";

        dbWrite.execSQL(tempCreateTableQuery);

        //Now again checking if the table exists for the return value
        if(checkIfTableExists(dbRead, tablename)){
            //Returning code 3 to tell the caller the tabel is now created and is an empty table
            return 3;
        } else {
            //Returning code 0 if the table is not created after the create table statement. This will tell the caller to abort the message indexing.
            return 0;
        }
    }

    //Function to get the last message from a existing table
    public Obj_Message fetchLastMessage(String tablename){
        SQLiteDatabase dbRead = this.getReadableDatabase();

        Cursor result = dbRead.rawQuery("SELECT * FROM " + tablename + " ORDER BY " + DatabaseInfo.Sapp_Table_Convx.CONVX_DATETIME_COLUMN + " DESC LIMIT 1", null);
        result.moveToFirst();

        //Creating the return object
        Obj_Message returnResult = new Obj_Message(result.getString(1), result.getString(4), result.getString(3), true);
        result.close();
        return returnResult;
    }

    //Highest function to create and update the message tables
    public void tableFillerRequestmaker(Context context, RequestQueue queue, List<Obj_ConvInfo> convInfoList, String activeAccId, String deviceId){
        //Walking through the objects to check if the table needs to be created or if it already exists
        for(int i = 0; i < convInfoList.size(); i++){
            //First of all making sure the conversation table exists
            int tempCreateResult = createTableIfExists(convInfoList.get(i).getConv_Id());

            //Creating the messagevolleys object
            MessageVolleys messageVolleys = new MessageVolleys();

            //Now the request needs to be made depending on if the table is completely new or already exists
            if(tempCreateResult == 3 || tempCreateResult == 2){
                messageVolleys.getCompleteConversation(context, queue, activeAccId, deviceId, convInfoList.get(i).getConv_Id());
            } else if(tempCreateResult == 1){
                //First getting the last message from the existing database
                Obj_Message lastMessage = fetchLastMessage(convInfoList.get(i).getConv_Id());

                //Now executing the method to send the request
                messageVolleys.getMessagesAfterLastMessage(context, queue, lastMessage, activeAccId, deviceId, convInfoList.get(i).getConv_Id());
            }
        }
    }
}
