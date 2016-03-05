package com.uppoint.android.pro.core.dialog;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.core.util.Preconditions;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 */
public class ProgressDialog extends BaseDialogFragment {

    public static final String TAG = "progress_dialog";

    public static ProgressDialog newInstance(@StringRes int messageResId) {
        final ProgressDialog progressDialog = new ProgressDialog();

        final Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE, messageResId);
        progressDialog.setArguments(args);

        return progressDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Preconditions.stateNonNull(getArguments(), "Required initialization arguments missing");

        final int messageResId = getArguments().getInt(ARG_MESSAGE, NONE);
        if (NONE == messageResId) {
            throw new IllegalStateException("Required message missing");
        }

        @SuppressLint("InflateParams") final View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_progress, null);
        final TextView messageView = (TextView) view.findViewById(R.id.dialog_progress_message);
        messageView.setText(messageResId);

        return new AlertDialog.Builder(getContext()).setView(view).create();
    }
}
