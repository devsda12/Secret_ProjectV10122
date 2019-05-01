package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;

public class Account extends AppCompatActivity {

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
    }

    //Method to refresh the profile picture preview
    private void refreshProfilePicture(){
        byte[] storedImage = new DatabaseHelper(this).getProfilePic(mainprefs.getString("activeAccId", "none"));
        if(storedImage != null){
            profilePicPreview.setImageBitmap(BitmapFactory.decodeByteArray(storedImage, 0, storedImage.length));
        }
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
