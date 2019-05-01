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

public class ChangeQuoteDialog extends AppCompatDialogFragment {

    private EditText newQuote;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customLayoutView = inflater.inflate(R.layout.changequote_dialog_layout, null);

        builder.setView(customLayoutView)
                .setTitle()
                .setNegativeButton(, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        newQuote = customLayoutView.findViewById(R.id.changeQuoteEditText);

        return builder.create();
    }
}
