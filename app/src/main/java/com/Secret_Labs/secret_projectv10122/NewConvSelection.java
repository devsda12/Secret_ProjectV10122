package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.listview_adapters.UsernameSelectionAdapter;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
import com.Secret_Labs.secret_projectv10122.models.Obj_Usersearch;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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
        Toolbar newConvSelToolbar = (Toolbar) findViewById(R.id.newConvSelToolbar);
        newConvSelToolbar.setTitle(R.string.toolbar_title_newconv_selection);
        setSupportActionBar(newConvSelToolbar);

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

        //Filling and setting the adapter for the arraylist
        usernameAdapter = new UsernameSelectionAdapter(this, R.layout.usernameinfo_layout, usernameList);
        listView.setAdapter(usernameAdapter);

        //If the list if empty display the textview
        showNoNewConvTV("");
    }

    //Method to fetch the latest usernames and corresponding acc_Id's from the server
    public void fetchUsernames(final RequestQueue queue, final String searchString){
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
                        if(!tempAccId.equals(mainPrefs.getString("activeAccId", "none"))) {
                            usernameList.add(new Obj_Usersearch(tempUsername, tempUserQuote, tempAccId));
                        }
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

                //Setting the clicklistener on the new elements
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Make the onclick of the username
                        createNewConvTable(queue, usernameList.get(position).getUserAccId(), usernameList.get(position).getUsername());
                    }
                });

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

    //Method to send a commit of a username for a new conversation to the server
    private void createNewConvTable(final RequestQueue queue, final String partnerId, final String partnerUsername){
        //First checking if the connection to the api is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(NewConvSelection.this, "Conversation Creation Failed! No connection to API");
            return;
        }

        //Now checking whether a value is present as acc_Id in sharedpreferences
        if(mainPrefs.getString("activeAccId", "none").equals("none")){
            common.displayToast(NewConvSelection.this, "Conversation Creation Failed! No account logged in");
            return;
        }

        //Now making the request object
        JSONObject createNewTableRequestOBJ = new JSONObject();
        try{
            createNewTableRequestOBJ.put("device_Id", mainPrefs.getString("device_Id", "0"));
            createNewTableRequestOBJ.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
            createNewTableRequestOBJ.put("partner_Id", partnerId);
        } catch (JSONException e) {
            common.displayToast(NewConvSelection.this, "Conversation Creation Failed! Json Exception occurred");
            return;
        }

        JsonObjectRequest tempRequest = new JsonObjectRequest(Request.Method.POST, common.apiUrl + "/sapp_createTable", createNewTableRequestOBJ, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("insertResult").equals("true")){
                        common.displayToast(NewConvSelection.this, "Conversation table created succesfully");

                        //Now creating the local table
                        List<Obj_ConvInfo> tempInsertlist = new ArrayList<>();
                        Obj_ConvInfo tempConvInfoObj = new Obj_ConvInfo(mainPrefs.getString("activeAccId", "none") + partnerId, null, null, null, null, null, null, null, null);
                        tempInsertlist.add(tempConvInfoObj);

                        //Inserting the local table
                        common.tableFillerRequestmaker(NewConvSelection.this, queue, tempInsertlist, mainPrefs.getString("activeAccId", "none"), mainPrefs.getString("device_Id", "0"), true);

                        //Starting the messenger
                        Intent goToMessenger = new Intent(NewConvSelection.this, Messenger.class);
                        goToMessenger.putExtra("partnerUsername", partnerUsername);
                        goToMessenger.putExtra("conv_Id", mainPrefs.getString("activeAccId", "none") + partnerId);
                        startActivity(goToMessenger);
                        finish();
                    }
                } catch (JSONException e) {
                    common.displayToast(NewConvSelection.this, "Conversation Creation Failed! Json Exception occurred");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.displayToast(NewConvSelection.this, "Conversation Creation Failed! The server denied the request");
            }
        });

        queue.add(tempRequest);
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
        usernameSearchView.setIconified(false);
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
