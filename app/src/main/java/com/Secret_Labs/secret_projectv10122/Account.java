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

import java.util.ArrayList;

public class Account extends AppCompatActivity implements myAccountDialogListener{

    FloatingActionButton profilePicButton;
    ImageView profilePicPreview;
    Common common;
    SharedPreferences mainprefs;

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
        byte[] storedImage = new DatabaseHelper(this).getProfilePic(mainprefs.getString("activeAccId", "none"));
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
                            new DatabaseHelper(Account.this).changePassword(newVariables.get(1), mainprefs.getString("activeAccId", "0"));
                            common.displayToast(Account.this, "Password changed successfully");
                            finish();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    common.displayToast(Account.this, "Password Change Failed Password incorrect");
                }
            });

        } else if(newVariables.size() == 1){
            //Do quote stuff
        }
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
