package com.uppoint.android.pro.login.activity;

import com.uppoint.android.pro.core.activity.BaseActivity;
import com.uppoint.android.pro.login.fragment.GetStartedFragment;

import android.content.Intent;

/**
 */
public class GetStartedActivity extends BaseActivity implements GetStartedFragment.Callback {


    @Override
    protected void onFragmentContainerReady() {
        addFragment(GetStartedFragment.newInstance(), false /* don't add to back stack */);
    }

    @Override
    protected boolean hasToolbar() {
        return false;
    }

    @Override
    protected int getActivityTitle() {
        return NO_TITLE;
    }

    @Override
    public void onPickAccountSelected() {
        final Intent intent = new Intent(this, PickAccountActivity.class);
        startActivity(intent);
    }
}
