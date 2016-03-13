package com.uppoint.android.pro.core.activity;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.core.EndpointCommand;
import com.uppoint.android.pro.core.fragment.EndpointCacheFragment;
import com.uppoint.android.pro.core.util.Preconditions;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected static final int NO_TITLE = -1;

    private EndpointCacheFragment mCacheFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResId());

        initToolbar();

        initCache();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        onFragmentContainerReady();
    }

    protected int getLayoutResId() {
        return R.layout.activity_base;
    }

    private void initCache() {
        final FragmentManager fm = getSupportFragmentManager();
        mCacheFragment = (EndpointCacheFragment) fm.findFragmentByTag(EndpointCacheFragment.class.getSimpleName());
        if (mCacheFragment == null) {
            mCacheFragment = EndpointCacheFragment.newInstance();
            fm.beginTransaction().add(mCacheFragment, EndpointCacheFragment.TAG).commit();
        }
    }

    private void initToolbar() {
        setUpToolbar();
        initToolbarTitle();
    }

    @SuppressWarnings("ConstantConditions")
    private void setUpToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!hasToolbar()) {
            toolbar.setVisibility(View.GONE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbarTitle() {
        final int titleResId = getActivityTitle();
        if (NO_TITLE != titleResId) {
            getSupportActionBar().setTitle(titleResId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            final Intent upIntent = NavUtils.getParentActivityIntent(this);
            if (upIntent == null) {
                finish();
            } else if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                TaskStackBuilder.create(this)
                        .addNextIntentWithParentStack(upIntent)
                        .startActivities();
            } else {
                NavUtils.navigateUpTo(this, upIntent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected abstract void onFragmentContainerReady();

    @StringRes
    protected abstract int getActivityTitle();

    @IdRes
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    protected boolean hasToolbar() {
        return true;
    }

    public void putEndpoint(@NonNull String key, @NonNull EndpointCommand<?, ?> endpointCommand) {
        mCacheFragment.put(key, endpointCommand);
    }

    @Nullable
    public EndpointCommand<?, ?> getEndpoint(@NonNull String key) {
        return mCacheFragment.get(key);
    }

    protected void addFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        Preconditions.nonNull(fragment, "Fragment can't be null");

        final FragmentTransaction ftx = getSupportFragmentManager().beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        ftx.add(R.id.fragment_container, fragment, tag);
        if (addToBackStack) {
            ftx.addToBackStack(tag);
        }
        ftx.commit();
    }

    protected void replaceFragment(@NonNull Fragment fragment, boolean addToBackStack) {
        Preconditions.nonNull(fragment, "Fragment can't be null");

        final FragmentTransaction ftx = getSupportFragmentManager().beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        ftx.replace(R.id.fragment_container, fragment, tag);
        if (addToBackStack) {
            ftx.addToBackStack(tag);
        }
        ftx.commit();
    }
}
