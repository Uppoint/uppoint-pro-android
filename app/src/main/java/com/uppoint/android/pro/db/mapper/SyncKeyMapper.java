package com.uppoint.android.pro.db.mapper;

import com.uppoint.android.pro.db.Scheme;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import bg.dalexiev.bender.mapper.RowMapper;

/**
 */
public class SyncKeyMapper implements RowMapper<String> {

    @Nullable
    @Override
    public String toObject(@NonNull Cursor cursor, int rowNum) {
        return cursor.getString(cursor.getColumnIndex(Scheme.Entity._KEY));
    }
}
