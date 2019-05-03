package com.Secret_Labs.secret_projectv10122;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.Secret_Labs.secret_projectv10122.databases.DatabaseHelper;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {

    RequestQueue loginQueue;
    Common common;
    Button loginButton;
    EditText username;

    SharedPreferences mainPrefs;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        common = new Common();
        dbHelper = new DatabaseHelper(this);
        mainPrefs = getSharedPreferences(common.mainPrefsName, 0);

        //Setting custom toolbar for the login activity
        Toolbar loginToolbar = (Toolbar) findViewById(R.id.loginToolbar);
        loginToolbar.setTitle(R.string.toolbar_title_login);
        setSupportActionBar(loginToolbar);

        //Adding a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Creating a requestqueue for the login activity
        loginQueue = Volley.newRequestQueue(this);

        //Making onclicklistener for the login button
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginV2(loginQueue);
            }
        });

        //Setting the username if intent has extra
        username = (EditText) findViewById(R.id.usernameEditText);
        Intent intent = getIntent();
        if(intent.hasExtra("suggestedUsername")){
            username.setText(intent.getStringExtra("suggestedUsername"));
        }
    }

    //New login method V2 to use common login
    private void loginV2(RequestQueue queue){
        //Checking whether the connection is true
        if(!mainPrefs.getBoolean("apiConnection", false)){
            common.displayToast(LoginActivity.this, "Login Failed: No connection to API");
            return;
        }

        //First getting references to the username and password from the fields
        final EditText password = (EditText) findViewById(R.id.passwordEditText);
        final CheckBox rememberCheckbox = (CheckBox) findViewById(R.id.rememberCheckBox);

        //Checking whether the fields are empty
        if(username.getText().toString().equals("") || password.getText().toString().equals("")){
            common.displayToast(LoginActivity.this, "Login Failed: Fields may not be empty");
            return;
        }

        //Getting fromWhereRemembered int from the checkbox
        int fromWhereRemember = 2;
        if(rememberCheckbox.isChecked()){
            fromWhereRemember = 1;
        }

        common.login(LoginActivity.this, queue, username.getText().toString(), password.getText().toString(), fromWhereRemember, true);
    }

    //Method that runs when back button is pressed
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
