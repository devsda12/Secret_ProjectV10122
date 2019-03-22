package com.Secret_Labs.secret_projectv10122.message_volley;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.Secret_Labs.secret_projectv10122.Common;
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

public class MessageVolleys {

    Common common;

    //Method to get a complete conversation
    public void getCompleteConversation(final Context context, RequestQueue queue, String activeAccId, String deviceId, String tablename){
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
    public void getMessagesAfterLastMessage(final Context context, RequestQueue queue, Obj_Message obj_message, String activeAccId, String deviceId, String tablename){
        common = new Common();

        //Making the json object and array
        JSONObject tempRequestObject = new JSONObject();
        JSONArray tempRequestArray = new JSONArray();
        try {
            //First putting the device and acc_Id into the
            tempRequestObject.put("device_Id", deviceId);
            tempRequestObject.put("acc_Id", activeAccId);
            tempRequestObject.put("table_Name", tablename);
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
