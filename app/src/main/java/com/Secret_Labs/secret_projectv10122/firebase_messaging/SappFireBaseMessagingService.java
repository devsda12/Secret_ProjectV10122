package com.Secret_Labs.secret_projectv10122.firebase_messaging;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.Secret_Labs.secret_projectv10122.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.sapp_launcher_v2)
                .setContentTitle("TestNotification")
                .setContentText(bodyString);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, nBuilder.build());

        //Now making the requests to the getPartialConversation
    }

}
