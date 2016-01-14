package com.techlung.moodtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.techlung.moodtracker.greendao.extended.DaoFactory;
import com.techlung.moodtracker.greendao.extended.ExtendedMoodScopeDao;
import com.techlung.moodtracker.greendao.generated.MoodScope;
import com.techlung.moodtracker.logbook.LogListFragment;
import com.techlung.moodtracker.modescope.MoodScopeActivity;
import com.techlung.moodtracker.settings.Preferences;
import com.techlung.moodtracker.settings.SettingsActivity;
import com.techlung.moodtracker.tracking.TrackingFragment;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    public static String OPEN_TRACKING = "OPEN_TRACKING";
    private boolean openTracking = false;

    private static MainActivity instance;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        openTracking = getIntent().getBooleanExtra(OPEN_TRACKING, false);

        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        checkAndDoFirstStart();
        setUserToUi();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_moodscopes) {
            Intent intent = new Intent(this, MoodScopeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_import) {
            // TODO Import
            Toast.makeText(this, "TODO Import", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_export) {
            // TODO Export
            Toast.makeText(this, "TODO Export", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_share) {
            // TODO Share
            Toast.makeText(this, "TODO Share", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkAndDoFirstStart() {
        if (Preferences.isFirstStartup()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View inputLayout = LayoutInflater.from(this).inflate(R.layout.alert_input, null);
            final EditText input = (EditText) inputLayout.findViewById(R.id.alert_input);
            input.setHint(R.string.first_start_what_is_your_name_hint);

            builder.setView(inputLayout);
            builder.setCancelable(false);
            builder.setTitle(R.string.first_start_what_is_your_name);
            builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String userName = input.getText().toString().trim();

                    Preferences.setUserName(userName);
                    setUserToUi();
                    initDatabase();
                    Preferences.setFirstStart(false);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle(String.format(getString(R.string.first_start_welcome), userName));
                    builder.setMessage(R.string.first_start_welcome_message);
                    builder.setPositiveButton(R.string.alert_thanks, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });

            builder.show();

        }
    }

    private void setUserToUi() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        TextView userTextView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_user);
        userTextView.setText(Preferences.getUserName().toUpperCase());
    }
    private void initDatabase() {
        ExtendedMoodScopeDao extendedMoodScopeDao =  DaoFactory.getInstance(this).getExtendedMoodScopeDao();

        extendedMoodScopeDao.insertOrReplace(new MoodScope(0l, "Work", 1));
        extendedMoodScopeDao.insertOrReplace(new MoodScope(1l, "Family", 2));
        extendedMoodScopeDao.insertOrReplace(new MoodScope(2l, "Social", 3));
        extendedMoodScopeDao.insertOrReplace(new MoodScope(3l, "Overall", 4));
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        LogListFragment logListFragment;
        TrackingFragment trackingFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (trackingFragment == null) {
                        trackingFragment = new TrackingFragment();
                        trackingFragment.setOpenTracking(openTracking);
                    }
                    return trackingFragment;
                case 1:
                    if (logListFragment == null) {
                        logListFragment = new LogListFragment();
                    }
                    return logListFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return MainActivity.this.getString(R.string.title_tab_tracking);
                case 1:
                    return MainActivity.this.getString(R.string.title_tab_logbook);
            }
            return null;
        }
    }
}
