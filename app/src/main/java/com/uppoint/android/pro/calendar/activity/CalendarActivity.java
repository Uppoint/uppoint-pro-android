package com.uppoint.android.pro.calendar.activity;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.fragment.CalendarFragment;
import com.uppoint.android.pro.core.activity.DrawerActivity;

/**
 */
public class CalendarActivity extends DrawerActivity {

    @Override
    protected void onFragmentContainerReady() {
        addFragment(CalendarFragment.newInstance(), false /* don't add to back stack */);
    }

    @Override
    protected int getActivityTitle() {
        return R.string.calendar_title;
    }
}
