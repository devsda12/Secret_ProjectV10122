package com.Secret_Labs.secret_projectv10122.firebase_messaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.Secret_Labs.secret_projectv10122.Messenger;
import com.Secret_Labs.secret_projectv10122.R;
import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_DatabaseMessage;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class SappFireBaseMessagingService extends FirebaseMessagingService {

    //This method runs when a new token is received
    @Override
    public void onNewToken(String token){
        Log.d("ServiceNotice", "On new token function starts now with token: " + token);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", 0);
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            Log.d("ServiceError", "The api connection is false so could not send updated token");
            return;
        }

        //Making request
        JSONObject tempTokenUpdateObj = new JSONObject();
        try {
            tempTokenUpdateObj.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempTokenUpdateObj.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
            tempTokenUpdateObj.put("newToken", token);
        } catch (JSONException e) {
            Log.d("ServiceError", "Json error occured on packing");
            return;
        }

        //Making the actual request
        JsonObjectRequest tempTokenUpdateRequest = new JsonObjectRequest(Request.Method.POST, "http://54.36.98.223:5000/sapp_updateFirebaseToken", tempTokenUpdateObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("ServiceSucces", "The service updated token '" + response.getString("insertedToken") + "' succesfully");
                } catch (JSONException e) {
                    Log.d("ServiceSucces", "Token updated succesfully, but there was an error in the response");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ServiceError", "The server returned an error on the token update");
            }
        });

        Volley.newRequestQueue(this).add(tempTokenUpdateRequest);
    }

    //This override method runs when there is a message received from the firebase messaging service
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d("ServiceNotice", "OnMessagereceived called");
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
        final SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", 0);
        Log.d("ServiceUpdate", "Store or update called");

        //Checking in the default prefs if the activity is active and if so if the convId corresponds
        if(convId.equals(mainPrefs.getString("convIdActive", "none"))){
            //In here making the broadcast to the messenger because the updated chat is active
            Log.d("ServiceUpdate", "Broadcasting to messenger");
            Intent broadCastIntent = new Intent("activeConvIdBroadcast");
            this.sendBroadcast(broadCastIntent);
        } else {
            //If the chat is not active the conversation needs to be updated normally
            //Checking whether the connection is true
            if(!mainPrefs.getBoolean("apiConnection", false)){
                Log.d("ServiceError", "The api connection is false so could not update");
                return;
            }

            //Checking whether an update for the current tablename is already in progress
            Set<String> currentProgressSet = mainPrefs.getStringSet("wideSpreadUpdate", new HashSet<String>());
            if(currentProgressSet.size() > 0) {
                if (currentProgressSet.contains(convId)) {
                    //Update for said table is already in progress or called for
                    Log.d("ServiceUpdate", "CovID already updating");
                    return;
                }
            }

            //Now adding said table to the list
            currentProgressSet.add(convId);
            mainPrefs.edit().putStringSet("wideSpreadUpdate", currentProgressSet).commit();

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
                    Log.d("ServiceUpdate", "Table update response");
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

                    //First giving the convId free for refresh
                    Set<String> tempSet = mainPrefs.getStringSet("wideSpreadUpdate", new HashSet<String>());
                    tempSet.remove(convId);
                    mainPrefs.edit().putStringSet("wideSpreadUpdate", tempSet).commit();

                    //If the insertion was successful a notification has to be made. Calling this method under here
                    if(insertResult){
                        notificationer(tempMessageList, mainPrefs);
                    } else {
                        Log.d("ServiceError", "Error on inserting messages into database");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("ServiceError", "Server returned error");

                    //First giving the convId free for refresh
                    Set<String> tempSet = mainPrefs.getStringSet("wideSpreadUpdate", new HashSet<String>());
                    tempSet.remove(convId);
                    mainPrefs.edit().putStringSet("wideSpreadUpdate", tempSet).commit();
                }
            });

            Volley.newRequestQueue(this).add(storeRequest);
        }
    }

    //Method to make notifications if the updated conversation is not active
    private void notificationer(List<Obj_DatabaseMessage> messageList, SharedPreferences mainprefs){
        //Getting the notification preferences
        SharedPreferences tempNotificationPreferences = getSharedPreferences("notificationIdsShown", 0);

        //Intent for the tap on the notification
        Intent intent = new Intent(this, Messenger.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("partnerUsername", messageList.get(0).getSender());
        intent.putExtra("conv_Id", messageList.get(0).getConv_Id());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        //First creating the notification builder
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this, "SAPP_Channel")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.sapp_launcher_v2))
                .setSmallIcon(R.drawable.sapp_notif_icon_v2)
                .setVibrate(new long[]{300, 500, 200, 150, 200, 150})
                .setContentTitle("SAPP")
                .setContentIntent(pendingIntent);

        //Checking if there is already an notification key value pair to update the currentBody with
        String currentBody = "";
        int currentSize = 0;
        if(tempNotificationPreferences.contains(messageList.get(0).getConv_Id() + "_Notification_Body")){
            currentBody = tempNotificationPreferences.getString(messageList.get(0).getConv_Id() + "_Notification_Body", "");
            currentSize = currentBody.split("\\n").length;
        }

        //Checking the length of the messageList
        if(messageList.size() > 1){
            //Looping through all messages in the list and adding them to notification body
            for(int i = 0; i < messageList.size(); i++){
                currentBody = currentBody + messageList.get(i).getSender() + ": " + messageList.get(i).getMessage() + "\n";
            }
            //currentBody = currentBody.substring(0, currentBody.length() - 1);
            currentSize = currentSize + messageList.size();

            nBuilder.setContentText(Integer.toString(currentSize) + " new messages");
            nBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(currentBody.substring(0, currentBody.length() - 1)));
        } else {
            currentBody = currentBody + messageList.get(0).getSender() + ": " + messageList.get(0).getMessage() + "\n";

            if(currentSize > 0){
                nBuilder.setContentText(Integer.toString(currentSize + 1) + " new messages");
            } else {
                nBuilder.setContentText(messageList.get(0).getSender() + ": " + messageList.get(0).getMessage());
            }

            nBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(currentBody.substring(0, currentBody.length() - 1)));
        }

        //For api 26 and higher setting channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel_name);
            String description = getString(R.string.notification_channel_desc);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("SAPP_Channel", name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[]{300, 500, 200, 150, 200, 150});
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        //Under here updating the notification preferences
        //First getting a random int
        int randomId = ThreadLocalRandom.current().nextInt(100, 200 + 1);

        //Checking whether the preferences already contains the notification
        if(tempNotificationPreferences.contains(messageList.get(0).getConv_Id() + "_Notification_ID")){
            randomId = tempNotificationPreferences.getInt(messageList.get(0).getConv_Id() + "_Notification_ID", 0);
        } else {
            //Now checking if the int is already present in the sharedpreferences
            Map<String,?> allNIds = tempNotificationPreferences.getAll();
            for(Map.Entry<String,?> entry : allNIds.entrySet()){
                if(entry.getValue().equals(randomId)){
                    randomId = ThreadLocalRandom.current().nextInt(200, 300 + 1);
                }
            }
        }

        //Under here the ID of the notification is set to true because a notification with this id is shown, also storing the current body of the notification in a second key value pair
        tempNotificationPreferences.edit().putInt(messageList.get(0).getConv_Id() + "_Notification_ID", randomId).apply();
        tempNotificationPreferences.edit().putString(messageList.get(0).getConv_Id() + "_Notification_Body", currentBody).apply();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(randomId, nBuilder.build());
    }

}
