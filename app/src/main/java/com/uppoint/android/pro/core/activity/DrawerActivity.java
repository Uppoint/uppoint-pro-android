package com.uppoint.android.pro.core.activity;

import com.uppoint.android.pro.R;
import com.uppoint.android.pro.calendar.activity.CalendarActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class DrawerActivity extends BaseActivity {

    private static List<DrawerItem> sDrawerItems;

    private ActionBarDrawerToggle mDrawerToggle;

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpDrawerList();
        setUpDrawerToggle();
    }

    private void setUpDrawerToggle() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_container);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(),
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void setUpDrawerList() {
        final DrawerAdapter drawerAdapter = new DrawerAdapter(this, getDrawerItems(), getClass());
        final int selectedPosition = drawerAdapter.getCurrentItemPosition();
        final ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        drawerList.setAdapter(drawerAdapter);
        drawerList.setItemChecked(selectedPosition, true);
        drawerList.setOnItemClickListener(new DrawerItemOnClickListener());
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_drawer;
    }

    private static List<DrawerItem> getDrawerItems() {
        if (sDrawerItems == null) {
            sDrawerItems = new ArrayList<>();
            sDrawerItems.add(new DrawerItem(R.drawable.ic_today_black_24dp, R.string.calendar_title,
                    CalendarActivity.class));
            sDrawerItems.add(new DrawerItem(R.drawable.ic_face_black_24dp, R.string.profile_title, null));
            sDrawerItems.add(new DrawerItem(R.drawable.ic_list_black_24dp, R.string.services_title, null));
            sDrawerItems.add(new DrawerItem(R.drawable.ic_schedule_black_24dp, R.string.office_hours_title, null));
            sDrawerItems.add(new DrawerItem(R.drawable.ic_exit_to_app_black_24dp, R.string.logout, null));
        }

        return sDrawerItems;
    }

    protected static class DrawerItem {

        public int iconResId;
        public int titleResId;
        public Class<? extends BaseActivity> targetClass;

        public DrawerItem(@DrawableRes int iconResId, @StringRes int titleResId,
                Class<? extends BaseActivity> targetClass) {
            this.iconResId = iconResId;
            this.titleResId = titleResId;
            this.targetClass = targetClass;
        }

    }

    protected static class DrawerAdapter extends ArrayAdapter<DrawerItem> {

        private final Class<? extends BaseActivity> mCurrentClass;

        public DrawerAdapter(Context context, List<DrawerItem> objects, Class<? extends BaseActivity> currentClass) {
            super(context, R.layout.list_item_drawer, objects);

            mCurrentClass = currentClass;
        }

        protected int getCurrentItemPosition() {
            for (int i = 0; i < getCount(); i++) {
                final DrawerItem item = getItem(i);
                if (mCurrentClass.equals(item.targetClass)) {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_drawer, parent, false);

                final ViewHolder holder = new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.drawer_item_icon);
                holder.title = (TextView) convertView.findViewById(R.id.drawer_item_title);

                convertView.setTag(holder);
            }

            final DrawerItem item = getItem(position);
            final ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.icon.setImageResource(item.iconResId);
            holder.title.setText(item.titleResId);

            return convertView;
        }

        private class ViewHolder {

            public ImageView icon;
            public TextView title;
        }
    }

    private class DrawerItemOnClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final DrawerItem item = (DrawerItem) parent.getItemAtPosition(position);
            if (DrawerActivity.this.getClass().equals(item.targetClass)) {
                // do nothing when the same item is selected
                return;
            }

            if (item.targetClass == null) {
                Toast.makeText(DrawerActivity.this, getString(item.titleResId) + " is under construction",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            final ListView drawerList = (ListView) parent;
            drawerList.setItemChecked(position, true);
            final Intent intent = new Intent(DrawerActivity.this, item.targetClass);
            startActivity(intent);
        }
    }
}
