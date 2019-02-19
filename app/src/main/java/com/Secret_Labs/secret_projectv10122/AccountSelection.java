package com.Secret_Labs.secret_projectv10122;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.Secret_Labs.secret_projectv10122.models.Obj_AccountInfo;

import java.util.ArrayList;
import java.util.List;

public class AccountSelection extends AppCompatActivity {

    //Class variables
    RecyclerView recyclerView;
    RecyclerAdapter_AccSelection adapter_accSelection;
    List<Obj_AccountInfo> acc_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountselection);

        //Setting the custom toolbar for our main activity
        Toolbar mainAcToolbar = (Toolbar) findViewById(R.id.acToolbar);
        mainAcToolbar.setTitle(getString(R.string.toolbar_title_account_selection));
        setSupportActionBar(mainAcToolbar);

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
}
