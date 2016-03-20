package com.uppoint.android.pro.calendar.activity;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.fragment.CalendarFragment;
import com.uppoint.android.pro.calendar.fragment.NewEventFragment;

/**
 */
public class CalendarActivity extends BaseCalendarActivity {

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
