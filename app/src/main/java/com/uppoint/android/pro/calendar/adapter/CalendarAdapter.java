package com.uppoint.android.pro.calendar.adapter;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.uppoint.android.pro.core.util.Preconditions;

import android.util.SparseArray;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import bg.dalexiev.bender.content.EntityCursor;

/**
 */
public class CalendarAdapter implements MonthLoader.MonthChangeListener {

    private WeekView mWeekView;
    private SparseArray<List<WeekViewEvent>> mData;

    private OnDataNeededListener mOnDataNeededListener;

    public CalendarAdapter() {
        mData = new SparseArray<>();
    }

    public void setOnDataNeededListener(OnDataNeededListener onDataNeededListener) {
        mOnDataNeededListener = onDataNeededListener;
    }

    public void setWeekView(WeekView weekView) {
        Preconditions.nonNull(weekView, "Required parameter weekView is null");

        mWeekView = weekView;
        mWeekView.setMonthChangeListener(this);
        if (mData.size() > 0) {
            mWeekView.notifyDatasetChanged();
        }
    }

    public void addData(EntityCursor<WeekViewEvent> data) {
        if (data != null) {
            while (data.moveToNext()) {
                final WeekViewEvent event = data.getEntity();
                final int key = calculateEventKey(event);
                List<WeekViewEvent> monthlyEvents = mData.get(key);
                monthlyEvents.add(event);
                mData.put(key, monthlyEvents);
            }
        }

        mWeekView.notifyDatasetChanged();
    }

    private static int calculateEventKey(WeekViewEvent event) {
        final Calendar startTime = event.getStartTime();
        final int month = startTime.get(Calendar.MONTH) + 1;
        final int year = startTime.get(Calendar.YEAR);
        return calculateKey(month, year);
    }

    private static int calculateKey(int month, int year) {
        return year * 12 + month;
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        final int key = calculateKey(newYear, newMonth);
        final List<WeekViewEvent> weekViewEvents = mData.get(key);
        if (weekViewEvents == null) {
            if (mOnDataNeededListener != null) {
                mOnDataNeededListener.onDataNeeded(newYear, newMonth);
            }

            final List<WeekViewEvent> monthlyEvents = new LinkedList<>();
            mData.put(key, monthlyEvents);
            return monthlyEvents;
        }

        return weekViewEvents;
    }

    public interface OnDataNeededListener {

        void onDataNeeded(int year, int month);

    }
}
