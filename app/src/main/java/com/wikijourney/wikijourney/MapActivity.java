package com.wikijourney.wikijourney;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.IllegalFormatException;


public class MapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Launch an activity with the Map layout
        setContentView(R.layout.activity_map);
        final MapView map = (MapView) findViewById(R.id.map);

        // We get the intent values
        double[] coord = new double[2];
        try {
            coord = getIntent().getDoubleArrayExtra(HomeActivity.EXTRA_COORD);
        }
        catch (RuntimeException error) {
            coord[0] = 42;
            coord[1] = 2;
        }
        final double[] finalCoord = coord;

/* ====================== GETTING LOCATION ============================ */


//        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                /* TODO Called when a new location is found by the network location provider. */
//                makeUseOfNewLocation(location);
                drawMap(location, map, finalCoord);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

/* ====================== END GETTING LOCATION ============================ */


//    drawMap(lastKnownLocation, map, coord);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
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

    public void drawMap(Location location, MapView map, double coord[]) {
        // These lines initialize the map settings
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9);

        // This starts the map at the desired point
//        final GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        final GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);

        // Now we add a marker using osmBonusPack
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);

        // And we have to use this to refresh the map
        map.invalidate();

        // We can change some properties of the marker (don't forget to refresh the map !!)
        // startMarker.setIcon(getResources().getDrawable(R.drawable.ic_logo));
        startMarker.setTitle(getString(R.string.you_are_here));
        map.invalidate();

        // Now we can also calculate and draw roads
        // First we need to choose a road manager
        RoadManager roadManager = new OSRMRoadManager();

        // Then we add some waypoints
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(coord[0], coord[1]);
        waypoints.add(endPoint);

        // And we get the road between the points, we build the polyline between them
        Road road = roadManager.getRoad(waypoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, this);

        // We add the road to the map, and we refresh the letter
        map.getOverlays().add(roadOverlay);
        map.invalidate();


        // Now we add markers at each node of the route
        Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
        for(int i=0; i<road.mNodes.size(); i++) {
            RoadNode node = road.mNodes.get(i); // We get the i-ème node of the route
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle(getString(R.string.step_nbr) + " " + i);
            map.getOverlays().add(nodeMarker);

            // And we fill the bubbles with the directions
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));

            // Finally, we add an icon to the bubble
            Drawable icon = getResources().getDrawable(R.drawable.ic_continue);
            nodeMarker.setImage(icon);
        }
        map.invalidate();
    }
}
