package com.Secret_Labs.secret_projectv10122;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class NewConvSelection extends AppCompatActivity {

    SearchView usernameSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conv_selection);

        //Setting the custom toolbar for the activity
        Toolbar createAccToolbar = (Toolbar) findViewById(R.id.createAccToolbar);
        createAccToolbar.setTitle(R.string.toolbar_title_newconv_selection);
        setSupportActionBar(createAccToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    //Toolbar menu methods beneath here
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_newconvselection, menu);

        //Declaring usernamesearchview in the oncreate
        MenuItem searchItem = menu.findItem(R.id.action_newconvsel_search);
        usernameSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        usernameSearchView.setQueryHint(getString(R.string.newconv_selection_search));
        usernameSearchView.setIconifiedByDefault(true);
        usernameSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //This will run when user presses enter or submit, we don't need it
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //This will run when the entered string changes its characters, can be 1 or more. This we will use
                return false;
            }
        });

        return true;
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
