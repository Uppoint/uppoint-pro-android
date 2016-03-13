package com.uppoint.android.pro.calendar.fragment;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.adapter.CalendarAdapter;
import com.uppoint.android.pro.calendar.mapper.EventMapper;
import com.uppoint.android.pro.core.fragment.BaseFragment;
import com.uppoint.android.pro.db.Scheme;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.View;

import java.util.Calendar;

import bg.dalexiev.bender.content.EntityCursor;
import bg.dalexiev.bender.content.QueryCommand;
import bg.dalexiev.bender.content.SupportEntityCursorLoader;

import static bg.dalexiev.bender.db.OrderBy.asc;
import static bg.dalexiev.bender.db.Predicate.between;

/**
 */
public class CalendarFragment extends BaseFragment<EntityCursor<WeekViewEvent>>
        implements CalendarAdapter.OnDataNeededListener {

    private static final int EVENT_LOADER = 10;

    private static String ARG_YEAR = "arg_year";
    private static String ARG_MONTH = "arg_month";

    private CalendarAdapter mCalendarAdapter;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCalendarAdapter = new CalendarAdapter();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_calendar;
    }

    @Override
    protected int getLoaderId() {
        return EVENT_LOADER;
    }

    @Nullable
    @Override
    protected Bundle getLoaderArguments() {
        final Calendar today = Calendar.getInstance();
        final Bundle args = new Bundle();
        args.putInt(ARG_YEAR, today.get(Calendar.YEAR));
        args.putInt(ARG_MONTH, today.get(Calendar.MONTH));
        return args;
    }

    @Override
    protected void initUI(View view) {
        final WeekView weekView = (WeekView) view.findViewById(R.id.week_view);
        mCalendarAdapter.setWeekView(weekView);
        mCalendarAdapter.setOnDataNeededListener(this);
    }

    @Override
    protected void updateUI(EntityCursor<WeekViewEvent> model) {
        mCalendarAdapter.addData(model);
    }

    @Override
    public Loader<EntityCursor<WeekViewEvent>> onCreateLoader(int id, Bundle args) {
        final int year = args.getInt(ARG_YEAR);
        final int month = args.getInt(ARG_MONTH);
        final Calendar start = getStartDate(year, month);
        final Calendar end = getEndDate(year, month);
        final QueryCommand<WeekViewEvent> query = getContentResolverCommandBuilder()
                .query(getContext().getContentResolver(), WeekViewEvent.class)
                .onUri(Scheme.Event.URI)
                .select(Scheme.Event.PROJECTION)
                .where(between(Scheme.Event.START_TIME, String.valueOf(start.getTimeInMillis()),
                        String.valueOf(end.getTimeInMillis())))
                .orderBy(asc(Scheme.Event.START_TIME))
                .useRowMapper(new EventMapper());
        return new SupportEntityCursorLoader<>(getContext(), query, id);
    }

    @NonNull
    private Calendar getEndDate(int year, int month) {
        final Calendar end = getStartDate(year, month);
        end.add(Calendar.MONTH, 3);
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
        final Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        getLoaderManager().restartLoader(EVENT_LOADER, args, this);
    }
}
