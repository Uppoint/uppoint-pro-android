package com.uppoint.android.pro;

import com.uppoint.android.pro.core.util.SharedPreferenceConstants;
import com.uppoint.android.pro.db.Provider;
import com.uppoint.android.pro.db.Scheme;
import com.uppoint.android.pro.db.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 */
public class UppointProApplication extends Application {

    private static final String ACCOUNT = "dummyaccount";
    private static final String ACCOUNT_TYPE = "uppoint.rocks";

    @Override
    public void onCreate() {
        super.onCreate();

        addSyncAccount();
        registerSyncObserver();
    }

    private void addSyncAccount() {
        final Account syncAccount = getSyncAccount();
        final AccountManager accountService = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        accountService.addAccountExplicitly(syncAccount, null, null);
    }

    private void registerSyncObserver() {
        getContentResolver().registerContentObserver(Scheme.SYNC_URI, true, new SyncContentObserver());
    }

    public void requestSync(boolean force) {
        final Bundle args = getSyncArgs();
        if (force) {
            addForceSyncArgs(args);
        }
        ContentResolver.requestSync(getSyncAccount(), Provider.AUTHORITY, args);
    }

    private void addForceSyncArgs(Bundle args) {
        args.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        args.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
    }

    private Bundle getSyncArgs() {
        final SharedPreferences prefs = getSharedPreferences(SharedPreferenceConstants.SETTINGS_PREFS, MODE_PRIVATE);
        final Bundle bundle = new Bundle();
        bundle.putString(SyncAdapter.ARG_USER_KEY, prefs.getString(SharedPreferenceConstants.KEY_USER_REMOTE_ID, null));
        bundle.putString(SyncAdapter.ARG_TIMESTAMP,
                prefs.getString(SharedPreferenceConstants.KEY_SYNC_TIMESTAMP, null));
        return bundle;
    }

    @NonNull
    private Account getSyncAccount() {
        return new Account(ACCOUNT, ACCOUNT_TYPE);
    }


    private class SyncContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         */
        public SyncContentObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            requestSync(false /* don't force sync */);
        }

    }
}
