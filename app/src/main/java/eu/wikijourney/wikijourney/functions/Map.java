package eu.wikijourney.wikijourney.functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import eu.wikijourney.wikijourney.views.MapFragment;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

/**
 * Contains all Map related functions<br/><br/>
 * Created by Thomas on 03/08/2015.
 */
public class Map {
    /**
     * Displays on the map the POIs found by the WikiJourney API.<br/>
     * They are represented by a marker, with an info bubble containing the name of the page, its URL,
     * and a "More info" arrow button, which opens the default browser to the Wikipedia page.
     * @param pMapFragment The Fragment containing the MapView
     * @param pPoiArrayList The ArrayList of POIs, once it has been handled by the parseApiJson method
     */
    public static void drawPOI(MapFragment pMapFragment, ArrayList<POI> pPoiArrayList) {
        MapView mMap = null;
        try {
            mMap = (MapView) pMapFragment.getActivity().findViewById(eu.wikijourney.wikijourney.R.id.map);
        } catch (Exception e) {
            // If we cannot find the MapView (the Fragment was destroyed), abort.
            return;
        }
        Context mContext = pMapFragment.getActivity();

        // We create an Overlay Folder to store every POI, so that they are grouped in clusters
        // if there are too many of them
        final RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(mContext);
        Drawable mClusterIconDrawable = ContextCompat.getDrawable(mContext, eu.wikijourney.wikijourney.R.drawable.marker_cluster);
        Bitmap mClusterIcon = ((BitmapDrawable)mClusterIconDrawable).getBitmap();
        poiMarkers.setIcon(mClusterIcon);
        mMap.getOverlays().add(poiMarkers);

        // We create only one info window and one marker icon, and we will set it for each Marker
        CustomInfoWindow mCustomInfoWindow = new CustomInfoWindow(mMap);
        Drawable mMarkerIcon = ContextCompat.getDrawable(mContext, eu.wikijourney.wikijourney.R.drawable.ic_place);

        // We add each POI to the Overlay Folder, with a custom icon, and the description bubble
        if (pPoiArrayList != null && pPoiArrayList.size() != 0) {
            for (POI poi:pPoiArrayList) {
                double mLat = poi.getLatitude();
                double mLong = poi.getLongitude();
                GeoPoint poiWaypoint = new GeoPoint(mLat, mLong);

                Marker marker = new Marker(mMap);
                marker.setPosition(poiWaypoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setRelatedObject(poi); // This stores the POI related to each Marker, and allows to get it back in CustomInfoWindow.java
                marker.setInfoWindow(mCustomInfoWindow); // The CustomInfoWindow, with the More Info arrow
                marker.setTitle(poi.getName());
                marker.setSnippet(poi.getSitelink());
                marker.setIcon(mMarkerIcon);
                poiMarkers.add(marker);
            }
        }
        mMap.invalidate();
    }

}
