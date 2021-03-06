package com.Secret_Labs.secret_projectv10122;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
import com.Secret_Labs.secret_projectv10122.recyclerviews.OnclickListener_ConvSelection;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_ConvSelection;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    protected void onResume(){
        super.onResume();
        mainPrefs.edit().putBoolean("convSelActive", true).commit();
        registerReceiver(notificationReceiver, new IntentFilter("convSelBroadcast"));
        Log.d("ConvSelection", "On resume called");
        updateConvList(requestQueue);
    }

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
                //Refreshing from the external server
                updateConvList(requestQueue);

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
                //First getting the conversation ID and partner name from the convSelList list
                String pressedConvId = convSelList.get(position).getConv_Id();
                String pressedPartnerName = convSelList.get(position).getConvPartner_Username();

                //Making and starting the intent
                Intent goToMessenger = new Intent(ConvSelection.this, Messenger.class);
                goToMessenger.putExtra("partnerUsername", pressedPartnerName);
                goToMessenger.putExtra("conv_Id", pressedConvId);
                startActivity(goToMessenger);
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
    public void updateConvList(final RequestQueue queue){
        Log.d("ConvSelection", "UpdateConvList called");
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

        //Getting the current conv_Id's with profile pic id's from the database
        List<Obj_ConvInfo> templist = dbHelper.fetchConvIdWithProfilePicId(mainPrefs.getString("activeAccId", "none"));

        //Now making the json object
        JSONArray tempRequestJsonArray = new JSONArray();
        JSONObject tempRequestJson = new JSONObject();
        try{
            tempRequestJson.put("device_Id", mainPrefs.getString("device_Id", "0"));
            tempRequestJson.put("acc_Id", mainPrefs.getString("activeAccId", "none"));
            tempRequestJsonArray.put(tempRequestJson);

            for(int i = 0; i < templist.size(); i++){
                JSONObject tempJsonObject = new JSONObject();
                tempJsonObject.put("conv_Id", templist.get(i).getConv_Id());

                //Checking if null
                if(templist.get(i).getConvPartner_ProfilePicId() == null){
                    tempJsonObject.put("profilePic_Id", "null");
                } else {
                    tempJsonObject.put("profilePic_Id", templist.get(i).getConvPartner_ProfilePicId());
                }
                tempRequestJsonArray.put(tempJsonObject);
            }
        } catch (JSONException e) {
            common.displayToast(ConvSelection.this, "Refresh Failed: JSON Exception occurred");
            reloadPb.setVisibility(View.INVISIBLE);
            return;
        }

        //Now making a request to the api to request the conversation list
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, common.apiUrl + "/sapp_getChats", tempRequestJsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("ConvSelection", "On response of conv info request");
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
                        String tempConvPartnerQuote = tempJsonObject.getString("partner_Quote");
                        String tempConvNewProfileId = tempJsonObject.getString("newProfilePictureId");
                        String tempConvLastMessage = tempJsonObject.getString("last_Message");
                        String tempConvLastMessageSender = tempJsonObject.getString("message_Sender");
                        String tempConvLastMessageDate = tempJsonObject.getString("message_Date");

                        //If the new profile picture ID is something else than null a new image will be requested
                        if (!tempConvNewProfileId.equals("null") && !tempConvNewProfileId.equals("None")){
                            requestProfilePicture(queue, tempConvId, tempConvNewProfileId);
                        }

                        updateConvSelList.add(new Obj_ConvInfo(tempConvId, tempConvAccId, tempConvPartnerId, tempConvPartnerUsername, tempConvPartnerQuote, null, tempConvNewProfileId, tempConvLastMessage, tempConvLastMessageSender, tempConvLastMessageDate));
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

                //For now over here syncing the complete message tables
                Log.d("ConvSelection", "Executing tablefillerrequestmaker");
                common.tableFillerRequestmaker(ConvSelection.this, queue, convSelList, mainPrefs.getString("activeAccId", "none"), mainPrefs.getString("device_Id", "none"), false);
                reloadPb.setVisibility(View.INVISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(common.serverDebugToasts) {
                    common.displayToast(ConvSelection.this, "Refresh Failed: No conversations started yet");
                }
                reloadPb.setVisibility(View.INVISIBLE);
            }
        });

        //Adding request to the queue
        queue.add(jsonArrayRequest);
    }

    //Function to request the profile pictures
    private void requestProfilePicture(RequestQueue queue, final String conv_Id, final String profilePicId){
        //Making the request
        ImageRequest profilePicRequest = new ImageRequest(common.apiUrl + "/sapp_getProfilePic/" + profilePicId, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Log.d("ConvSelection", "Storing image on response of imagerequest");
                dbHelper.storeConvProfilePicture(common.getBitmapAsByteArray(response), profilePicId, conv_Id);
                refreshAdapter();
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ConvSelection", "Error on response of the imagerequest");
            }
        });

        queue.add(profilePicRequest);
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
                //Refreshing from the external server
                updateConvList(requestQueue);
                return true;
            case R.id.action_convsel_about:
                startActivity(new Intent(ConvSelection.this, aboutActivity.class));
                return true;
            case R.id.action_convsel_settings:
                startActivity(new Intent(ConvSelection.this, SettingsActivity.class));
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

    //Broadcast receiver under here
    public BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ConvSelection", "Broadcast received from service, now refreshing");
            updateConvList(requestQueue);
        }
    };

    //Methods under here are to change the sharedpreferences when the activity is no more active
    @Override
    protected void onPause(){
        super.onPause();
        mainPrefs.edit().putBoolean("convSelActive", false).commit();
        unregisterReceiver(notificationReceiver);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mainPrefs.edit().putBoolean("convSelActive", false).commit();
    }
}
