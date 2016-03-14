package com.uppoint.android.pro.login.activity;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.activity.CalendarActivity;
import com.uppoint.android.pro.core.activity.BaseActivity;
import com.uppoint.android.pro.login.fragment.PickAccountFragment;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

/**
 */
public class PickAccountActivity extends BaseActivity implements PickAccountFragment.Callback {

    @Override
    protected void onFragmentContainerReady() {
        addFragment(PickAccountFragment.newInstance(), false /* don't add to back stack */);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.pick_account_title;
    }

    @Override
    public void onAddGoogleAccount() {
        final Intent addGoogleAccountIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        addGoogleAccountIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            addGoogleAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
        }
        startActivity(addGoogleAccountIntent);
    }

    @Override
    public void onAccountReady() {
        final Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        finish();
    }
}
