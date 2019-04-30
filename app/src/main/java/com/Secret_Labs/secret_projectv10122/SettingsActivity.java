package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

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

        CardView viewEditAccount = (CardView) findViewById(R.id.cardViewEditAccount);
        CardView viewPass =  (CardView) findViewById(R.id.cardViewProfilePic);
        CardView viewStats = (CardView) findViewById(R.id.cardViewStats);
        CardView viewAbout = (CardView) findViewById(R.id.cardViewAbout);

        viewEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, Account.class));
            }
        });
        viewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, ChangePass.class));
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
                startActivity(new Intent(SettingsActivity.this, aboutActivity.class));
            }
        });

    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
