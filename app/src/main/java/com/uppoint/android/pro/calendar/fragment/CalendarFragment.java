package com.uppoint.android.pro.calendar.fragment;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.adapter.CalendarAdapter;
import com.uppoint.android.pro.calendar.mapper.EventMapper;
import com.uppoint.android.pro.core.fragment.BaseFragment;
import com.uppoint.android.pro.core.util.Preconditions;
import com.uppoint.android.pro.db.Scheme;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import java.util.Calendar;

import bg.dalexiev.bender.content.EntityCursor;
import bg.dalexiev.bender.content.QueryCommand;

import static bg.dalexiev.bender.db.OrderBy.asc;
import static bg.dalexiev.bender.db.Predicate.between;
import static bg.dalexiev.bender.db.Predicate.eq;


/**
 */
public class CalendarFragment extends BaseFragment<Void>
        implements CalendarAdapter.OnDataNeededListener, View.OnClickListener, QueryCommand.Callback<WeekViewEvent> {

    private CalendarAdapter mCalendarAdapter;

    private Callback mCallback;

    private ForceLoadObserver mObserver;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Preconditions.instanceOf(context, Callback.class,
                "The hosting activity doensn't implement the required Callback interface.");
        mCallback = (Callback) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendarAdapter = new CalendarAdapter();

        mObserver = new ForceLoadObserver();
        getContext().getContentResolver().registerContentObserver(Scheme.Event.URI, true, mObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getContext().getContentResolver().unregisterContentObserver(mObserver);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_calendar;
    }

    @Override
    protected void initUI(View view) {
        final WeekView weekView = (WeekView) view.findViewById(R.id.week_view);
        mCalendarAdapter.setWeekView(weekView);
        mCalendarAdapter.setOnDataNeededListener(this);

        final FloatingActionButton addButton = (FloatingActionButton) view.findViewById(R.id.add_event_button);
        addButton.setOnClickListener(this);
    }

    @Override
    protected void updateUI(Void model) {
        // do nothing
    }

    @NonNull
    private Calendar getEndDate(int year, int month) {
        final Calendar end = getStartDate(year, month);
        end.add(Calendar.MONTH, 1);
        return end;
    }

    @NonNull
    private Calendar getStartDate(int year, int month) {
        final Calendar start = Calendar.getInstance();
        start.set(year, month, start.getActualMinimum(month), 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);
        return start;
    }

    @Override
    public void onDataNeeded(int year, int month) {
        final Calendar start = getStartDate(year, month);
        final Calendar end = getEndDate(year, month);
        getContentResolverCommandBuilder()
                .query(getContext().getContentResolver(), WeekViewEvent.class)
                .onUri(Scheme.Event.URI)
                .select(Scheme.Event.PROJECTION)
                .where(between(Scheme.Event.START_TIME, String.valueOf(start.getTimeInMillis()),
                        String.valueOf(end.getTimeInMillis())))
                .where(eq(Scheme.Event._IS_DELETED, "0"))
                .orderBy(asc(Scheme.Event.START_TIME))
                .useRowMapper(new EventMapper())
                .executeAsync(CalendarAdapter.calculateKey(month, year), this);
    }

    @Override
    public void onClick(View v) {
        if (R.id.add_event_button == v.getId()) {
            mCallback.onAddEvent();
        }
    }

    @Override
    public void onQueryComplete(int token, EntityCursor<WeekViewEvent> cursor) {
        mCalendarAdapter.addData(token, cursor);
    }

    public interface Callback {

        void onAddEvent();

    }

    private class ForceLoadObserver extends ContentObserver {

        /**
         * Creates a content observer.
         */
        public ForceLoadObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mCalendarAdapter.invalidate();
        }
    }
}
