package com.wikijourney.wikijourney;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wikijourney.wikijourney.fragments.HomeFragment;
import com.wikijourney.wikijourney.fragments.MapFragment;


public class HomeActivity extends AppCompatActivity {

    // Variables to use with the drawer
    private String[] mScreenTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private NavigationView mDrawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // We now add the Drawer to the View, and populate it with the resources
            mScreenTitles = getResources().getStringArray(R.array.screens_array);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerView = (NavigationView) findViewById(R.id.nav_view);

            //setting up selected item listener
            mDrawerView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });

            mDrawerTitle = mTitle = getTitle();
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    R.string.drawer_open, R.string.drawer_close) {
                /* Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    getSupportActionBar().setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }

                /* Called when a drawer has settled in a completely open state.*/
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getSupportActionBar().setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };

            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);

/*
            // Set the Adapter for the ListView
            mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                    R.layout.drawer_list_item, mScreenTitles));
            // Set the list's click listener
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
*/

            // Create a new Fragment to be placed in the activity layout
            HomeFragment firstFragment = new HomeFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerView);
                if (!drawerOpen) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                else {
                    mDrawerLayout.closeDrawers();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // See https://stackoverflow.com/a/28322881 for more info
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps Fragments in the main View */
    private void selectItem(int position) {
        // Create a new fragment and specify the screen to show based on position
        switch (position) {
            case 0:
                if (findViewById(R.id.banner) != null) break; // If we are already at the HomeFragment, do nothing
                // Else insert the fragment by replacing any existing fragment
                HomeFragment homeFragment = new HomeFragment();
                mTitle = getString(R.string.app_name);
                FragmentManager homeFragmentManager = getFragmentManager();
                homeFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle(mTitle);
            case 1:
                if (findViewById(R.id.map) != null) break; // If we are already at the MapFragment, do nothing
                // Else insert the fragment by replacing any existing fragment
                MapFragment mapFragment = new MapFragment();
                mTitle = mScreenTitles[position];
                FragmentManager mapFragmentManager = getFragmentManager();
                mapFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mapFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle(mTitle);
            default:
                break;
        }
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerView);
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
        menu.findItem(R.id.action_about).setVisible(!drawerOpen);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getFragmentManager().popBackStack();
        setTitle(getString(R.string.app_name));
        return super.onSupportNavigateUp();
    }
}
