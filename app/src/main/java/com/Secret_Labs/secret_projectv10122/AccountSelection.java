package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class AccountSelection extends AppCompatActivity {

    //Class variables
    RecyclerView recyclerView;
    RecyclerAdapter_AccSelection adapter_accSelection;
    List<Obj_AccountInfo> acc_list;

    FloatingActionButton plusFab;
    ConstraintLayout existingAccCL;
    FloatingActionButton existingAccFab;
    TextView existingAccTv;
    ConstraintLayout newAccCL;
    FloatingActionButton newAccFab;
    TextView newAccTv;
    Boolean isFabOpen = false;

    Common common;

    RequestQueue requestQueue;
    Boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountselection);
        common = new Common();

        //Setting the custom toolbar for our main activity
        Toolbar mainAcToolbar = (Toolbar) findViewById(R.id.acToolbar);
        mainAcToolbar.setTitle(getString(R.string.toolbar_title_account_selection));
        setSupportActionBar(mainAcToolbar);

        //Fab Button initialisation
        initializeFabMenu();

        //Testing the Api connection
        requestQueue = Volley.newRequestQueue(this);
        common.testApiConnection(this, requestQueue);

        //Recyclerview area
        //Declaration of the list to import into the recyclerview adapter
        acc_list = new ArrayList<>();

        //Defining the recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.Acc_Recyclerview);

        //Setting the layoutmanager (whaterver that may be) XD
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Defining a temporary list
        acc_list.add(new Obj_AccountInfo(1, "Test_User1", "1234", false, "12-01-2019, 12:30"));
        acc_list.add(new Obj_AccountInfo(2, "Test_User2", "1234", false, "12-01-2019, 12:30"));
        acc_list.add(new Obj_AccountInfo(3, "Test_User3", "1234", false, "12-01-2019, 12:30"));

        //Importing the acquired list in the adapter
        adapter_accSelection = new RecyclerAdapter_AccSelection(this, acc_list);

        //Coupling the adapter to the already present recyclerview
        recyclerView.setAdapter(adapter_accSelection);
    }

    //These functions are for the volley api requests


    //This function is for the expandable fab menu
    private void initializeFabMenu(){
        plusFab = (FloatingActionButton) findViewById(R.id.plusFab);
        existingAccCL = (ConstraintLayout) findViewById(R.id.addExistingAccCL);
        existingAccFab = (FloatingActionButton) findViewById(R.id.addExistingAccFab);
        existingAccTv = (TextView) findViewById(R.id.addExistingAccTV);
        newAccCL = (ConstraintLayout) findViewById(R.id.addNewAccCL);
        newAccFab = (FloatingActionButton) findViewById(R.id.addNewAccFab);
        newAccTv = (TextView) findViewById(R.id.addNewAccTv);

        //Setting listener on the plus fab
        plusFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isFabOpen){
                    openFab();
                } else {
                    closeFab();
                }
            }
        });

        //Click listeners of the sub fab's
        existingAccFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFab();
                Intent goToLogin = new Intent(AccountSelection.this, LoginActivity.class);
                startActivity(goToLogin);
            }
        });
    }

    //Function to open fab
    private void openFab(){
        isFabOpen = true;

        //Animating the menu
        plusFab.animate().rotationBy(135);
        existingAccCL.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_upper));
        newAccCL.animate().translationY(-getResources().getDimension(R.dimen.fab_menu_middle));

        //Making text visible
        existingAccTv.setVisibility(View.VISIBLE);
        newAccTv.setVisibility(View.VISIBLE);

        //Making fab's focusable and clickable
        existingAccFab.setClickable(true);
        existingAccFab.setFocusable(true);
        newAccFab.setClickable(true);
        newAccFab.setFocusable(true);
    }

    //Function to close fab
    private void closeFab(){
        isFabOpen = false;
        plusFab.animate().rotation(0);

        //Making fab's not focusable and clickable
        existingAccFab.setClickable(false);
        existingAccFab.setFocusable(false);
        newAccFab.setClickable(false);
        newAccFab.setFocusable(false);

        //Making text invisible
        existingAccTv.setVisibility(View.INVISIBLE);
        newAccTv.setVisibility(View.INVISIBLE);

        //Animating fab's back into place
        existingAccCL.animate().translationY(0);
        newAccCL.animate().translationY(0);
    }
    //End of the expandable fab menu

    //These functions are for the toolbar and the toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_accountselection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                return true;
            case R.id.action_about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //End of the toolbar menu
}
