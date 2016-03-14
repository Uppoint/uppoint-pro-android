package com.uppoint.android.pro.calendar.fragment;

import com.uppoint.android.pro.core.util.Preconditions;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

/**
 */
public class DatePickerDialog extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {

    public static final String TAG = "date_picker";

    private static final String ARG_ID = "arg_id";
    private static final String ARG_YEAR = "arg_year";
    private static final String ARG_MONTH = "arg_month";
    private static final String ARG_DAY = "arg_day";

    private Callback mCallback;

    public static DatePickerDialog newInstance(int id, int year, int month, int day) {
        final Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_DAY, day);

        final DatePickerDialog dialog = new DatePickerDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle args = getArguments();
        Preconditions.stateNonNull(args, "Required arguments missing");

        final int year = args.getInt(ARG_YEAR);
        final int month = args.getInt(ARG_MONTH);
        final int day = args.getInt(ARG_DAY);

        return new android.app.DatePickerDialog(getContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (mCallback != null) {
            mCallback.onDateSet(getArguments().getInt(ARG_ID), year, monthOfYear, dayOfMonth);
        }
    }

    public interface Callback {

        void onDateSet(int id, int year, int month, int day);

    }
}
