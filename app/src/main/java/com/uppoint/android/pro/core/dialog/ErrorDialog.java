package com.uppoint.android.pro.core.dialog;

import com.uppoint.android.pro.R;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;

/**
 */
public class ErrorDialog extends BaseDialogFragment {

    public static final String TAG = "error_dialog";

    public static ErrorDialog newInstance(@StringRes int messageResId) {
        final ErrorDialog errorDialog = new ErrorDialog();

        final Bundle args = new Bundle();
        args.putInt(ARG_TITLE, R.string.error_dialog_title);
        args.putInt(ARG_MESSAGE, messageResId);
        errorDialog.setArguments(args);

        return errorDialog;
    }

    @Override
    protected Pair<Integer, DialogInterface.OnClickListener> getPositiveButton() {
        return new Pair<>(R.string.error_dialog_ok_button, null);
    }
}
