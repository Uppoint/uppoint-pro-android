package com.uppoint.android.pro.db.mapper;

import com.appspot.uppoint_api.uppointApi.model.EventPayload;
import com.uppoint.android.pro.db.Scheme;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import bg.dalexiev.bender.mapper.RowMapper;

/**
 */
public class SyncEventMapper implements RowMapper<EventPayload> {

    @Nullable
    @Override
    public EventPayload toObject(@NonNull Cursor cursor, int rowNum) {
        final EventPayload event = new EventPayload();

        final String key = cursor.getString(cursor.getColumnIndex(Scheme.Event._KEY));
        event.setKey(key);

        final String title = cursor.getString(cursor.getColumnIndex(Scheme.Event.TITLE));
        event.setTitle(title);

        final String description = cursor.getString(cursor.getColumnIndex(Scheme.Event.DESCRIPTION));
        event.setDescription(description);

        final long startTime = cursor.getLong(cursor.getColumnIndex(Scheme.Event.START_TIME));
        event.setStartTime(startTime);

        final long endTime = cursor.getLong(cursor.getColumnIndex(Scheme.Event.END_TIME));
        event.setEndTime(endTime);

        final String userKey = cursor.getString(cursor.getColumnIndex(Scheme.Event.USER_KEY));
        event.setUserKey(userKey);

        return event;
    }
}
