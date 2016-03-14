package com.uppoint.android.pro.calendar.activity;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.fragment.CalendarFragment;
import com.uppoint.android.pro.calendar.fragment.NewEventFragment;
import com.uppoint.android.pro.core.activity.DrawerActivity;

/**
 */
public class CalendarActivity extends DrawerActivity implements CalendarFragment.Callback {

    @Override
    protected void onFragmentContainerReady() {
        addFragment(CalendarFragment.newInstance(), false /* don't add to back stack */);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.calendar_title;
    }

    @Override
    public void onAddEvent() {
        addFragment(NewEventFragment.newInstance(), true /* add to back stack */);
    }
}
