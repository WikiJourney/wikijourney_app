package com.wikijourney.wikijourney.functions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wikijourney.wikijourney.R;
import com.wikijourney.wikijourney.WikiJourneyApplication;
import com.wikijourney.wikijourney.views.MapFragment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.lang.reflect.Type;
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
     * @param pServerResponsePOI The JSON got from the WikiJourney API, converted to a String
     */
    public static void drawPOI(MapFragment pMapFragment, JSONObject pServerResponsePOI) {
        MapView mMap = (MapView) pMapFragment.getActivity().findViewById(R.id.map);
        Context mContext = pMapFragment.getActivity();

        Gson mGson = new Gson();
        JSONObject mServerResponsePOI;
        JSONObject mGeoPointsJSON;
        JSONArray mFinalResponse = null;

        try {
//            mServerResponsePOI = new JSONObject(pServerResponsePOI);
            mServerResponsePOI = pServerResponsePOI;
            mGeoPointsJSON = mServerResponsePOI.getJSONObject("poi");
            mFinalResponse = mGeoPointsJSON.getJSONArray("poi_info");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<POI> mPoiArrayList;
        Type arrayPoiType = new TypeToken<ArrayList<POI>>(){}.getType();
        String responseString = null;
        try {
            responseString = mFinalResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPoiArrayList = mGson.fromJson(responseString, arrayPoiType);

        // We then store the poiList in HomeActivity, so it can be accessed anywhere
        WikiJourneyApplication appState = ((WikiJourneyApplication)mContext.getApplicationContext());
        appState.setPoiList(mPoiArrayList);

        // We create an Overlay Folder to store every POI, so that they are grouped in clusters
        // if there are too many of them
        final RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(mContext);
        Drawable mClusterIconDrawable = ContextCompat.getDrawable(mContext, R.drawable.marker_cluster);
        Bitmap mClusterIcon = ((BitmapDrawable)mClusterIconDrawable).getBitmap();
        poiMarkers.setIcon(mClusterIcon);
        mMap.getOverlays().add(poiMarkers);

        CustomInfoWindow mCustomInfoWindow = new CustomInfoWindow(mMap);
        Drawable mMarkerIcon = ContextCompat.getDrawable(mContext, R.drawable.ic_place);

        // We add each POI to the Overlay Folder, with a custom icon, and the description bubble
        for (POI poi:mPoiArrayList) {
            double mLat = poi.getLatitude();
            double mLong = poi.getLongitude();
            GeoPoint poiWaypoint = new GeoPoint(mLat, mLong);
            Marker marker = new Marker(mMap);
            marker.setPosition(poiWaypoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setRelatedObject(poi);
            marker.setInfoWindow(mCustomInfoWindow);
            marker.setTitle(poi.getName());
            marker.setSnippet(poi.getSitelink());
            marker.setIcon(mMarkerIcon);
            poiMarkers.add(marker);
        }
        mMap.invalidate();
    }
}
