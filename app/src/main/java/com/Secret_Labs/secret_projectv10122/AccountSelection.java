package com.Secret_Labs.secret_projectv10122;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class AccountSelection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountselection);

        //Setting the custom toolbar for our main activity
        Toolbar mainAcToolbar = (Toolbar) findViewById(R.id.acToolbar);
        mainAcToolbar.setTitle(getString(R.string.toolbar_title_account_selection));
        setSupportActionBar(mainAcToolbar);
    }

    //These functions are for the toolbar and the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accountselection, menu);
        return true;
    }
}
