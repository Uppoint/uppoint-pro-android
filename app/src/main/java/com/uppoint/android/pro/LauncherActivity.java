package com.uppoint.android.pro;

import com.uppoint.android.pro.core.util.SharedPreferenceConstants;
import com.uppoint.android.pro.login.activity.GetStartedActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 */
public class LauncherActivity extends AppCompatActivity {

    private static final String ACCOUNT = "dummyaccount";
    private static final String ACCOUNT_TYPE = "uppoint.rocks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSyncAccount();

        openStartingActivity();

        finish();
    }

    private void addSyncAccount() {
        final Account syncAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        final AccountManager accountService = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        accountService.addAccountExplicitly(syncAccount, null, null);
    }

    private void openStartingActivity() {
        final Intent intent;
        if (getLoggedUser() == null) {
            // no user, go through login
            intent = new Intent(this, GetStartedActivity.class);
            startActivity(intent);
        } else {
            // user logger, go to main screen
            // TODO: Implement navigation to the main screen
        }
    }

    private String getLoggedUser() {
        final SharedPreferences settings = getSharedPreferences(SharedPreferenceConstants.SETTINGS_PREFS, MODE_PRIVATE);
        return settings.getString(SharedPreferenceConstants.KEY_USER, null);
    }
}
