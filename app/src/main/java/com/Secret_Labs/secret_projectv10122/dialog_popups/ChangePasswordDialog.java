package com.Secret_Labs.secret_projectv10122.dialog_popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.Account;
import com.Secret_Labs.secret_projectv10122.Common;
import com.Secret_Labs.secret_projectv10122.R;

import java.util.ArrayList;

public class ChangePasswordDialog extends AppCompatDialogFragment {

    private EditText oldPassword;
    private EditText newPassword1;
    private EditText newPassword2;
    TextView errorMessageText;

    private myAccountDialogListener listener;


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
                .setPositiveButton(R.string.dialogPositiveButton, null);

        final Dialog returnDialog = builder.create();

        //Show listener for the custom submit button
        returnDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button customSubmitButton = ((AlertDialog) returnDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                customSubmitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // perform password checks
                        if(checkPass()){
                            ArrayList<String> returnList = new ArrayList<>();
                            returnList.add(oldPassword.getText().toString());
                            returnList.add(newPassword1.getText().toString());
                            returnList.add(newPassword2.getText().toString());
                            listener.applyNewVariables(returnList);
                            returnDialog.dismiss();
                        }
                    }
                });
            }
        });

        oldPassword = customLayoutView.findViewById(R.id.oldPasswordEditText);
        newPassword1 = customLayoutView.findViewById(R.id.newPasswordEditText);
        newPassword2 = customLayoutView.findViewById(R.id.newPassword2EditText);
        errorMessageText = customLayoutView.findViewById(R.id.changePassTextView);

        return returnDialog;
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
        errorMessageText.setText("");
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (myAccountDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}