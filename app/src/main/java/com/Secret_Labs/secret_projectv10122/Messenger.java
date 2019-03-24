package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.databases.DatabaseInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_Message;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_Messenger;

import java.util.ArrayList;
import java.util.List;

public class Messenger extends AppCompatActivity {

    RecyclerView messengerRecyclerview;
    RecyclerAdapter_Messenger messengerAdapter;
    List<Obj_Message> messageList;

    Common common;
    SharedPreferences mainprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        common = new Common();
        mainprefs = getSharedPreferences(common.mainPrefsName, 0);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Intent intentReceiver = getIntent();

        Toolbar mesToolbar = (Toolbar) findViewById(R.id.mesToolbar);
        if(intentReceiver.hasExtra("partnerUsername")){
            mesToolbar.setTitle(intentReceiver.getExtras().getString("partnerUsername"));
        } else {
            mesToolbar.setTitle(R.string.toolbar_title_messenger);
        }
        setSupportActionBar(mesToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Recyclerview under here
        messageList = new ArrayList<>();
        messengerRecyclerview = (RecyclerView) findViewById(R.id.messengerRecyclerview);

        //Layoutmanager
        LinearLayoutManager recyclerLayoutmanager = new LinearLayoutManager(this);
        messengerRecyclerview.setLayoutManager(recyclerLayoutmanager);

        //Filling the list under here
        messageList = dbHelper.fetchAllMessagesByConvId(intentReceiver.getExtras().getString("conv_Id"), dbHelper.returnUsernameFromAccId(mainprefs.getString("activeAccId", "none")));

        //Setting the adapter
        messengerAdapter = new RecyclerAdapter_Messenger(this, messageList);
        messengerRecyclerview.setAdapter(messengerAdapter);
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
