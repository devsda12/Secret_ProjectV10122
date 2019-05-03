package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.dialog_popups.ChangePasswordDialog;
import com.Secret_Labs.secret_projectv10122.dialog_popups.ChangeQuoteDialog;
import com.Secret_Labs.secret_projectv10122.dialog_popups.myAccountDialogListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Account extends AppCompatActivity implements myAccountDialogListener{

    FloatingActionButton profilePicButton;
    ImageView profilePicPreview;
    Common common;
    SharedPreferences mainprefs;
    DatabaseHelper dbHelper;
    RequestQueue myAccountQueue;
    TextView quotePreviewTextview;

    @Override
    protected void onResume(){
        super.onResume();
        refreshProfilePicture();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        common = new Common();
        mainprefs = getSharedPreferences(common.mainPrefsName, 0);
        dbHelper = new DatabaseHelper(this);
        myAccountQueue = Volley.newRequestQueue(this);

        //Setting the custom toolbar for the activity
        Toolbar accEditToolbar = (Toolbar) findViewById(R.id.accEditToolbar);
        accEditToolbar.setTitle(R.string.toolbar_title_accEdit);
        setSupportActionBar(accEditToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profilePicButton = (FloatingActionButton) findViewById(R.id.changeProfilePicButton);
        profilePicPreview = (ImageView) findViewById(R.id.editAccounProfilePic);

        profilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Account.this, ProfilePicActivity.class));
            }
        });

        //Setting the stored profile pic as preview
        refreshProfilePicture();

        //Setting the quote for change quote
        quotePreviewTextview = (TextView) findViewById(R.id.quotePreviewTextview);
        quotePreviewTextview.setText(dbHelper.getUserQuote(mainprefs.getString("activeAccId", "none")));

        //Setting the button for changeQuote
        ImageView changeQuotePencil = findViewById(R.id.changeQuotePencil);
        changeQuotePencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeQuoteDialog changeQuoteDialog = new ChangeQuoteDialog();
                changeQuoteDialog.show(getSupportFragmentManager(), "changeQuoteDialog");
            }
        });

        //Setting the button for changePassword
        ImageView changePasswordPencil = findViewById(R.id.changePasswordPencil);
        changePasswordPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
                changePasswordDialog.show(getSupportFragmentManager(), "changePasswordDialog");
            }
        });
    }

    //Method to refresh the profile picture preview
    private void refreshProfilePicture(){
        byte[] storedImage = dbHelper.getProfilePic(mainprefs.getString("activeAccId", "none"));
        if(storedImage != null){
            profilePicPreview.setImageBitmap(BitmapFactory.decodeByteArray(storedImage, 0, storedImage.length));
        }
    }

    @Override
    public void applyNewVariables(final ArrayList<String> newVariables) {
        Log.d("ApplyNewVariables", newVariables.toString());
        //Checking whether the connection is true
        if(!mainprefs.getBoolean("apiConnection", false)){
            common.displayToast(Account.this, "Change failed: No connection to API");
            return;
        }

        if(newVariables.size() == 3){
            //If code reaches here making a new JSONObject with the given values
            JSONObject tempChangePassJson = new JSONObject();
            try{
                tempChangePassJson.put("acc_NewPassword", newVariables.get(1));
                tempChangePassJson.put("acc_Password", newVariables.get(0));
                tempChangePassJson.put("acc_Id", mainprefs.getString("activeAccId", "0"));
                tempChangePassJson.put("device_Id", mainprefs.getString("device_Id", "none"));
            } catch(JSONException e){
                common.displayToast(Account.this, "Password change Failed: JSON Exception occurred");
                return;
            }
            //Creating the JSONObject request itself
            JsonObjectRequest changePassRequest = new JsonObjectRequest(Request.Method.POST, common.apiUrl + "/sapp_changePass", tempChangePassJson,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            dbHelper.changePassword(newVariables.get(1), mainprefs.getString("activeAccId", "0"));
                            common.displayToast(Account.this, "Password changed successfully");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    common.displayToast(Account.this, "Password Change Failed Password incorrect");
                }
            });

            myAccountQueue.add(changePassRequest);

        } else if(newVariables.size() == 1){
            changeQuote(newVariables);
        }
    }

    private void changeQuote(final ArrayList<String> newVariables){
        //Now checking whether a value is present as acc_Id in sharedpreferences
        if(mainprefs.getString("activeAccId", "none").equals("none")){
            common.displayToast(Account.this, "Refresh Failed! No account logged in");
            return;
        }

        //Making the JSON object
        JSONObject newQuoteJson = new JSONObject();
        try {
            newQuoteJson.put("device_Id", mainprefs.getString("device_Id", "0"));
            newQuoteJson.put("acc_Id", mainprefs.getString("activeAccId", "none"));
            newQuoteJson.put("acc_Quote", newVariables.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest newQuoteRequest = new JsonObjectRequest(Request.Method.POST, common.apiUrl + "/sapp_changeQuote", newQuoteJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dbHelper.setUserQuote(mainprefs.getString("activeAccId", "none"), newVariables.get(0));
                quotePreviewTextview.setText(newVariables.get(0));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.displayToast(Account.this, "Quote change failed");
            }
        });

        myAccountQueue.add(newQuoteRequest);
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
