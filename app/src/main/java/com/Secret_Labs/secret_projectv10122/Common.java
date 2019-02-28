package com.Secret_Labs.secret_projectv10122;

import android.content.Context;
import android.widget.Toast;

public class Common {

    //Method to display toasts
    public void displayToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
