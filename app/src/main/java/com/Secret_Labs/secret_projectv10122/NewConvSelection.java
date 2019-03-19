package com.Secret_Labs.secret_projectv10122;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.listview_adapters.UsernameSelectionAdapter;
import com.Secret_Labs.secret_projectv10122.models.Obj_Usersearch;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class NewConvSelection extends AppCompatActivity {

    SearchView usernameSearchView;
    List<Obj_Usersearch> usernameList;
    ListView listView;
    UsernameSelectionAdapter usernameAdapter;
    ProgressBar pBar;
    TextView messageTV;

    RequestQueue usernameSearchQueue;

    Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conv_selection);
        common = new Common();

        //Setting the custom toolbar for the activity
        Toolbar createAccToolbar = (Toolbar) findViewById(R.id.createAccToolbar);
        createAccToolbar.setTitle(R.string.toolbar_title_newconv_selection);
        setSupportActionBar(createAccToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Defining the requestqueue for the activity and once again testing for connection
        usernameSearchQueue = Volley.newRequestQueue(this);
        common.testApiConnection(this, usernameSearchQueue);

        //Listview items under here
        pBar = (ProgressBar) findViewById(R.id.newConvSelPB);
        messageTV = (TextView) findViewById(R.id.newConvSelTV);
        listView = (ListView) findViewById(R.id.newConvSelLV);

        //First filling the list of username with usernames and quotes
        usernameList = new ArrayList<>();
        usernameList.add(new Obj_Usersearch("piet", "hello quote", "1234"));
        usernameList.add(new Obj_Usersearch("piet", "hello quote", "1234"));

        //Filling and setting the adapter for the arraylist
        usernameAdapter = new UsernameSelectionAdapter(this, R.layout.usernameinfo_layout, usernameList);
        listView.setAdapter(usernameAdapter);

        //If the list if empty display the textview
        if(usernameList.isEmpty()){
            messageTV.setVisibility(View.VISIBLE);
            messageTV.setText(getString(R.string.newconvsel_start_search));
        }
    }

    //Method to fetch the latest usernames and corresponding acc_Id's from the server
    public void fetchUsernames(){
        //Enabling the loading wheel
        pBar.setVisibility(View.VISIBLE);


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
