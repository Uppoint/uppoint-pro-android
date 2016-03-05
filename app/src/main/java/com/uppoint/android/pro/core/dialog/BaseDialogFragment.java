package com.uppoint.android.pro.core.dialog;

import com.uppoint.android.pro.core.util.Preconditions;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;

/**
 */
public abstract class BaseDialogFragment extends DialogFragment {

    protected static final int NONE = -1;

    protected static final String ARG_TITLE = "arg_title";
    protected static final String ARG_MESSAGE = "arg_message";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        Preconditions.stateNonNull(args, "Required initialization parameters missing");

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        setTitle(args, builder);
        setMessage(args, builder);
        setPositiveButton(builder);
        setNegativeButton(builder);
        setNeutralButton(builder);
        return builder.create();
    }

    protected Pair<Integer, DialogInterface.OnClickListener> getPositiveButton() {
        return null;
    }

    protected Pair<Integer, DialogInterface.OnClickListener> getNegativeButton() {
        return null;
    }

    protected Pair<Integer, DialogInterface.OnClickListener> getNeutralButton() {
        return null;
    }

    private void setTitle(Bundle args, AlertDialog.Builder builder) {
        final int titleResId = args.getInt(ARG_TITLE, NONE);
        if (NONE != titleResId) {
            builder.setTitle(titleResId);
        }
    }

    private void setMessage(Bundle args, AlertDialog.Builder builder) {
        final int messageResId = args.getInt(ARG_MESSAGE, NONE);
        if (NONE == messageResId) {
            throw new IllegalStateException("Required message missing");
        }
        builder.setMessage(messageResId);
    }

    private void setPositiveButton(AlertDialog.Builder builder) {
        final Pair<Integer, DialogInterface.OnClickListener> positivePair = getPositiveButton();
        if (positivePair != null) {
            builder.setPositiveButton(positivePair.first, positivePair.second);
        }
    }

    private void setNegativeButton(AlertDialog.Builder builder) {
        final Pair<Integer, DialogInterface.OnClickListener> positivePair = getNegativeButton();
        if (positivePair != null) {
            builder.setNegativeButton(positivePair.first, positivePair.second);
        }
    }

    private void setNeutralButton(AlertDialog.Builder builder) {
        final Pair<Integer, DialogInterface.OnClickListener> positivePair = getNeutralButton();
        if (positivePair != null) {
            builder.setNeutralButton(positivePair.first, positivePair.second);
        }
    }
}
