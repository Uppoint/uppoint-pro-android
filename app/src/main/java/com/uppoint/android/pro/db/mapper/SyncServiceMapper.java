package com.uppoint.android.pro.db.mapper;

import com.appspot.uppoint_api.uppointApi.model.UserDefinedServicePayload;
import com.uppoint.android.pro.db.Scheme;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import bg.dalexiev.bender.mapper.RowMapper;

/**
 */
public class SyncServiceMapper implements RowMapper<UserDefinedServicePayload> {

    @Nullable
    @Override
    public UserDefinedServicePayload toObject(@NonNull Cursor cursor, int rowNum) {
        final UserDefinedServicePayload service = new UserDefinedServicePayload();

        final String key = cursor.getString(cursor.getColumnIndex(Scheme.UserDefinedService._KEY));
        service.setKey(key);

        final String name = cursor.getString(cursor.getColumnIndex(Scheme.UserDefinedService.NAME));
        service.setName(name);

        final String description = cursor.getString(cursor.getColumnIndex(Scheme.UserDefinedService.DESCRIPTION));
        service.setDescription(description);

        final double price = cursor.getDouble(cursor.getColumnIndex(Scheme.UserDefinedService.PRICE));
        service.setPrice(price);

        final int duration = cursor.getInt(cursor.getColumnIndex(Scheme.UserDefinedService.DURATION));
        service.setDuration(duration);

        final String userKey = cursor.getString(cursor.getColumnIndex(Scheme.UserDefinedService.USER_KEY));
        service.setUserKey(userKey);

        final String serviceTypeKey = cursor
                .getString(cursor.getColumnIndex(Scheme.UserDefinedService.SERVICE_TYPE_KEY));
        service.setServiceTypeKey(serviceTypeKey);

        return service;
    }
}
