package eu.wikijourney.wikijourney;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import eu.wikijourney.wikijourney.views.AboutFragment;
import eu.wikijourney.wikijourney.views.HomeFragment;
import eu.wikijourney.wikijourney.views.MapFragment;
import eu.wikijourney.wikijourney.views.OptionsFragment;
import eu.wikijourney.wikijourney.views.PoiListFragment;

import de.k3b.geo.api.GeoPointDto;
import de.k3b.geo.api.IGeoPointInfo;
import de.k3b.geo.io.GeoUri;


public class HomeActivity extends AppCompatActivity {

    private LocationManager locationManager;

    // Variables to use with the drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private NavigationView mDrawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // To check if the user has enabled GPS
        locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerView = (NavigationView) findViewById(R.id.nav_view);

            //setting up selected item listener
            mDrawerView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
//                            menuItem.setChecked(true);
                            selectItem(menuItem.getTitle());
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
                    setTitle(mTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                    // With this, we add or remove Toolbar buttons depending on drawer state
                }

                /* Called when a drawer has settled in a completely open state.*/
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    setTitle(mDrawerTitle);
                    invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                }
            };
            // Set the drawer toggle as the DrawerListener
            mDrawerLayout.setDrawerListener(mDrawerToggle);

            Fragment firstFragment = null;

            // k3b: when startet via geo-uri then start map with these data else start home
            // where you can enter parameters
            Uri uri = getIntent().getData();
            String geoUriString = (uri != null) ? uri.toString() : null;
            GeoUri parser = new GeoUri(GeoUri.OPT_DEFAULT);
            IGeoPointInfo geoPoint = parser.fromUri(geoUriString);

            if ((geoPoint != null) && ((geoPoint.getName() != null) || !GeoPointDto.isEmpty(geoPoint))) {
                Resources res = getResources();

                Bundle args = new Bundle();

                args.putInt(HomeFragment.EXTRA_OPTIONS[3], HomeFragment.METHOD_URI);
                args.putString(HomeFragment.EXTRA_OPTIONS[4], geoUriString);
                args.putInt(HomeFragment.EXTRA_OPTIONS[0], res.getInteger(R.integer.default_maxPOI));
                args.putDouble(HomeFragment.EXTRA_OPTIONS[1], res.getInteger(R.integer.default_range));

                firstFragment = new MapFragment();
                firstFragment.setArguments(args);
            } else {
                // Create a new Fragment to be placed in the activity layout
                firstFragment = new HomeFragment();

                // In case this activity was started with special instructions from an
                // Intent, pass the Intent's extras to the fragment as arguments
                firstFragment.setArguments(getIntent().getExtras());
            }

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        // This makes the arrow/hamburger menu animate
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
            default: // Do nothing
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        }
        // See https://stackoverflow.com/a/28322881 for more info
        else if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
            setTitle(R.string.app_name); //TODO display the correct title, not always "WikiJourney"
        } else {
            super.onBackPressed();
        }
    }

    private boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    private void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Swaps Fragments in the main View
     */
    private void selectItem(CharSequence title) {
        // This is to get the position of the item in the menu... ><
        // TODO Optimize this, see if it can be done natively
        int i = 0;
        String[] drawerStrings = getResources().getStringArray(R.array.screens_array);
        for (String string:drawerStrings) {
            if (title.toString().equals(string)) {
                break;
            }
            i++;
        }

        // Create a new fragment and specify the screen to show based on position
        switch (i) {
            case 0:
                if (findViewById(R.id.banner) != null) break; // If we are already at the HomeFragment, do nothing
                // Else insert the fragment by replacing any existing fragment
                HomeFragment homeFragment = new HomeFragment();
                mTitle = getString(R.string.app_name);
                FragmentManager homeFragmentManager = getFragmentManager();
                homeFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .addToBackStack(null) // TODO Do we really need to add it to the Back stack?
                        .commit();
                setTitle(mTitle);
                break;
            case 1:
                if (findViewById(R.id.map) != null) break; // If we are already at the MapFragment, do nothing
                // Else insert the fragment by replacing any existing fragment
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    MapFragment mapFragment = new MapFragment();
                    mTitle = drawerStrings[i];
                    FragmentManager mapFragmentManager = getFragmentManager();
                    mapFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, mapFragment)
                            .addToBackStack(null)
                            .commit();
                    setTitle(mTitle);
                }
                break;
            case 2:
                if (findViewById(R.id.poi_list) != null) break; // If we are already at the PoiListFragment, do nothing
                // Else insert the fragment by replacing any existing fragment
                PoiListFragment poiListFragment = new PoiListFragment();
                mTitle = drawerStrings[i];
                FragmentManager poiListFragmentManager = getFragmentManager();
                poiListFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, poiListFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle(mTitle);
                break;
            case 3:
                if (findViewById(R.id.options_page) != null) break; // If we are already at the OptionsFragment, do nothing
                // Else insert the fragment by replacing any existing fragment
                OptionsFragment optionsFragment = new OptionsFragment();
                mTitle = drawerStrings[i];
                FragmentManager optionsFragmentManager = getFragmentManager();
                optionsFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, optionsFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle(mTitle);
                break;
            case 4:
                AboutFragment aboutFragment = new AboutFragment();
                mTitle = drawerStrings[i];
                FragmentManager aboutFragmentManager = getFragmentManager();
                aboutFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, aboutFragment)
                        .addToBackStack(null)
                        .commit();
                setTitle(mTitle);
                break;

            default:
                Log.i("Value","Default");
                break;
        }
        closeNavDrawer();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerView);
//        menu.findItem(R.id.action_search).setVisible(!drawerOpen);
//        menu.findItem(R.id.action_about).setVisible(!drawerOpen);
//        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getFragmentManager().popBackStack();
        setTitle(getString(R.string.app_name));
        return super.onSupportNavigateUp();
    }
}
