package com.uppoint.android.pro.calendar.adapter;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.uppoint.android.pro.core.util.Preconditions;

import android.util.SparseArray;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import bg.dalexiev.bender.content.EntityCursor;

/**
 */
public class CalendarAdapter implements MonthLoader.MonthChangeListener {

    private WeekView mWeekView;
    private CalendarData mData;

    private OnDataNeededListener mOnDataNeededListener;

    public static int calculateKey(int month, int year) {
        return year * 12 + month;
    }

    public CalendarAdapter() {
        mData = new CalendarData();
    }

    public void setOnDataNeededListener(OnDataNeededListener onDataNeededListener) {
        mOnDataNeededListener = onDataNeededListener;
    }

    public void setWeekView(WeekView weekView) {
        Preconditions.nonNull(weekView, "Required parameter weekView is null");

        mWeekView = weekView;
        mWeekView.setMonthChangeListener(this);
        if (!mData.isEmpty()) {
            mWeekView.notifyDatasetChanged();
        }
    }

    public void addData(int key, EntityCursor<WeekViewEvent> data) {
        mData.addEvents(key, data);
        mWeekView.notifyDatasetChanged();
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        final int key = calculateKey(newMonth, newYear);
        final int status = mData.getStatus(key);

        if (CalendarData.NOT_REQUESTED == status && mOnDataNeededListener != null) {
            mData.setStatus(key, CalendarData.LOADING);
            mOnDataNeededListener.onDataNeeded(newYear, newMonth);
            return Collections.<WeekViewEvent>emptyList();
        }

        if (CalendarData.LOADING == status) {
            return Collections.<WeekViewEvent>emptyList();
        }

        return mData.getEvents(key);
    }

    public void invalidate() {
        mData.clear();
        mWeekView.notifyDatasetChanged();
    }

    public interface OnDataNeededListener {

        void onDataNeeded(int year, int month);

    }

    private class CalendarData {

        private static final int NOT_REQUESTED = -1;
        private static final int LOADING = 0;
        private static final int LOADED = 1;

        private final SparseArray<List<WeekViewEvent>> mMonthlyEvents;
        private final SparseArray<Integer> mStatuses;

        private CalendarData() {
            mMonthlyEvents = new SparseArray<>();
            mStatuses = new SparseArray<>();
        }

        private boolean isEmpty() {
            return mMonthlyEvents.size() == 0;
        }

        private void addEvents(int key, EntityCursor<WeekViewEvent> eventCursor) {
            if (eventCursor == null) {
                mMonthlyEvents.put(key, Collections.<WeekViewEvent>emptyList());
            } else {
                List<WeekViewEvent> monthlyEvents = mMonthlyEvents.get(key);
                if (monthlyEvents == null) {
                    monthlyEvents = new LinkedList<>();
                } else {
                    monthlyEvents.clear();
                }
                while (eventCursor.moveToNext()) {
                    final WeekViewEvent event = eventCursor.getEntity();
                    monthlyEvents.add(event);
                }
                mMonthlyEvents.put(key, monthlyEvents);
            }

            mStatuses.put(key, LOADED);
        }

        private List<WeekViewEvent> getEvents(int key) {
            return mMonthlyEvents.get(key);
        }

        private int getStatus(int key) {
            return mStatuses.get(key, NOT_REQUESTED);
        }

        private void setStatus(int key, int status) {
            mStatuses.put(key, status);
        }

        private void clear() {
            mMonthlyEvents.clear();
            mStatuses.clear();
        }
    }
}
