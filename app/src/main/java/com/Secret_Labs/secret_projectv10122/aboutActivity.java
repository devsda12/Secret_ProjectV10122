package com.Secret_Labs.secret_projectv10122;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class aboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView andSign = (TextView)findViewById(R.id.aboutAndSign);
        andSign.setText("&");
    }
}
