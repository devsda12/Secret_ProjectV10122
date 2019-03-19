package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    SharedPreferences mainPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conv_selection);
        common = new Common();
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);

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
        //usernameList.add(new Obj_Usersearch("piet", "hello quote", "1234"));
        //usernameList.add(new Obj_Usersearch("piet", "hello quote", "1234"));

        //Filling and setting the adapter for the arraylist
        usernameAdapter = new UsernameSelectionAdapter(this, R.layout.usernameinfo_layout, usernameList);
        listView.setAdapter(usernameAdapter);

        //If the list if empty display the textview
        showNoNewConvTV("");
    }

    //Method to fetch the latest usernames and corresponding acc_Id's from the server
    public void fetchUsernames(RequestQueue queue, final String searchString){
        //Checking if the search string is actually more than 3 chars
        if(searchString.length() < 3){
            usernameList.clear();
            usernameAdapter = new UsernameSelectionAdapter(this, R.layout.usernameinfo_layout, usernameList);
            listView.setAdapter(usernameAdapter);
            showNoNewConvTV(searchString);
            return;
        }

        //Enabling the loading wheel
        if(pBar.getVisibility() == View.INVISIBLE) {
            pBar.setVisibility(View.VISIBLE);
        }

        //First checking if the connection to the api is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(NewConvSelection.this, "Refresh Failed! No connection to API");
            if(pBar.getVisibility() == View.VISIBLE) {
                pBar.setVisibility(View.INVISIBLE);
            }
            return;
        }

        //Now checking whether a value is present as acc_Id in sharedpreferences
        if(mainPrefs.getString("activeAccId", "none").equals("none")){
            common.displayToast(NewConvSelection.this, "Refresh Failed! No account logged in");
            if(pBar.getVisibility() == View.VISIBLE) {
                pBar.setVisibility(View.INVISIBLE);
            }
            return;
        }

        //Now making the json object
        JSONArray tempRequestJsonArray = new JSONArray();
        JSONObject tempRequestJson = new JSONObject();
        try{
            tempRequestJson.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempRequestJson.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
            tempRequestJson.put("acc_Username", searchString);
            tempRequestJsonArray.put(tempRequestJson);
        } catch (JSONException e) {
            common.displayToast(NewConvSelection.this, "Refresh Failed: JSON Exception occurred");
            if(pBar.getVisibility() == View.VISIBLE) {
                pBar.setVisibility(View.INVISIBLE);
            }
            return;
        }

        //Now making the actual request
        JsonArrayRequest fetchUsernamesRequest = new JsonArrayRequest(Request.Method.POST, common.apiUrl + "/sapp_findUser", tempRequestJsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //First clearing the old username list
                usernameList.clear();

                //Looping through JSONArray to insert it all into a list
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject tempJsonObject = response.getJSONObject(i);
                        String tempUsername = tempJsonObject.getString("acc_Username");
                        String tempUserQuote = "This is the default quote! Hi!";
                        String tempAccId = tempJsonObject.getString("acc_Id");
                        usernameList.add(new Obj_Usersearch(tempUsername, tempUserQuote, tempAccId));
                    } catch (JSONException e){
                        common.displayToast(NewConvSelection.this, "Refresh Failed: JSON Exception occurred");
                        if(pBar.getVisibility() == View.VISIBLE) {
                            pBar.setVisibility(View.INVISIBLE);
                        }
                        return;
                    }
                }

                //Setting the newly list into the adapter
                usernameAdapter = new UsernameSelectionAdapter(NewConvSelection.this, R.layout.usernameinfo_layout, usernameList);
                listView.setAdapter(usernameAdapter);
                if(pBar.getVisibility() == View.VISIBLE) {
                    pBar.setVisibility(View.INVISIBLE);
                }
                showNoNewConvTV(searchString);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(pBar.getVisibility() == View.VISIBLE) {
                    pBar.setVisibility(View.INVISIBLE);
                }
                showNoNewConvTV(searchString);
            }
        });

        queue.add(fetchUsernamesRequest);

    }

    //Method for updating the textview
    private void showNoNewConvTV(String searchString){
        if(usernameList.isEmpty() && messageTV.getVisibility() == View.INVISIBLE){
            messageTV.setVisibility(View.VISIBLE);
        } else if(!usernameList.isEmpty() && messageTV.getVisibility() == View.VISIBLE){
            messageTV.setVisibility(View.INVISIBLE);
        }

        //Setting content of the string according to its length
        if(searchString.length() == 0){
            messageTV.setText(getString(R.string.newconvsel_start_search));
        } else if(searchString.length() < 3){
            messageTV.setText(getString(R.string.newconvsel_less_3_chars));
        } else {
            messageTV.setText(getString(R.string.newconvsel_no_matching_usernames));
        }
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
                fetchUsernames(usernameSearchQueue, s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //This will run when the entered string changes its characters, can be 1 or more. This we will use
                fetchUsernames(usernameSearchQueue, s);
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
