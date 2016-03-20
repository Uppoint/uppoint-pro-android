package com.uppoint.android.pro.calendar.activity;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.fragment.CalendarFragment;
import com.uppoint.android.pro.calendar.fragment.NewEventFragment;
import com.uppoint.android.pro.core.util.DatabaseUtil;
import com.uppoint.android.pro.db.Helper;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;

/**
 */
public class CalendarActivity extends BaseCalendarActivity {

    @Override
    protected void onFragmentContainerReady() {
        addFragment(CalendarFragment.newInstance(), false /* don't add to back stack */);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_database_utils, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_extract_db == item.getItemId()) {
            extractDatabase();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void extractDatabase() {
        try {
            DatabaseUtil.extractDatabase(this, Helper.DB_NAME);
        } catch (IOException e) {
            Log.e("Database", "Could not extract database", e);
        }
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
