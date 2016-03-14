package com.uppoint.android.pro.calendar.fragment;

import com.uppoint.android.pro.core.util.Preconditions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

/**
 */
public class TimePickerDialog extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {

    public static final String TAG = "time_picker";

    private static final String ARG_ID = "arg_id";
    private static final String ARG_HOUR = "arg_hour";
    private static final String ARG_MINUTE = "arg_minute";

    private Callback mCallback;

    public static TimePickerDialog newInstance(int id, int hour, int minute) {
        final Bundle args = new Bundle();
        args.putInt(ARG_ID, id);
        args.putInt(ARG_HOUR, hour);
        args.putInt(ARG_MINUTE, minute);

        final TimePickerDialog dialog = new TimePickerDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Preconditions.stateNonNull(getArguments(), "Required arguments missing");

        final Context context = getContext();
        final Bundle args = getArguments();
        final int hour = args.getInt(ARG_HOUR);
        final int minute = args.getInt(ARG_MINUTE);
        return new android.app.TimePickerDialog(context, this, hour, minute, DateFormat.is24HourFormat(context));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mCallback != null) {
            mCallback.onTimeSet(getArguments().getInt(ARG_ID), hourOfDay, minute);
        }
    }

    public interface Callback {

        void onTimeSet(int id, int hour, int minute);

    }
}
