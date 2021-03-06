package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

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
        Switch notificationSwitch = (Switch) findViewById(R.id.switchNotifications);
        final ImageView notificationImage = (ImageView) findViewById(R.id.imageViewNotification) ;


        Boolean notificationsState = mainprefs.getBoolean("enableNotifications", true);
        notificationSwitch.setChecked(notificationsState);

        if(notificationsState){
            notificationImage.setImageResource(R.drawable.action_notifications_active);
        } else{
            notificationImage.setImageResource(R.drawable.action_notifications_off);
        }


        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mainprefs.edit().putBoolean("enableNotifications", isChecked).apply();

                if(isChecked){
                    notificationImage.setImageResource(R.drawable.action_notifications_active);
                } else{
                    notificationImage.setImageResource(R.drawable.action_notifications_off);
                }
            }
        });
        viewEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, Account.class));
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
