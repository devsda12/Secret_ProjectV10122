package com.Secret_Labs.secret_projectv10122.message_volley;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.Secret_Labs.secret_projectv10122.Common;
import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_DatabaseMessage;
import com.Secret_Labs.secret_projectv10122.models.Obj_Message;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MessageVolleys {

    Common common;

    //Method to get a complete conversation
    public void getCompleteConversation(final Context context, RequestQueue queue, String activeAccId, String deviceId, final String tablename){
        common = new Common();

        //Making the json object and array
        JSONObject tempRequestObject = new JSONObject();
        JSONArray tempRequestArray = new JSONArray();
        try {
            //First putting the device and acc_Id into the
            tempRequestObject.put("device_Id", deviceId);
            tempRequestObject.put("acc_Id", activeAccId);
            tempRequestObject.put("conv_Id", tablename);
            tempRequestArray.put(tempRequestObject);
        } catch (JSONException e) {
            common.displayToast(context, "Message retrieval failed: JSON exception occurred");
            return;
        }

        //Making the request
        JsonArrayRequest fetchCompleteConversationRequest = new JsonArrayRequest(Request.Method.POST, "http://54.36.98.223:5000/sapp_getCompleteChat", tempRequestArray, new Response.Listener<JSONArray>() {
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
                        tempMessageList.add(new Obj_DatabaseMessage(tablename, tempSender, tempReceiver, tempMessage, tempDatetime));
                    } catch (JSONException e) {
                        common.displayToast(context, "Message retrieval failed: Json exception in response");
                        return;
                    }
                }

                //Calling function to add all of the messages to the chosen database
                new DatabaseHelper(context).insertMessagesIntoDB(tempMessageList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.displayToast(context, "Message retrieval failed: Server returned error");
            }
        });

        queue.add(fetchCompleteConversationRequest);

    }

    //Method to get a conversation after a last message date
    public void getMessagesAfterLastMessage(final Context context, RequestQueue queue, Obj_DatabaseMessage obj_message, String activeAccId, String deviceId, final String tablename){
        common = new Common();

        //Making the json object and array
        JSONObject tempRequestObject = new JSONObject();
        JSONArray tempRequestArray = new JSONArray();
        try {
            //First putting the device and acc_Id into the
            tempRequestObject.put("device_Id", deviceId);
            tempRequestObject.put("acc_Id", activeAccId);
            tempRequestObject.put("conv_Id", tablename);
            tempRequestObject.put("lastMessageDate", obj_message.getDatetime());
            tempRequestArray.put(tempRequestObject);
        } catch (JSONException e) {
            common.displayToast(context, "Message retrieval failed: JSON exception occurred");
            return;
        }

        //Making the request itself
        JsonArrayRequest fetchPartialConversationRequest = new JsonArrayRequest(Request.Method.POST, "http://54.36.98.223:5000/sapp_getPartialChat", tempRequestArray, new Response.Listener<JSONArray>() {
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
                        tempMessageList.add(new Obj_DatabaseMessage(tablename, tempSender, tempReceiver, tempMessage, tempDatetime));
                    } catch (JSONException e) {
                        common.displayToast(context, "Message retrieval failed: Json exception in response");
                        return;
                    }
                }

                //Calling function to add all of the messages to the chosen database
                new DatabaseHelper(context).insertMessagesIntoDB(tempMessageList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.displayToast(context, "Message retrieval failed: Server returned error");
            }
        });

        queue.add(fetchPartialConversationRequest);
    }

}
