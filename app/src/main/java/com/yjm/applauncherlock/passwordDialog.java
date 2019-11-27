package com.yjm.applauncherlock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

public class passwordDialog extends AppCompatDialogFragment {
    private EditText inputPassword;
    private EnterPasswordListener listner;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Enter Password")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = inputPassword.getText().toString();
                        listner.applyTexts(password);
                    }
                });
        inputPassword = view.findViewById(R.id.input_password);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listner = (EnterPasswordListener) context;

        }
        catch (ClassCastException e){
           throw new ClassCastException(context.toString() + "must implement EnterPasswordListener");
        }
    }

    public interface EnterPasswordListener{
        void applyTexts(String password);
    }
}
