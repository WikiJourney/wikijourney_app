package com.wikijourney.wikijourney.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wikijourney.wikijourney.R;
import com.wikijourney.wikijourney.functions.Routing;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MapFragment extends Fragment {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        final MapView map = (MapView) view.findViewById(R.id.map);

        // These lines initialize the map settings
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(9);



        // TODO This won't work with Fragments, change to Bundle (??)
        // We get the intent values
        final double[] finalCoord = getActivity().getIntent().getDoubleArrayExtra(HomeFragment.EXTRA_COORD);

        /* ====================== GETTING LOCATION ============================ */

        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                /* TODO Called when a new location is found by the network location provider. */
                drawMap(location, map, finalCoord, locationManager, this);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

/* ====================== END GETTING LOCATION ============================ */

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void drawMap(Location location, MapView map, double coord[], LocationManager locationManager, LocationListener locationListener) {
        Routing routing = new Routing(getActivity());
        Gson gson = new Gson();

        // TODO Temporary fix
        // This stop the location updates, so the map doesn't always refresh
        locationManager.removeUpdates(locationListener);

        IMapController mapController = map.getController();

        // This starts the map at the desired point
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
        Type arrayGeoType = new TypeToken<ArrayList<GeoPoint>>() {}.getType();
//        String geoPointsJSON = new HttpData(url).get().asString();
//        waypoints = gson.fromJson(geoPointsJSON, arrayGeoType);
        waypoints.add(startPoint);
//        GeoPoint endPoint = new GeoPoint(coord[0], coord[1]);
        GeoPoint endPoint = new GeoPoint(42.0, 2.0);
        waypoints.add(endPoint);

        // And we get the road between the points, we build the polyline between them
        //  Road road = roadManager.getRoad(waypoints);
        Road road = routing.buildRoute(roadManager, waypoints);
        // We add the road to the map, and we refresh the letter
        routing.drawPolyline(road, map, getActivity());

        // Now we add markers at each node of the route
        routing.drawWaypoints(road, map);

    }
}