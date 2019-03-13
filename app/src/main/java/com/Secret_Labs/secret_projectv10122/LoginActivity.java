package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    RequestQueue loginQueue;
    Common common;
    Button loginButton;

    SharedPreferences mainPrefs;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        common = new Common();
        dbHelper = new DatabaseHelper(this);
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);

        //Setting custom toolbar for the login activity
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.loginToolbar);
        loginToolbar.setTitle(R.string.toolbar_title_login);
        setSupportActionBar(loginToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Creating a requestqueue for the login activity
        loginQueue = Volley.newRequestQueue(this);

        //Making onclicklistener for the login button
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(loginQueue);
            }
        });
    }

    //Method that runs when the login button is pressed
    public void login(RequestQueue queue){
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(LoginActivity.this, "Login Failed: No connection to API");
            return;
        }

        //First getting references to the username and password from the fields
        final EditText username = (EditText) findViewById(R.id.usernameEditText);
        final EditText password = (EditText) findViewById(R.id.passwordEditText);
        final CheckBox rememberCheckbox = (CheckBox) findViewById(R.id.rememberCheckBox);

        //Checking whether the fields are empty
        if(username.getText().toString().equals("") || password.getText().toString().equals("")){
            common.displayToast(LoginActivity.this, "Login Failed: Fields may not be empty");
            return;
        }

        //If not empty making a JSON object with the values
        JSONObject tempAuthJson = new JSONObject();
        try{
            tempAuthJson.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempAuthJson.put("acc_Username", username.getText().toString());
            tempAuthJson.put("acc_Password", password.getText().toString());
        } catch (JSONException e) {
            common.displayToast(LoginActivity.this, "Login Failed: JSON Exception occurred");
            return;
        }

        //Creating the request
        JsonObjectRequest authRequest = new JsonObjectRequest(Request.Method.POST, common.apiUrl + "/sapp_login", tempAuthJson,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String responseAccId = response.getString("acc_Id");
                            common.displayToast(LoginActivity.this, responseAccId);

                            //Adding account details to the database after getting the current time
                            boolean insertResult = false;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String currentDT = sdf.format(new Date());
                            if(rememberCheckbox.isChecked()){
                                insertResult = dbHelper.addAccount(new Obj_AccountInfo(responseAccId, username.getText().toString(), password.getText().toString(), true, currentDT));
                            } else {
                                insertResult = dbHelper.addAccount(new Obj_AccountInfo(responseAccId, username.getText().toString(), null, false, currentDT));
                            }

                            //Checking if the insert was successful
                            if(!insertResult){
                                common.displayToast(LoginActivity.this, "Login Failed: Account insertion into database failed");
                                return;
                            }

                            //Setting in the sharedpreferences which acc_Id is now active
                            SharedPreferences.Editor tempEditor = mainPrefs.edit();
                            tempEditor.putString("activeAccId", responseAccId);
                            tempEditor.commit();

                            //Making Intent for the conv activity
                            Intent goToConvSelection = new Intent(LoginActivity.this, ConvSelection.class);
                            startActivity(goToConvSelection);
                        } catch (JSONException e){
                            common.displayToast(LoginActivity.this, "Login Failed: JSON Exception occurred");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        common.displayToast(LoginActivity.this, "Login Failed: Username or Password is incorrect");
                    }
                });
        //Adding request to queue
        queue.add(authRequest);
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
