package com.uppoint.android.pro.db;

import com.appspot.uppoint_api.uppointApi.model.EventPayload;
import com.appspot.uppoint_api.uppointApi.model.ProUserPayload;
import com.appspot.uppoint_api.uppointApi.model.SyncPayload;
import com.appspot.uppoint_api.uppointApi.model.UserDefinedServicePayload;
import com.uppoint.android.pro.core.ApiFactory;
import com.uppoint.android.pro.core.util.Preconditions;
import com.uppoint.android.pro.db.mapper.SyncEventMapper;
import com.uppoint.android.pro.db.mapper.SyncKeyMapper;
import com.uppoint.android.pro.db.mapper.SyncServiceMapper;
import com.uppoint.android.pro.db.mapper.SyncUserMapper;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import bg.dalexiev.bender.content.EntityCursor;
import bg.dalexiev.bender.content.ResolverCommandBuilder;

import static bg.dalexiev.bender.db.Predicate.eq;
import static bg.dalexiev.bender.db.Predicate.ge;

/**
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String ARG_USER_KEY = "arg_user_key";
    public static final String ARG_TIMESTAMP = "arg_timestamp";

    private final ContentResolver mContentResolver;

    private final ResolverCommandBuilder mBuilder;

    public SyncAdapter(Context context, boolean autoInitialize) {
        this(context, autoInitialize, false /* don't allow parallel sync */);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
        mBuilder = new ResolverCommandBuilder();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        final String timestamp = extras.getString(ARG_TIMESTAMP);
        final String userKey = extras.getString(ARG_USER_KEY);
        Preconditions.nonNull(userKey, "Missing required parameter arg_user_key");

        SyncPayload localChanges = null;
        if (timestamp != null) {
            localChanges = fetchLocalChanges(syncResult, timestamp, userKey);
        }

        try {
            final SyncPayload remoteChanges = ApiFactory.getApi(getContext()).users().sync(userKey, localChanges)
                    .setTimestamp(timestamp).execute();
            applyRemoteChanges(remoteChanges, syncResult);
        } catch (IOException e) {
            syncResult.hasHardError();
        }
    }

    private void applyRemoteChanges(SyncPayload remoteChanges, SyncResult syncResult) {
    }

    private SyncPayload fetchLocalChanges(SyncResult syncResult, String timestamp, String userKey) {
        final SyncPayload syncPayload = new SyncPayload();

        final ProUserPayload proUserPayload = fetchProfile(userKey, timestamp);
        syncPayload.setProfile(proUserPayload);

        final List<UserDefinedServicePayload> updatedServices = fetchUpdatedServices(userKey, timestamp);
        syncPayload.setUpdatedServices(updatedServices);

        final List<EventPayload> updatedEvents = fetchUpdatedEvents(userKey, timestamp);
        syncPayload.setUpdatedEvents(updatedEvents);

        final List<String> deletedServiceKeys = fetchDeletedServiceKeys(userKey, timestamp);
        syncPayload.setDeletedServices(deletedServiceKeys);

        final List<String> deletedEventKeys = fetchDeletedEventKeys(userKey, timestamp);
        syncPayload.setDeletedEvents(deletedEventKeys);

        return syncPayload;
    }

    private List<String> fetchDeletedEventKeys(String userKey, String timestamp) {
        final EntityCursor<String> keysCursor = mBuilder
                .query(mContentResolver, String.class)
                .onUri(Scheme.Event.URI)
                .select(Scheme.Event._KEY)
                .where(eq(Scheme.UserDefinedService.USER_KEY, userKey))
                .where(ge(Scheme.UserDefinedService._LAST_UPDATE, timestamp))
                .where(eq(Scheme.UserDefinedService._IS_DELETED, "1"))
                .useRowMapper(new SyncKeyMapper())
                .execute();

        return cursorAsList(keysCursor);
    }

    private List<String> fetchDeletedServiceKeys(String userKey, String timestamp) {
        final EntityCursor<String> keysCursor = mBuilder
                .query(mContentResolver, String.class)
                .onUri(Scheme.UserDefinedService.URI)
                .select(Scheme.UserDefinedService._KEY)
                .where(eq(Scheme.UserDefinedService.USER_KEY, userKey))
                .where(ge(Scheme.UserDefinedService._LAST_UPDATE, timestamp))
                .where(eq(Scheme.UserDefinedService._IS_DELETED, "1"))
                .useRowMapper(new SyncKeyMapper())
                .execute();

        return cursorAsList(keysCursor);
    }

    private List<EventPayload> fetchUpdatedEvents(String userKey, String timestamp) {
        final EntityCursor<EventPayload> eventsCursor = mBuilder
                .query(mContentResolver, EventPayload.class)
                .onUri(Scheme.Event.URI)
                .select(Scheme.Event.SYNC_PROJECTION)
                .where(eq(Scheme.Event.USER_KEY, userKey))
                .where(ge(Scheme.Event._LAST_UPDATE, timestamp))
                .where(eq(Scheme.Event._IS_DELETED, "0"))
                .useRowMapper(new SyncEventMapper())
                .execute();

        return cursorAsList(eventsCursor);
    }

    private List<UserDefinedServicePayload> fetchUpdatedServices(String userKey, String timestamp) {
        final EntityCursor<UserDefinedServicePayload> servicesCursor = mBuilder
                .query(mContentResolver, UserDefinedServicePayload.class).onUri(Scheme.UserDefinedService.URI)
                .select(Scheme.UserDefinedService.SYNC_PROJECTION)
                .where(eq(Scheme.UserDefinedService.USER_KEY, userKey))
                .where(ge(Scheme.UserDefinedService._LAST_UPDATE, timestamp))
                .where(eq(Scheme.UserDefinedService._IS_DELETED, "0"))
                .useRowMapper(new SyncServiceMapper())
                .execute();

        return cursorAsList(servicesCursor);
    }

    @Nullable
    private ProUserPayload fetchProfile(String userKey, String timestamp) {
        final EntityCursor<ProUserPayload> cursor = mBuilder
                .query(mContentResolver, ProUserPayload.class)
                .onUri(Scheme.User.URI)
                .select(Scheme.User.SYNC_PROJECTION)
                .where(eq(Scheme.User._KEY, userKey))
                .where(ge(Scheme.User._LAST_UPDATE, timestamp))
                .useRowMapper(new SyncUserMapper())
                .execute();
        ProUserPayload proUserPayload = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                proUserPayload = cursor.getEntity();
            }
            cursor.close();
        }
        return proUserPayload;
    }

    private static <E> List<E> cursorAsList(EntityCursor<E> cursor) {
        if (cursor == null) {
            return Collections.emptyList();
        }
        final List<E> list = new LinkedList<>();
        while (cursor.moveToNext()) {
            list.add(cursor.getEntity());
        }

        return list;
    }
}
