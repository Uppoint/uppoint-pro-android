package com.uppoint.android.pro.db;

import com.appspot.uppoint_api.uppointApi.model.ProUserPayload;
import com.appspot.uppoint_api.uppointApi.model.SyncPayload;
import com.uppoint.android.pro.core.ApiFactory;
import com.uppoint.android.pro.core.util.Preconditions;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import java.io.IOException;

import bg.dalexiev.bender.content.ResolverCommandBuilder;

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
        SyncPayload localChanges = null;
        if (timestamp != null) {
            localChanges = fetchLocalChanges(syncResult);
        }

        try {
            final String userKey = extras.getString(ARG_USER_KEY);
            Preconditions.nonNull(userKey, "Missing required parameter arg_user_key");
            final SyncPayload remoteChanges = ApiFactory.getApi(getContext()).users().sync(userKey, localChanges)
                    .setTimestamp(timestamp).execute();
            applyRemoteChanges(remoteChanges, syncResult);
        } catch (IOException e) {
            syncResult.hasHardError();
        }
    }

    private void applyRemoteChanges(SyncPayload remoteChanges, SyncResult syncResult) {
    }

    private SyncPayload fetchLocalChanges(SyncResult syncResult) {

        return null;
    }

}
