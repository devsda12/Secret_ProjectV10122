package com.Secret_Labs.secret_projectv10122;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.Secret_Labs.secret_projectv10122.message_volley.VolleyMultipartRequest;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    Common common;
    SharedPreferences mainprefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        common = new Common();
        mainprefs = getSharedPreferences(common.mainPrefsName, 0);

        //Setting the custom toolbar for the activity
        Toolbar newConvSelToolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        newConvSelToolbar.setTitle(R.string.toolbar_title_settings);
        setSupportActionBar(newConvSelToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CardView viewPass = (CardView) findViewById(R.id.cardViewPass);
        CardView viewProfilePicture =  (CardView) findViewById(R.id.cardViewProfilePic);
        CardView viewStats = (CardView) findViewById(R.id.cardViewStats);
        CardView viewAbout = (CardView) findViewById(R.id.cardViewAbout);

        viewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ChangePass.class));
            }
        });
        viewProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageFromGallery();
            }
        });
        viewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, StatisticsActivity.class));
            }
        });
        viewAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, StatisticsActivity.class));
            }
        });

    }

    //Method to pick image from gallery
    private void pickImageFromGallery(){
        //Create an Intent with action as ACTION_PICK
        Intent intent=new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        // Launching the Intent
        startActivityForResult(intent,12);
    }

    //Method that runs on result of the image activity
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case 12:
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();

                    Bitmap pictureBitmap = null;
                    try {
                        pictureBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        sendImageToServer(pictureBitmap);
                    } catch (IOException e) {
                        common.displayToast(SettingsActivity.this, "There was an error on the selected image");
                    }
                    break;
            }
    }

    //Method to actually send the image to the server
    private void sendImageToServer(final Bitmap imageBitmap){
        //Setting the progressdialog
        final ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setTitle("Uploading Image");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //Under here constructing the custom multipartrequest
        VolleyMultipartRequest imageUploadRequest = new VolleyMultipartRequest(Request.Method.POST, common.apiUrl + "/sapp_changeProfilePic", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                progressDialog.dismiss();
                common.displayToast(SettingsActivity.this, new String(response.data));
                //common.displayToast(SettingsActivity.this, "Profile picture updated successfully");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                common.displayToast(SettingsActivity.this, error.getMessage());
                //common.displayToast(SettingsActivity.this, "Profile picture did not update successfully");
            }
        }) {

            //Under here adding parameters to the image request. Here the device and account id are being passed
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("device_Id", mainprefs.getString("device_Id", "0"));
                params.put("acc_Id", mainprefs.getString("activeAccId", "none"));
                return params;
            }

            //Under here passing the actual image
            @Override
            protected Map<String, DataPart> getByteData(){
                Map<String, DataPart> params = new HashMap<>();
                String imagename = mainprefs.getString("activeAccId", "none") + "_ProfilePicture";
                params.put("profilePic", new DataPart(imagename + ".png", getBitmapAsByteArray(imageBitmap)));
                return params;
            }

        };

        Volley.newRequestQueue(SettingsActivity.this).add(imageUploadRequest);
    }

    //Method that converts bitmap image to bytearray. This is used for the database as well as the multipart request
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        //The quality under here determines the quality of the compressed image. Set to 0 now. This will save data.
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
