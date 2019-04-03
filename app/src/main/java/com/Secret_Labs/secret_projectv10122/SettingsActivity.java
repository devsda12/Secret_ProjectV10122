package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Setting the custom toolbar for the activity
        Toolbar newConvSelToolbar = (Toolbar) findViewById(R.id.settingsToolbar);
        newConvSelToolbar.setTitle(R.string.toolbar_title_settings);
        setSupportActionBar(newConvSelToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button button = (Button) findViewById(R.id.buttonStatistics);
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.LayoutSettings01);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, StatisticsActivity.class));
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
