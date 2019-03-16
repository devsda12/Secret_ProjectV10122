package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;
import com.Secret_Labs.secret_projectv10122.recyclerviews.OnclickListener_AccSelection;
import com.Secret_Labs.secret_projectv10122.recyclerviews.RecyclerAdapter_AccSelection;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

public class AccountSelection extends AppCompatActivity {

    //Class variables
    RecyclerView recyclerView;
    RecyclerAdapter_AccSelection adapter_accSelection;
    List<Obj_AccountInfo> acc_list;
    DatabaseHelper dbHelper;

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

    SwipeRefreshLayout sRLayout;
    TextView noAccTV;

    @Override
    protected void onResume(){
        super.onResume();
        refreshAccList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountselection);
        common = new Common();
        dbHelper = new DatabaseHelper(this);
        noAccTV = (TextView) findViewById(R.id.noAccountsTextView);

        //Setting the custom toolbar for our main activity
        Toolbar mainAcToolbar = (Toolbar) findViewById(R.id.acToolbar);
        mainAcToolbar.setTitle(getString(R.string.toolbar_title_account_selection));
        setSupportActionBar(mainAcToolbar);

        //Fab Button initialisation
        initializeFabMenu();

        //Swipe to refresh implementation
        sRLayout = findViewById(R.id.acc_SwipeRefresh);
        sRLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAccList();
                sRLayout.setRefreshing(false);
            }
        });

        //Testing the Api connection
        requestQueue = Volley.newRequestQueue(this);
        common.startUpConnect(this, requestQueue);

        //Recyclerview area
        //Declaration of the list to import into the recyclerview adapter
        acc_list = new ArrayList<>();

        //Defining the recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.Acc_Recyclerview);

        //Setting the layoutmanager (whaterver that may be) XD
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        //Adding the devider class object to the recyclerview
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), recyclerLayoutManager.getOrientation()));

        //Filling the list with the accounts
        acc_list = dbHelper.fetchAllStoredAccounts();

        //Importing the acquired list in the adapter
        adapter_accSelection = new RecyclerAdapter_AccSelection(this, acc_list, new OnclickListener_AccSelection() {
            @Override
            public void onItemClicked(int position) {
                loginOnAccPress(requestQueue, position);
            }

            @Override
            public void onItemRemoveClicked(int position) {
                boolean removeResult = dbHelper.removeAccount(acc_list.get(position).getAcc_Id());
                if(removeResult) {
                    acc_list.remove(position);
                    adapter_accSelection.notifyItemRemoved(position);
                    adapter_accSelection.notifyItemRangeChanged(position, adapter_accSelection.getItemCount() - position);
                    showNoAccTV();
                }
            }
        });

        //Coupling the adapter to the already present recyclerview
        recyclerView.setAdapter(adapter_accSelection);

        //Initial coupling of the adapter, future reloads should use the function refreshAccList
        if(acc_list.isEmpty()){
            noAccTV.setVisibility(View.VISIBLE);
        }
    }

    //These functions are for the refresh of the acc list
    private void refreshAccList(){
        //First update the list under here
        acc_list.clear();
        acc_list.addAll(dbHelper.fetchAllStoredAccounts());

        //Notifying the adapter the dataset has changed so it updates the index and inserts new items where possible
        adapter_accSelection.notifyDataSetChanged();

        //Checking if the text for no accounts should be shown
        showNoAccTV();
    }

    private void showNoAccTV(){
        if(acc_list.isEmpty() && noAccTV.getVisibility() == View.INVISIBLE){
            noAccTV.setVisibility(View.VISIBLE);
        } else if(!acc_list.isEmpty() && noAccTV.getVisibility() == View.VISIBLE){
            noAccTV.setVisibility(View.INVISIBLE);
        }
    }
    //End of the acc List refresh

    //Login function when acc is pressed
    private void loginOnAccPress(RequestQueue queue, int listPos){
        //First testing if the pressed acc has a stored pswd hash

    }

    //These functions are for the expandable fab menu
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

        newAccFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFab();
                Intent goToCreateAcc = new Intent(AccountSelection.this, CreateAccActivity.class);
                startActivity(goToCreateAcc);
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
            case R.id.action_accountselection_refresh:
                refreshAccList();
                return true;
            case R.id.action_accountselection_about:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //End of the toolbar menu
}
