package com.wikijourney.wikijourney.functions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wikijourney.wikijourney.HomeActivity;
import com.wikijourney.wikijourney.R;
import com.wikijourney.wikijourney.fragments.HomeFragment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Thomas on 01/08/2015.
 */
public class drawPOI extends AsyncTask<String, Void, Void> {

    // Required variables for the logic
    private MapView map;
    private Context context;
    private ContextCompat contextCompat;
    private Activity activity;

    private Gson gson = new Gson();
    private JSONObject serverResponsePOI = null;
    private JSONObject geoPointsJSON = null;
    private JSONArray finalResponse = null;


    public drawPOI(MapView map, ContextCompat contextCompat, Context context, Activity activity) {
        this.map = map;
        this.contextCompat = contextCompat;
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            geoPointsJSON = serverResponsePOI.getJSONObject("poi");
            finalResponse = geoPointsJSON.getJSONArray("poi_info");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<POI> poiList = new ArrayList<>();
        Type arrayPoiType = new TypeToken<ArrayList<POI>>(){}.getType();
        poiList = gson.fromJson(finalResponse.toString(), arrayPoiType);

        // We create an Overlay Folder to store every POI, so that they are grouped in clusters
        // if there are too many of them
        final RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(context);
        Drawable clusterIconD = ContextCompat.getDrawable(context, R.drawable.marker_cluster);
        Bitmap clusterIcon = ((BitmapDrawable)clusterIconD).getBitmap();
        poiMarkers.setIcon(clusterIcon);
        activity.runOnUiThread(new Runnable() {
            public void run() {
                map.getOverlays().add(poiMarkers);
            }
        });

        CustomInfoWindow customInfoWindow = new CustomInfoWindow(map);
        Drawable icon = contextCompat.getDrawable(context, R.drawable.ic_place);

        for (POI poi:poiList) {
            double mLat = poi.getLatitude();
            double mLong = poi.getLongitude();
            GeoPoint poiWaypoint = new GeoPoint(mLat, mLong);
            Marker marker = new Marker(map);
            marker.setPosition(poiWaypoint);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setRelatedObject(poi);
            marker.setInfoWindow(customInfoWindow);
            marker.setTitle(poi.getName());
            marker.setSnippet(poi.getSitelink());
            marker.setIcon(icon);
            poiMarkers.add(marker);
        }

    return null;
    }
}
