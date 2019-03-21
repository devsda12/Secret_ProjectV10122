package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
import com.Secret_Labs.secret_projectv10122.recyclerviews.OnclickListener_ConvSelection;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_AccSelection;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_ConvSelection;
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
import java.util.ListIterator;

public class ConvSelection extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Obj_ConvInfo> convSelList;
    List<Obj_ConvInfo> updateConvSelList;
    RecyclerAdapter_ConvSelection adapter_convSelection;
    TextView noConvTV;
    DatabaseHelper dbHelper;
    ProgressBar reloadPb;

    SwipeRefreshLayout sRLayout;

    RequestQueue requestQueue;

    Common common;
    SharedPreferences mainPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conv_selection);
        common = new Common();
        dbHelper = new DatabaseHelper(this);
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);
        noConvTV = (TextView) findViewById(R.id.noConvSel_Textview);

        //Setting the custom toolbar for the activity
        Toolbar convSelToolbar = (Toolbar) findViewById(R.id.convSel_Toolbar);
        convSelToolbar.setTitle(getString(R.string.toolbar_title_conv_selection));
        setSupportActionBar(convSelToolbar);

        //Setting the swipe refreshlayout for the activity
        sRLayout = (SwipeRefreshLayout) findViewById(R.id.convSel_SwipeRefresh);
        sRLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Refreshing from the local db
                refreshAdapter();

                sRLayout.setRefreshing(false);
            }
        });

        //Setting the onclicklistener for the add conv fab
        FloatingActionButton addConvFab = (FloatingActionButton) findViewById(R.id.convSel_AddConvFab);
        addConvFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToNewConv = new Intent(ConvSelection.this, NewConvSelection.class);
                startActivity(goToNewConv);
            }
        });

        //TestBUTTON
        Button testButton = (Button)findViewById(R.id.testButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConvSelection.this, Messenger.class);
                startActivity(intent);
            }
        });

        //Testing the Api connection
        requestQueue = Volley.newRequestQueue(this);
        common.startUpConnect(this, requestQueue);

        //Recyclerview area here
        //Defining the list over here
        convSelList = new ArrayList<>();
        updateConvSelList = new ArrayList<>();

        //First refreshing the conversations from the local database here
        refreshAdapter();

        //Defining the recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.convSel_Recyclerview);

        //Setting the layoutmanager
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        //Adding the devider class object to the recyclerview
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), recyclerLayoutManager.getOrientation()));

        //Importing the acquired list in the adapter
        adapter_convSelection = new RecyclerAdapter_ConvSelection(this, convSelList, new OnclickListener_ConvSelection() {
            @Override
            public void onItemClicked(int position) {

            }
        });

        //Coupling the adapter to the already present recyclerview
        recyclerView.setAdapter(adapter_convSelection);

        //Updating the convlist from online sources
        updateConvList(requestQueue);

        //Initial coupling of the adapter, future reloads should use the function refreshAccList
        if(convSelList.isEmpty()){
            noConvTV.setVisibility(View.VISIBLE);
        }
    }

    //The function to update the conversations that are presented to the user
    public void updateConvList(RequestQueue queue){
        //First displaying the progressbar
        reloadPb = (ProgressBar) findViewById(R.id.convSelProgressBar);
        reloadPb.setVisibility(View.VISIBLE);

        //First checking if the connection to the api is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(ConvSelection.this, "Refresh Failed! No connection to API");
            reloadPb.setVisibility(View.INVISIBLE);
            return;
        }

        //Now checking whether a value is present as acc_Id in sharedpreferences
        if(mainPrefs.getString("activeAccId", "none").equals("none")){
            common.displayToast(ConvSelection.this, "Refresh Failed! No account logged in");
            reloadPb.setVisibility(View.INVISIBLE);
            return;
        }

        //Now making the json object
        JSONArray tempRequestJsonArray = new JSONArray();
        JSONObject tempRequestJson = new JSONObject();
        try{
            tempRequestJson.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempRequestJson.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
            tempRequestJsonArray.put(tempRequestJson);
        } catch (JSONException e) {
            common.displayToast(ConvSelection.this, "Refresh Failed: JSON Exception occurred");
            reloadPb.setVisibility(View.INVISIBLE);
            return;
        }

        //Now making a request to the api to request the conversation list
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, common.apiUrl + "/sapp_getChats", tempRequestJsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Clearing the old update list
                updateConvSelList.clear();

                //Looping through JSONArray to insert it all into a list
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject tempJsonObject = response.getJSONObject(i);
                        String tempConvId = tempJsonObject.getString("table_Name");
                        String tempConvAccId = mainPrefs.getString("activeAccId", "none");
                        String tempConvPartnerId = tempJsonObject.getString("partner_Id");
                        String tempConvPartnerUsername = tempJsonObject.getString("partner_Username");
                        String tempConvLastMessage = tempJsonObject.getString("last_Message");
                        String tempConvLastMessageSender = tempJsonObject.getString("message_Sender");
                        String tempConvLastMessageDate = tempJsonObject.getString("message_Date");
                        updateConvSelList.add(new Obj_ConvInfo(tempConvId, tempConvAccId, tempConvPartnerId, tempConvPartnerUsername, tempConvLastMessage, tempConvLastMessageSender, tempConvLastMessageDate));
                    } catch (JSONException e){
                        common.displayToast(ConvSelection.this, "Refresh Failed: JSON Exception occurred");
                        reloadPb.setVisibility(View.INVISIBLE);
                        return;
                    }
                }

                //Inserting the new list into the database
                boolean insertResult = dbHelper.addConvThumbnails(updateConvSelList);

                if(!insertResult){
                    common.displayToast(ConvSelection.this,"Refresh Failed: Database insertion failed");
                }

                //For testing refreshing the adapter here
                refreshAdapter();
                reloadPb.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.displayToast(ConvSelection.this, "Refresh Failed: No conversations started yet");
                reloadPb.setVisibility(View.INVISIBLE);
            }
        });

        //Adding request to the queue
        queue.add(jsonArrayRequest);
    }

    //Function to refresh the adapter
    private void refreshAdapter(){
        //Clearing the list and adding all values from db
        convSelList.clear();
        convSelList.addAll(dbHelper.fetchAllConvThumbnails(mainPrefs.getString("activeAccId", "none")));

        //Telling adapter the dataset has changed
        if(adapter_convSelection != null) {
            adapter_convSelection.notifyDataSetChanged();
        }

        //Checking if the tv should be displayed
        showNoConvTV();
    }

    //Function to show or not show the noConvTV
    private void showNoConvTV(){
        if(convSelList.isEmpty() && noConvTV.getVisibility() == View.INVISIBLE){
            noConvTV.setVisibility(View.VISIBLE);
        } else if(!convSelList.isEmpty() && noConvTV.getVisibility() == View.VISIBLE){
            noConvTV.setVisibility(View.INVISIBLE);
        }
    }

    //These functions are for the toolbar and the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_convselection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_convsel_refresh:
                refreshAdapter();
                return true;
            case R.id.action_convsel_about:
                return true;
            case R.id.action_convsel_settings:
                return true;
            case R.id.action_convsel_logout:
                common.logout(ConvSelection.this, requestQueue, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //End of the toolbar menu

    @Override
    public void onBackPressed(){
        //Do nothing
    }
}
