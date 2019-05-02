package com.Secret_Labs.secret_projectv10122.dialog_popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.Secret_Labs.secret_projectv10122.R;

public class ChangePasswordDialog extends AppCompatDialogFragment {

    private EditText oldPassword;
    private EditText newPassword1;
    private EditText newPassword2;

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

                    }
                });

        oldPassword = customLayoutView.findViewById(R.id.oldPasswordEditText);
        newPassword1 = customLayoutView.findViewById(R.id.newPasswordEditText);
        newPassword2 = customLayoutView.findViewById(R.id.newPassword2EditText);

        return builder.create();
    }
}
