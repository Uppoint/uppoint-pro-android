package com.uppoint.android.pro.calendar.mapper;

import com.alamkanak.weekview.WeekViewEvent;
import com.uppoint.android.pro.db.Scheme;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Calendar;

import bg.dalexiev.bender.mapper.RowMapper;

/**
 */
public class EventMapper implements RowMapper<WeekViewEvent> {

    @Nullable
    @Override
    public WeekViewEvent toObject(@NonNull Cursor cursor, int rowNum) {
        final WeekViewEvent event = new WeekViewEvent();

        event.setId(cursor.getLong(cursor.getColumnIndex(Scheme.Event._ID)));
        event.setName(cursor.getString(cursor.getColumnIndex(Scheme.Event.TITLE)));
        event.setLocation(cursor.getString(cursor.getColumnIndex(Scheme.Event.DESCRIPTION)));

        final Calendar startTime = Calendar.getInstance();
        startTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Scheme.Event.START_TIME)));
        event.setStartTime(startTime);

        final Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(Scheme.Event.END_TIME)));
        event.setEndTime(endTime);

        return event;
    }
}
