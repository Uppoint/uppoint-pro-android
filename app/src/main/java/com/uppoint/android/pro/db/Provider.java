package com.uppoint.android.pro.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

import bg.dalexiev.bender.content.DatabaseContentProvider;

/**
 */
public class Provider extends DatabaseContentProvider {

    public static final String PARAM_IS_SYNC = "isSync";

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

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final Uri insertUri = super.insert(uri, values);
        triggerSync();
        return insertUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, ContentValues[] values) {
        final int insertedRows = super.bulkInsert(uri, values);
        triggerSync();
        return insertedRows;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int updatedRows = super.update(uri, values, selection, selectionArgs);
        triggerSync();
        return updatedRows;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final int deletedRows = super.delete(uri, selection, selectionArgs);
        triggerSync();
        return deletedRows;
    }

    private void triggerSync() {
        final Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(Scheme.SYNC_URI, null);
        }
    }

    @Override
    protected void notifyChange(@NonNull Uri uri) {
        if (uri.getBooleanQueryParameter(PARAM_IS_SYNC, false)) {
            // if the change is made during a sync, don't notify. The sync adapter takes care of that.
            super.notifyChange(uri);
        }
    }
}
