package com.Secret_Labs.secret_projectv10122.firebase_messaging;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.textclassifier.TextLinks;

import com.Secret_Labs.secret_projectv10122.Common;
import com.Secret_Labs.secret_projectv10122.R;
import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_DatabaseMessage;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SappFireBaseMessagingService extends FirebaseMessagingService {

    //This override method runs when there is a message received from the firebase messaging service
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        //Getting the send conversation ID's from the message
        String bodyString = remoteMessage.getData().get("conv_Id");

        /* Disabled because we probably only need 1 conv_Id
        //Getting the json object from the string and then getting the array from the object
        JSONArray convIds;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            convIds = jsonObject.getJSONArray("conv_Ids");
        } catch (JSONException e) {
            return;
        }

        //For testing now making a notification with all the conv id's
        String bodyString = "";
        for(int i = 0; i < convIds.length(); i++){
            try {
                bodyString = bodyString + convIds.getString(i) + "\n";
            } catch (JSONException e) {
                return;
            }
        }
        */

        //Calling storeOrUpdateMessages to or send a request to server to fill tables in background, or send broadcast to the messenger if the conv id's overlap
        storeOrUpdateMessages(bodyString);
    }

    //Function to execute on response of the server
    private void storeOrUpdateMessages(final String convId){
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", 0);

        //Checking in the default prefs if the activity is active and if so if the convId corresponds
        if(convId.equals(mainPrefs.getString("convIdActive", "none"))){
            //In here making the broadcast to the messenger because the updated chat is active
            Intent broadCastIntent = new Intent("activeConvIdBroadcast");
            this.sendBroadcast(broadCastIntent);
        } else {
            //If the chat is not active the conversation needs to be updated normally
            //Checking whether the connection is true
            if(!mainPrefs.getBoolean("apiConnection", false)){
                Log.d("ServiceError", "The api connection is false so could not update");
                return;
            }

            //Creating a JSON Object under here
            //Creating the JSON object and array
            JSONObject tempRequestObject = new JSONObject();
            JSONArray tempRequestArray = new JSONArray();
            try {
                //First putting the device and acc_Id into the
                tempRequestObject.put("device_Id", mainPrefs.getString("device_Id", "0"));
                tempRequestObject.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
                tempRequestObject.put("conv_Id", convId);
                tempRequestObject.put("lastMessageDate", new DatabaseHelper(this).fetchLastMessage(convId).getDatetime());
                tempRequestArray.put(tempRequestObject);
            } catch (JSONException e) {
                Log.d("ServiceError", "There was a JSON exception when trying to make the JSON object and array");
                return;
            }

            //Making the actual request
            JsonArrayRequest storeRequest = new JsonArrayRequest(Request.Method.POST, "http://54.36.98.223:5000/sapp_getPartialChat", tempRequestArray, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    List<Obj_DatabaseMessage> tempMessageList = new ArrayList<>();
                    tempMessageList.clear();

                    //Looping through the response and adding to tempmessagelist
                    for(int i = 0; i < response.length(); i++){
                        JSONObject tempJsonObject = null;
                        try {
                            tempJsonObject = response.getJSONObject(i);
                            String tempSender = tempJsonObject.getString("Sender");
                            String tempReceiver = tempJsonObject.getString("Receiver");
                            String tempMessage = tempJsonObject.getString("Message");
                            String tempDatetime = tempJsonObject.getString("DateTime");
                            tempMessageList.add(new Obj_DatabaseMessage(convId, tempSender, tempReceiver, tempMessage, tempDatetime));
                        } catch (JSONException e) {
                            Log.d("ServiceError", "Json error occurred on response");
                            return;
                        }
                    }

                    //Inserting the messages into the database
                    boolean insertResult = new DatabaseHelper(SappFireBaseMessagingService.this).insertMessagesIntoDB(tempMessageList);

                    //If the insertion was successful a notification has to be made. Calling this method under here
                    if(insertResult){
                        notificationer(tempMessageList);
                    } else {
                        Log.d("ServiceError", "Error on inserting messages into database");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ServiceError", "Server returned error");
                }
            });

            Volley.newRequestQueue(this).add(storeRequest);
        }
    }

    //Method to make notifications if the updated conversation is not active
    private void notificationer(List<Obj_DatabaseMessage> messageList){
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.sapp_launcher_v2))
                .setSmallIcon(R.mipmap.sapp_launcher_v2)
                .setContentTitle("SAPP");

        //Looping through all messages in the list and adding them to notification body
        String currentBody = "";
        for(int i = 0; i < messageList.size(); i++){
            currentBody = currentBody + messageList.get(i).getSender() + ": " + messageList.get(i).getMessage() + "\n";
        }
        currentBody = currentBody.substring(0, currentBody.length() - 1);
        nBuilder.setContentText(currentBody);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, nBuilder.build());
    }

}