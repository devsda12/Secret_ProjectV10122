package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Common common = new Common();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SharedPreferences mainPrefs = getSharedPreferences(common.mainPrefsName, 0);

        //Requestqueue for login
        RequestQueue queue = Volley.newRequestQueue(this);

        if(mainPrefs.getString("activeAccId", "none").equals("none")) {
            Intent intent = new Intent(this, AccountSelection.class);
            startActivity(intent);
        } else {
            //Before logging in getting the username and password from the database if they are present
            String[] detailLookup = dbHelper.checkIfActiveAccInDB(mainPrefs.getString("activeAccId", "none"));

            Intent intent = new Intent(this, AccountSelection.class);
            startActivity(intent);

            if(detailLookup[0] != null && detailLookup[1] != null){
                common.login(this, queue, detailLookup[0], detailLookup[1], 0, false);
            }
        }
        finish();
    }

}
