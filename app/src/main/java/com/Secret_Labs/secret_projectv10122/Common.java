package com.Secret_Labs.secret_projectv10122;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Random;

public class Common {

    //Common variables
    String apiUrl = "http://54.36.98.223:5000";
    Boolean apiConnection = false;

    //Method to generate random int
    public int getRandomNumberInRange(int min, int max){
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }

    //Method to display toasts
    public void displayToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
}
