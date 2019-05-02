package com.Secret_Labs.secret_projectv10122.dialog_popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.Secret_Labs.secret_projectv10122.R;

import java.util.ArrayList;

public class ChangeQuoteDialog extends AppCompatDialogFragment {

    private EditText newQuote;
    private TextView quoteLengthCounter;
    private myAccountDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View customLayoutView = inflater.inflate(R.layout.changequote_dialog_layout, null);

        builder.setView(customLayoutView)
                .setTitle(getString(R.string.changeQuoteDialogTitle))
                .setNegativeButton(getString(R.string.dialogNegativeButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.dialogPositiveButton), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<String> returnList = new ArrayList<>();
                        returnList.add(newQuote.getText().toString());
                        listener.applyNewVariables(returnList);
                    }
                });

        newQuote = customLayoutView.findViewById(R.id.changeQuoteEditText);
        quoteLengthCounter = customLayoutView.findViewById(R.id.quoteLengthCounterTextView);

        //Adding listener to detect char changes in the edittext
        newQuote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Dont need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                quoteLengthCounter.setText(s.length() + "/140");
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Dont need
            }
        });

        return builder.create();
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
