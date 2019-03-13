package com.Secret_Labs.secret_projectv10122;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.models.Obj_ConvInfo;
import com.Secret_Labs.secret_projectv10122.recyclerviews.OnclickListener_ConvSelection;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_AccSelection;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_ConvSelection;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ConvSelection extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Obj_ConvInfo> convSelList;
    RecyclerAdapter_ConvSelection adapter_convSelection;
    TextView noConvTV;

    SwipeRefreshLayout sRLayout;

    RequestQueue requestQueue;

    Common common;
    SharedPreferences mainPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conv_selection);
        common = new Common();
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
                //Put refreshing method here
                sRLayout.setRefreshing(false);
            }
        });

        //Testing the Api connection
        requestQueue = Volley.newRequestQueue(this);
        common.startUpConnect(this, requestQueue);

        //First refreshing the conversations from the database here


        //Recyclerview area here
        //Defining the list over here
        convSelList = new ArrayList<>();

        //Defining the recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.convSel_Recyclerview);

        //Setting the layoutmanager
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        //Adding the devider class object to the recyclerview
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), recyclerLayoutManager.getOrientation()));

        //Initial coupling of the adapter, future reloads should use the function refreshAccList
        if(convSelList.isEmpty()){
            noConvTV.setVisibility(View.VISIBLE);
        } else{
            //Importing the acquired list in the adapter
            adapter_convSelection = new RecyclerAdapter_ConvSelection(this, convSelList, new OnclickListener_ConvSelection() {
                @Override
                public void onItemClicked(int position) {

                }
            });

            //Coupling the adapter to the already present recyclerview
            recyclerView.setAdapter(adapter_convSelection);
        }
    }

    //The function to update the conversations that are presented to the user
    public List<Obj_ConvInfo> updateConvList(RequestQueue queue){
        //First checking if the connection to the api is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(ConvSelection.this, "Refresh failed! No connection to API");
            return new ArrayList<>();
        }

        //Now checking whether the
        return new ArrayList<>();
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
                return true;
            case R.id.action_convsel_about:
                return true;
            case R.id.action_convsel_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //End of the toolbar menu
}
