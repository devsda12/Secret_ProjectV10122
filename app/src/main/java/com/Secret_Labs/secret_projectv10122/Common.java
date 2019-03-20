package com.Secret_Labs.secret_projectv10122;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.CheckBox;
import android.widget.Toast;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Common {

    //Common variables
    String apiUrl = "http://54.36.98.223:5000";

    String mainPrefsName = "mainPrefs";
    SharedPreferences mainPrefs;
    DatabaseHelper dbHelper;

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

        //Now executing the function with identifying to the api
        identifyToApi(context, queue);
        return true;

    }
    //Method to test the api connection
    public void testApiConnection(final Context context, RequestQueue queue){
        mainPrefs = context.getSharedPreferences(mainPrefsName, 0);
        final SharedPreferences.Editor tempEditor = mainPrefs.edit();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl + "/testshake",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Here the response String is handled
                        tempEditor.putBoolean("apiConnection", true);
                        tempEditor.apply();
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
        mainPrefs = context.getSharedPreferences(mainPrefsName, 0);
        final SharedPreferences.Editor tempEditor = mainPrefs.edit();

        if(!mainPrefs.contains("device_Id")){
            tempEditor.putString("device_Id", "0");
            tempEditor.apply();
        }

        //Creating the temporary json object to store the id in
        JSONObject tempIdJson = new JSONObject();
        try{
            tempIdJson.put("device_Id", mainPrefs.getString("device_Id", "0"));
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

    //Method to login with the api
    public void login(final Context context, RequestQueue queue, final String username, final String password, final int fromWhereRemember, final boolean finish){
        mainPrefs = context.getSharedPreferences(mainPrefsName, 0);
        dbHelper = new DatabaseHelper(context);

        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            displayToast(context, "Login Failed: No connection to API");
            return;
        }

        //Checking whether the fields are empty
        if(username.equals("") || password.equals("")){
            displayToast(context, "Login Failed: Fields may not be empty");
            return;
        }

        //If not empty making a JSON object with the values
        JSONObject tempAuthJson = new JSONObject();
        try{
            tempAuthJson.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempAuthJson.put("acc_Username", username);
            tempAuthJson.put("acc_Password", password);
        } catch (JSONException e) {
            displayToast(context, "Login Failed: JSON Exception occurred");
            return;
        }

        //Creating the request
        JsonObjectRequest authRequest = new JsonObjectRequest(Request.Method.POST, apiUrl + "/sapp_login", tempAuthJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseAccId = response.getString("acc_Id");
                            displayToast(context, responseAccId);

                            // 0 means login is invoked from acc selection or from startup, 1 means to remember the psswd and 2 means don't from login activity
                            if(fromWhereRemember == 1 || fromWhereRemember == 2) {
                                //Adding account details to the database after getting the current time
                                boolean insertResult = false;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String currentDT = sdf.format(new Date());
                                if (fromWhereRemember == 1) {
                                    insertResult = dbHelper.addAccount(new Obj_AccountInfo(responseAccId, username, password, true, currentDT));
                                } else {
                                    insertResult = dbHelper.addAccount(new Obj_AccountInfo(responseAccId, username, null, false, currentDT));
                                }

                                //Checking if the insert was successful
                                if (!insertResult) {
                                    displayToast(context, "Login Failed: Account already exists");
                                    return;
                                }
                            }

                            //Setting in the sharedpreferences which acc_Id is now active
                            SharedPreferences.Editor tempEditor = mainPrefs.edit();
                            tempEditor.putString("activeAccId", responseAccId);
                            tempEditor.commit();

                            //Making Intent for the conv activity
                            Intent goToConvSelection = new Intent(context, ConvSelection.class);
                            context.startActivity(goToConvSelection);
                            if(finish) {
                                ((Activity) context).finish();
                            }
                        } catch (JSONException e){
                            displayToast(context, "Login Failed: JSON Exception occurred");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        displayToast(context, "Login Failed: Username or Password is incorrect");

                        //If username or password is incorrect from acc selection or app startup the login activity should be started
                        if(fromWhereRemember == 0){
                            Intent goToLogin = new Intent(context, LoginActivity.class);
                            context.startActivity(goToLogin);
                        }
                    }
        });
        //Adding request to queue
        queue.add(authRequest);
    }

    //Method to logout with the api
    public void logout(final Context context, RequestQueue queue, final boolean finish){
        mainPrefs = context.getSharedPreferences(mainPrefsName, 0);
        SharedPreferences.Editor tempEdit = mainPrefs.edit();

        //Logging the user out locally
        tempEdit.putString("activeAccId", "none");
        tempEdit.apply();

        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            displayToast(context, "Online Logout Failed: No connection to API");
            return;
        }

        //Making the logout request to the server
        JSONObject logoutOBJ = new JSONObject();
        try {
            logoutOBJ.put("device_Id", mainPrefs.getString("device_Id", "0"));
            logoutOBJ.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
        } catch (JSONException e) {
            displayToast(context, "Online logout failed: JSON Exception occurred");
            return;
        }

        //Making the request
        JsonObjectRequest logoutRequest = new JsonObjectRequest(Request.Method.POST, apiUrl + "/sapp_logout", logoutOBJ, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(!response.getString("deleteResult").equals("true")){
                        displayToast(context, "Online logout succesful");
                    }
                } catch (JSONException e) {
                    displayToast(context, "Online logout failed: JSON Exception occurred");
                }

                if(finish){
                    ((Activity)context).finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayToast(context, "Online logout failed: Server reported error");
                if(finish){
                    ((Activity)context).finish();
                }
            }
        });

        queue.add(logoutRequest);
    }
}
