package com.Secret_Labs.secret_projectv10122;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ConvSelection extends AppCompatActivity {

    SwipeRefreshLayout sRLayout;

    RequestQueue requestQueue;

    Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conv_selection);
        common = new Common();

        //Setting the custom toolbar for the activity
        Toolbar convSelToolbar = (Toolbar) findViewById(R.id.convSel_Toolbar);
        convSelToolbar.setTitle(getString(R.string.toolbar_title_conv_selection));
        setSupportActionBar(convSelToolbar);

        //Setting the swipe refreshlayout for the activity
        final SwipeRefreshLayout sRLayout = (SwipeRefreshLayout) findViewById(R.id.convSel_SwipeRefresh);
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
