package com.uppoint.android.pro.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import bg.dalexiev.bender.content.DatabaseContentProvider;

/**
 */
public class Provider extends DatabaseContentProvider {

    public static final String AUTHORITY = "com.uppoint.pro.provider";

    @NonNull
    @Override
    protected String createAuthority() {
        return AUTHORITY;
    }

    @NonNull
    @Override
    protected SQLiteOpenHelper createHelper(Context context) {
        return new Helper(context);
    }
}
