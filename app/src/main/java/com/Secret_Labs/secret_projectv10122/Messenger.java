package com.Secret_Labs.secret_projectv10122;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.databases.DatabaseInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
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

    String currentConvId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        common = new Common();
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);
        dbHelper = new DatabaseHelper(this);
        messageQueue = Volley.newRequestQueue(this);
        final Intent intentReceiver = getIntent();
        currentConvId = intentReceiver.getExtras().getString("conv_Id");

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

        //Edittext part under here
        final EditText messageText = (EditText) findViewById(R.id.messengerEditText);

        //Send button onclick
        Button sendButton = (Button) findViewById(R.id.messengerSendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAddMessage(currentConvId, dbHelper.returnUsernameFromAccId(mainPrefs.getString("activeAccId", "none")), intentReceiver.getExtras().getString("partnerUsername"), messageText.getText().toString());
                messageText.setText("");
            }
        });

        //Recyclerview under here
        messageList = new ArrayList<>();
        messengerRecyclerview = (RecyclerView) findViewById(R.id.messengerRecyclerview);

        //Recyclerview onlayoutchangelistener part for when the keyboard is out
        messengerRecyclerview.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom < oldBottom){
                    messengerRecyclerview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(messageList.size() > 0) {
                                messengerRecyclerview.smoothScrollToPosition(messageList.size() - 1);
                            } else {
                                messengerRecyclerview.smoothScrollToPosition(0);
                            }
                        }
                    }, 100);
                }
            }
        });

        //Layoutmanager
        LinearLayoutManager recyclerLayoutmanager = new LinearLayoutManager(this);
        messengerRecyclerview.setLayoutManager(recyclerLayoutmanager);

        //Filling the list under here
        messageList = dbHelper.fetchAllMessagesByConvId(currentConvId, dbHelper.returnUsernameFromAccId(mainPrefs.getString("activeAccId", "none")));

        //Setting the adapter
        messengerAdapter = new RecyclerAdapter_Messenger(this, messageList);
        messengerRecyclerview.setAdapter(messengerAdapter);

        //Scrolling to the bottom
        if(messageList.size() > 0) {
            messengerRecyclerview.scrollToPosition(messageList.size() - 1);
        }
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

                    //Now creating a new message object to put inside the list
                    Obj_Message insertMessage = new Obj_Message(sender, currentDT, message, true);
                    recyclerviewMessageInsert(insertMessage);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        messageQueue.add(sendMessageRequest);
    }

    //Method to notify the recyclerview there has been an item added
    private void recyclerviewMessageInsert(Obj_Message insertMessage){
        //Getting the index the message should take
        int insertIndex = messageList.size() + 1;
        messageList.add(insertMessage);
        messengerAdapter.notifyItemInserted(insertIndex);
        messengerRecyclerview.scrollToPosition(messageList.size() - 1);
    }

    //Method to refresh the current messages list
    private void refreshFullList(){
        //Making temporary convinfo object
        Obj_ConvInfo tempObj = new Obj_ConvInfo(currentConvId, null, null, null, null, null, null);
        List<Obj_ConvInfo> tempObjList = new ArrayList<>();
        tempObjList.add(tempObj);

        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(Messenger.this, "Message refresh failed: No connection to API");
            return;
        }

        //Making call to the tablefillerrequestmaker with a convIfoList with 1 item in it: the convinfo object of the current conversation
        common.tableFillerRequestmaker(Messenger.this, messageQueue, tempObjList, mainPrefs.getString("activeAccId", "none"), mainPrefs.getString("device_Id", "0"));

        //Now on the new dataset reacquiring the messages from the database and refreshing the list with it
        messageList.clear();
        messageList.addAll(dbHelper.fetchAllMessagesByConvId(currentConvId, dbHelper.returnUsernameFromAccId(mainPrefs.getString("activeAccId", "none"))));
        messengerAdapter.notifyDataSetChanged();
    }

    //These functions are for the toolbar and the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messenger, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_messenger_refresh:
                refreshFullList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //End of the toolbar menu

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
