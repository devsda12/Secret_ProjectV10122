package com.Secret_Labs.secret_projectv10122;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Common {

    //Common variables
    String apiUrl = "http://54.36.98.223:5000";
    Boolean apiConnection = false;
    String mainPrefsName = "mainPrefs";

    //Method to generate random int
    public int getRandomNumberInRange(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    //Method to display toasts
    public void displayToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    //Method to start the api connection
    public Boolean startUpConnect(Context context, RequestQueue queue){
        //First test the connection to the api
        testApiConnection(context, queue);

        //Now if the connection is true go on with the identification process
        //if(!apiConnection){
        //    return false;
        //}

        //Now executing the function with identifying to the api
        identifyToApi(context, queue);
        return true;

    }
    //Method to test the api connection
    public void testApiConnection(final Context context, RequestQueue queue){
        apiConnection = false;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl + "/testshake",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Here the response String is handled
                        apiConnection = true;
                        displayToast(context, "Connected to API");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        displayToast(context, "Could not reach SAPP API :(");
                    }
                });
        queue.add(stringRequest);
    }

    //Method to identify with the api
    public void identifyToApi(final Context context, RequestQueue queue){
        SharedPreferences tempMainPrefs = context.getSharedPreferences(mainPrefsName, 0);
        final SharedPreferences.Editor tempEditor = tempMainPrefs.edit();

        if(!tempMainPrefs.contains("device_Id")){
            tempEditor.putString("device_Id", "0");
            tempEditor.apply();
        }

        //Creating the temporary json object to store the id in
        JSONObject tempIdJson = new JSONObject();
        try{
            tempIdJson.put("device_Id", tempMainPrefs.getString("device_Id", "0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest idObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl + "/device_identifier", tempIdJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseId = response.getString("device_Id");
                            displayToast(context, response.getString("device_Id"));
                            tempEditor.putString("device_Id", responseId);
                            tempEditor.apply();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        displayToast(context, "Unsuccesful ID Exchange");
                    }
        });

        queue.add(idObjectRequest);
    }
}
