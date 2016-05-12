package com.uppoint.android.pro.calendar.fragment;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.activity.BaseCalendarActivity;
import com.uppoint.android.pro.core.fragment.BaseFragment;
import com.uppoint.android.pro.core.util.SharedPreferenceConstants;
import com.uppoint.android.pro.db.Scheme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import bg.dalexiev.bender.content.InsertCommand;

/**
 */
public class NewEventFragment extends BaseFragment<Void> implements View.OnClickListener, TimePickerDialog.Callback,
        DatePickerDialog.Callback, InsertCommand.Callback {

    private static final int SAVE_EVENT_TOKEN = 1;

    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;

    private TextInputLayout mTitleContainer;
    private EditText mTitleEditText;
    private EditText mDescriptionEditText;
    private EditText mFromDate;
    private EditText mFromTime;
    private TextInputLayout mToDateContainer;
    private EditText mToDate;
    private TextInputLayout mToTimeContainer;
    private EditText mToTime;

    private MenuItem mSaveAction;

    private Drawable mNavigationIcon;
    private String mOriginalTitle;

    public static NewEventFragment newInstance() {
        return new NewEventFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(getContext());
        mTimeFormat = android.text.format.DateFormat.getTimeFormat(getContext());

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_event, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        mSaveAction = menu.findItem(R.id.action_new_event_save);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_new_event_save == item.getItemId()) {
            onSaveClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveClick() {
        try {
            final long startTime = getStartTime();
            final long endTime = getEndTime();

            if (validateForm(startTime, endTime)) {
                saveEvent(startTime, endTime);
            }
        } catch (ParseException e) {
            // should never happen
        }
    }

    private boolean validateForm(long startTime, long endTime) {
        return false;
    }

    private void saveEvent(long startTime, long endTime) {
        getContentResolverCommandBuilder()
                .insert(getContext().getContentResolver())
                .onUri(Scheme.Event.URI)
                .set(Scheme.Event.TITLE, mTitleEditText.getText().toString().trim())
                .set(Scheme.Event.DESCRIPTION, mDescriptionEditText.getText().toString().trim())
                .set(Scheme.Event.START_TIME, startTime)
                .set(Scheme.Event.END_TIME, endTime)
                .set(Scheme.Event.USER_KEY, getUserKey())
                .executeAsync(SAVE_EVENT_TOKEN, this);

    }

    private String getUserKey() {
        final SharedPreferences sharedPreferences = getContext()
                .getSharedPreferences(SharedPreferenceConstants.SETTINGS_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SharedPreferenceConstants.KEY_USER_REMOTE_ID, null);
    }

    private long getStartTime() throws ParseException {
        return getEventTime(mFromDate, mFromTime);
    }

    private long getEndTime() throws ParseException {
        return getEventTime(mToDate, mToTime);
    }

    private long getEventTime(EditText dateText, EditText timeText) throws ParseException {
        final Calendar date = parseDate(dateText);
        final Calendar time = parseTime(timeText);
        final Calendar dateTime = mergeDateAndTime(date, time);
        return dateTime.getTimeInMillis();
    }

    @NonNull
    private Calendar mergeDateAndTime(Calendar date, Calendar time) {
        final Calendar dateTime = Calendar.getInstance();
        dateTime.set(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
        return dateTime;
    }

    @Override
    public void onResume() {
        super.onResume();

        final BaseCalendarActivity activity = (BaseCalendarActivity) getActivity();
        if (activity == null) {
            return;
        }

        final Toolbar toolbar = activity.getToolbar();
        mNavigationIcon = toolbar.getNavigationIcon();
        mOriginalTitle = toolbar.getTitle().toString();
        toolbar.setTitle(R.string.calendar_new_event_title);

        final ActionBarDrawerToggle toggle = activity.getDrawerToggle();
        toggle.setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        final BaseCalendarActivity activity = (BaseCalendarActivity) getActivity();
        if (activity == null) {
            return;
        }

        final Toolbar toolbar = activity.getToolbar();
        toolbar.setTitle(mOriginalTitle);

        final ActionBarDrawerToggle toggle = activity.getDrawerToggle();
        toggle.setHomeAsUpIndicator(mNavigationIcon);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setToolbarNavigationClickListener(null);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_new_event;
    }

    @Override
    protected void initUI(View view) {
        mTitleContainer = (TextInputLayout) view.findViewById(R.id.new_event_title_container);
        mTitleEditText = (EditText) view.findViewById(R.id.new_event_title);

        mDescriptionEditText = (EditText) view.findViewById(R.id.new_event_description);

        final Calendar now = Calendar.getInstance();
        final String dateText = mDateFormat.format(now.getTime());

        mFromDate = (EditText) view.findViewById(R.id.new_event_from_date);
        mFromDate.setText(dateText);
        mFromDate.setOnClickListener(this);

        mFromTime = (EditText) view.findViewById(R.id.new_event_from_time);
        mFromTime.setText(mTimeFormat.format(now.getTime()));
        mFromTime.setOnClickListener(this);

        mToDateContainer = (TextInputLayout) view.findViewById(R.id.new_event_to_date_container);
        mToDate = (EditText) view.findViewById(R.id.new_event_to_date);
        mToDate.setText(dateText);
        mToDate.setOnClickListener(this);

        mToTimeContainer = (TextInputLayout) view.findViewById(R.id.new_event_to_time_container);
        mToTime = (EditText) view.findViewById(R.id.new_event_to_time);
        now.add(Calendar.MINUTE, 30);
        mToTime.setText(mTimeFormat.format(now.getTime()));
        mToTime.setOnClickListener(this);
    }

    @Override
    protected void updateUI(Void model) {
        // do nothing
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_event_from_date:
            case R.id.new_event_to_date:
                try {
                    openDatePicker((EditText) v);
                } catch (ParseException e) {
                    // should never happen
                }
                break;

            case R.id.new_event_from_time:
            case R.id.new_event_to_time:
                try {
                    openTimePicker((EditText) v);
                } catch (ParseException e) {
                    // should never happen
                }
                break;

            default:
                // handle the drawer toggle navigation event
                close();
                break;
        }
    }

    private void close() {
        getActivity().onBackPressed();
    }

    @Override
    public void onTimeSet(int id, int hour, int minute) {
        final Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, hour);
        time.set(Calendar.MINUTE, minute);
        final EditText target = R.id.new_event_from_time == id ? mFromTime : mToTime;
        target.setText(mTimeFormat.format(time.getTime()));
    }

    @Override
    public void onDateSet(int id, int year, int month, int day) {
        final Calendar date = Calendar.getInstance();
        date.set(Calendar.YEAR, year);
        date.set(Calendar.MONTH, month);
        date.set(Calendar.DAY_OF_MONTH, day);
        final EditText target = R.id.new_event_from_date == id ? mFromDate : mToDate;
        target.setText(mDateFormat.format(date.getTime()));
    }

    private void openTimePicker(EditText triggerView) throws ParseException {
        final Calendar time = parseTime(triggerView);
        final int hour = time.get(Calendar.HOUR_OF_DAY);
        final int minute = time.get(Calendar.MINUTE);
        final TimePickerDialog dialog = TimePickerDialog.newInstance(triggerView.getId(), hour, minute);
        dialog.setCallback(this);
        dialog.show(getFragmentManager(), TimePickerDialog.TAG);
    }

    private void openDatePicker(EditText triggerView) throws ParseException {
        final Calendar date = parseDate(triggerView);
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH);
        final int day = date.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog dialog = DatePickerDialog.newInstance(triggerView.getId(), year, month, day);
        dialog.setCallback(this);
        dialog.show(getFragmentManager(), DatePickerDialog.TAG);
    }

    @NonNull
    private Calendar parseTime(EditText triggerView) throws ParseException {
        return parseDateTime(triggerView, mTimeFormat);
    }

    @NonNull
    private Calendar parseDate(EditText triggerView) throws ParseException {
        return parseDateTime(triggerView, mDateFormat);
    }

    @NonNull
    private Calendar parseDateTime(EditText triggerView, DateFormat format) throws ParseException {
        final String dateString = triggerView.getText().toString();
        final Date date = format.parse(dateString);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    @Override
    public void onInsertComplete(int token, Uri uri) {
        close();
    }
}
