package com.Secret_Labs.secret_projectv10122.dialog_popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.Account;
import com.Secret_Labs.secret_projectv10122.Common;
import com.Secret_Labs.secret_projectv10122.R;

public class ChangePasswordDialog extends AppCompatDialogFragment {

    private EditText oldPassword;
    private EditText newPassword1;
    private EditText newPassword2;
    TextView errorMessageText;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customLayoutView = inflater.inflate(R.layout.changepassword_dialog_layout, null);

        builder.setView(customLayoutView)
                .setTitle(getString(R.string.changePasswordDialogTitle))
                .setNegativeButton(R.string.dialogNegativeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.dialogPositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // perform password checks
                        if(checkPass()){
                            // do stuff
                        }
                    }
                });

        oldPassword = customLayoutView.findViewById(R.id.oldPasswordEditText);
        newPassword1 = customLayoutView.findViewById(R.id.newPasswordEditText);
        newPassword2 = customLayoutView.findViewById(R.id.newPassword2EditText);
        errorMessageText = customLayoutView.findViewById(R.id.changePassTextView);

        return builder.create();
    }

    public boolean checkPass(){
        //Checking whether the fields are empty
        if(oldPassword.getText().toString().equals("") || newPassword1.getText().toString().equals("") || newPassword2.getText().toString().equals("")){
            errorMessageText.setText("Text fields can't be empty!");
            return false;
        }
        //Checking whether the password fields match
        if(!newPassword1.getText().toString().equals(newPassword2.getText().toString())){
           errorMessageText.setText("Passwords do not match");
            return false;
        }
        //Check if old password is the same as new password
        if(newPassword1.getText().toString().equals(oldPassword.getText().toString())){
            errorMessageText.setText("New Password is the same as old password");
            return false;
        }
        return true;
    }
}