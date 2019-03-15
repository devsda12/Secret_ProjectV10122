package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences mainPrefs = getSharedPreferences("mainPrefs", 0);
        if(mainPrefs.getString("activeAccId", "none").equals("none")) {
            Intent intent = new Intent(this, AccountSelection.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ConvSelection.class);
            startActivity(intent);
        }
        finish();
    }

}
