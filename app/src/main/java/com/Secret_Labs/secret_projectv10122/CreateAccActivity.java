package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccActivity extends AppCompatActivity {

    RequestQueue accCreateQueue;
    Common common;
    Button accCreateButton;

    SharedPreferences mainPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acc);
        common = new Common();
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);

        //Setting the custom toolbar for the activity
        Toolbar createAccToolbar = (Toolbar) findViewById(R.id.createAccToolbar);
        createAccToolbar.setTitle(R.string.toolbar_title_account_creation);
        setSupportActionBar(createAccToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Creating the requestqueue for the account creation activity
        accCreateQueue = Volley.newRequestQueue(this);

        //Making onclicklistener for the button
        accCreateButton = (Button) findViewById(R.id.createAccButton);
        accCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAcc(accCreateQueue);
            }
        });
    }

    private void createAcc(RequestQueue queue){
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(CreateAccActivity.this, "Account Creation failed: No connection to API");
            return;
        }

        //First getting references to the username and password fields
        EditText username = (EditText) findViewById(R.id.new_UsernameEditText);
        EditText password = (EditText) findViewById(R.id.new_Password1EditText);
        EditText password2 = (EditText) findViewById(R.id.new_Password2EditText);

        //Checking whether the fields are empty
        if(username.getText().toString().equals("") || password.getText().toString().equals("") || password2.getText().toString().equals("")){
            common.displayToast(CreateAccActivity.this, "Account Creation Failed: Fields may not be empty");
            return;
        }

        //Checking whether the password fields match
        if(!password.getText().toString().equals(password2.getText().toString())){
            common.displayToast(CreateAccActivity.this, "Account Creation Failed: Passwords do not match");
            return;
        }

        //If code reaches here making a new JSONObject with the given values
        JSONObject tempCreateAccJson = new JSONObject();
        try{
            tempCreateAccJson.put("acc_Username", username.getText().toString());
            tempCreateAccJson.put("acc_Password", password.getText().toString());
        } catch(JSONException e){
            common.displayToast(CreateAccActivity.this, "Account Creation Failed: JSON Exception occurred");
            return;
        }

        //Creating the JSONObject request itself
        JsonObjectRequest createAccRequest = new JsonObjectRequest(Request.Method.POST, common.apiUrl + "/sapp_createAccount", tempCreateAccJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseAccId = response.getString("acc_Id");
                            common.displayToast(CreateAccActivity.this, responseAccId);
                        } catch (JSONException e){
                            common.displayToast(CreateAccActivity.this, "Account Creation Failed: JSON Exception occurred");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        common.displayToast(CreateAccActivity.this, "Account Creation Failed: This account already exists");
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
