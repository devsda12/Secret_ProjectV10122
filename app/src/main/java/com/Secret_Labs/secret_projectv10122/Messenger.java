package com.Secret_Labs.secret_projectv10122;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.databases.DatabaseInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_DatabaseMessage;
import com.Secret_Labs.secret_projectv10122.models.Obj_Message;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_Messenger;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Messenger extends AppCompatActivity {

    RecyclerView messengerRecyclerview;
    RecyclerAdapter_Messenger messengerAdapter;
    List<Obj_Message> messageList;

    Common common;
    DatabaseHelper dbHelper;
    SharedPreferences mainPrefs;

    RequestQueue messageQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        common = new Common();
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);
        dbHelper = new DatabaseHelper(this);
        messageQueue = Volley.newRequestQueue(this);
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
        messageList = dbHelper.fetchAllMessagesByConvId(intentReceiver.getExtras().getString("conv_Id"), dbHelper.returnUsernameFromAccId(mainPrefs.getString("activeAccId", "none")));

        //Setting the adapter
        messengerAdapter = new RecyclerAdapter_Messenger(this, messageList);
        messengerRecyclerview.setAdapter(messengerAdapter);
    }

    //Method to run when user adds message to the list
    private void userAddMessage(final String conv_Id, final String sender, final String receiver, final String message){
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(Messenger.this, "Message send failed: No connection to API");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String currentDT = sdf.format(new Date());

        //First sending the message to the server
        JSONObject tempMessageSendObject = new JSONObject();
        try {
            tempMessageSendObject.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempMessageSendObject.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
            tempMessageSendObject.put("conv_Id", conv_Id);
            tempMessageSendObject.put("Sender", sender);
            tempMessageSendObject.put("Receiver", receiver);
            tempMessageSendObject.put("Message", message);
            tempMessageSendObject.put("DateTime", currentDT);
        } catch (JSONException e) {
            common.displayToast(Messenger.this, "Message send failed: Json exception");
            return;
        }

        //Making the request
        JsonObjectRequest sendMessageRequest = new JsonObjectRequest(Request.Method.POST, common.apiUrl + "/sapp_addMessage", tempMessageSendObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Getting the insertresult from the server
                String insertResult = "false";
                try {
                    insertResult = response.getString("insertResult");
                } catch (JSONException e) {
                    common.displayToast(Messenger.this, "Message send failed: Json exception on return");
                    return;
                }

                if(insertResult.equals("true")) {
                    //Now inserting the message and all its attributes into the local db
                    List<Obj_DatabaseMessage> insertList = new ArrayList<>();
                    insertList.add(new Obj_DatabaseMessage(conv_Id, sender, receiver, message, currentDT));
                    dbHelper.insertMessagesIntoDB(insertList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        messageQueue.add(sendMessageRequest);
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
