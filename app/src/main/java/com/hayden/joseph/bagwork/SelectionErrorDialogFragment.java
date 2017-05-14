package com.hayden.joseph.bagwork;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Joseph on 7/22/2016.
 */
public class SelectionErrorDialogFragment extends DialogFragment {

    public static SelectionErrorDialogFragment newInstance(int title) {
        SelectionErrorDialogFragment frag = new SelectionErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_setup_nothing_selected)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // dialog closes
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}