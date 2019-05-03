package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePass extends AppCompatActivity {


    RequestQueue changePassQueue;
    Common common;
    Button changePassButton;
    SharedPreferences mainPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        common = new Common();
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);

        Toolbar newConvSelToolbar = (Toolbar) findViewById(R.id.changePassToolbar);
        newConvSelToolbar.setTitle(R.string.toolbar_title_changePass);
        setSupportActionBar(newConvSelToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Creating the requestque for password change
        changePassQueue = Volley.newRequestQueue(this);

        //Making onclickListener for the button
        changePassButton = (Button) findViewById(R.id.changePassButton);
        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePass(changePassQueue);
            }
        });
    }

    public void changePass(final RequestQueue queue) {
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(ChangePass.this, "Password Change failed: No connection to API");
            return;
        }
        //First getting references to the username and password fields
        final EditText oldPassword = (EditText) findViewById(R.id.Old_PassEditText);
        final EditText newPassword = (EditText) findViewById(R.id.New_PassEditText);
        EditText newPassword2 = (EditText) findViewById(R.id.New2_PassEditText);

        //Checking whether the fields are empty
        if(oldPassword.getText().toString().equals("") || newPassword.getText().toString().equals("") || newPassword2.getText().toString().equals("")){
            common.displayToast(ChangePass.this, "Password change Failed: Fields may not be empty");
            return;
        }

        //Checking whether the password fields match
        if(!newPassword.getText().toString().equals(newPassword2.getText().toString())){
            common.displayToast(ChangePass.this, "Password change Failed: Passwords do not match");
            return;
        }

        //Check if old password is the same as new password
        if(newPassword.getText().toString().equals(oldPassword.getText().toString())){
            common.displayToast(ChangePass.this, "Password change Failed: New Password is the same as old password");
            return;
        }

        //If code reaches here making a new JSONObject with the given values
        JSONObject tempCreateAccJson = new JSONObject();
        try{
            tempCreateAccJson.put("acc_NewPassword", newPassword.getText().toString());
            tempCreateAccJson.put("acc_Password", oldPassword.getText().toString());
            tempCreateAccJson.put("acc_Id", mainPrefs.getString("activeAccId", "0"));
            tempCreateAccJson.put("device_Id", mainPrefs.getString("device_Id", "none"));
        } catch(JSONException e){
            common.displayToast(ChangePass.this, "Password change Failed: JSON Exception occurred");
            return;
        }
        //Creating the JSONObject request itself
        JsonObjectRequest createAccRequest = new JsonObjectRequest(Request.Method.POST, common.apiUrl + "/sapp_changePass", tempCreateAccJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new DatabaseHelper(ChangePass.this).changePassword(newPassword.getText().toString(), mainPrefs.getString("activeAccId", "0"));
                        common.displayToast(ChangePass.this, "Password changed successfully");
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.displayToast(ChangePass.this, "Password Change Failed Password incorrect");
            }
        });
        //Adding request to queue
        queue.add(createAccRequest);

    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


