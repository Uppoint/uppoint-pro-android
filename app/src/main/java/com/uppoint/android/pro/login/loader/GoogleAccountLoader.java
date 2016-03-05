package com.uppoint.android.pro.login.loader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Arrays;
import java.util.List;

/**
 */
public class GoogleAccountLoader extends AsyncTaskLoader<List<Account>> {

    private static final String TYPE_GOOGLE = "com.google";

    private AccountManager mAccountManager;

    private List<Account> mData;

    public GoogleAccountLoader(Context context) {
        super(context);

        mAccountManager = AccountManager.get(context.getApplicationContext());
    }

    @Override
    public List<Account> loadInBackground() {
        final Account[] accounts = mAccountManager.getAccountsByType(TYPE_GOOGLE);
        return Arrays.asList(accounts);
    }

    @Override
    public void deliverResult(List<Account> data) {
        if (isReset()) {
            return;
        }

        List<Account> oldData = mData;
        mData = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != data) {
            oldData = null;
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (mData == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            mData = null;
        }
    }
}
