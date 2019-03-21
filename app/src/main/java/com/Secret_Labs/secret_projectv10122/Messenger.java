package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.Secret_Labs.secret_projectv10122.models.Obj_Message;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_Messenger;

import java.util.ArrayList;
import java.util.List;

public class Messenger extends AppCompatActivity {

    RecyclerView messengerRecyclerview;
    RecyclerAdapter_Messenger messengerAdapter;
    List<Obj_Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
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
        messageList.add(new Obj_Message("Piet", "12 uur", "Hey ho there i go", false));
        messageList.add(new Obj_Message("Piet", "12 uur", "Hey ho there i go", false));
        messageList.add(new Obj_Message("Jan", "12 uur", "Hey ho there i go", true));
        messageList.add(new Obj_Message("Piet", "12 uur", "Hey ho there i go", false));
        messageList.add(new Obj_Message("Piet", "12 uur", "Hey ho there i go", false));
        messageList.add(new Obj_Message("Jan", "12 uur", "Hey ho there i go", true));
        messageList.add(new Obj_Message("Piet", "12 uur", "Hey ho there i go", false));
        messageList.add(new Obj_Message("Piet", "12 uur", "Hey ho there i go", false));
        messageList.add(new Obj_Message("Jan", "12 uur", "Hey ho there i go", true));

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
