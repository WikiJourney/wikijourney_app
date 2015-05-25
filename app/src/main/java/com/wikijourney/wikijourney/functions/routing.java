package com.wikijourney.wikijourney.functions;

import android.graphics.drawable.Drawable;

import com.wikijourney.wikijourney.R;

import org.osmdroid.api.Polyline;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

// public class routing {
//
//    // Choose the RoadManager
//    public RoadManager chooseRoadManager() {
//        /* TODO Add support for other RoadManager, depending on the availability and if the user/program has API keys for it */
//        return new OSRMRoadManager();
//    }
//
//    // Convert list of POI to ArrayList<GeoPoint>
//    public ArrayList<GeoPoint> listToArraylist() {
//        /* TODO Complete function */
//        ArrayList<GeoPoint> result = new ArrayList<>();
//        return result;
//    }
//
//    // Build route between points
//    public Road buildRoute(RoadManager roadManager, ArrayList<GeoPoint> arrayList) {
//        Road route = roadManager.getRoad(arrayList);
//        return route;
//    }
//
//    // Create Polyline to bind nodes of the route, and draw it
//    public void drawPolyline(Road route, MapView map) {
//        Polyline roadOverlay = RoadManager.buildRoadOverlay(route, );
//        /* TODO Complete function with super context */
//        map.getOverlays().add(roadOverlay);
//        map.invalidate();
//    }
//
//    public void drawWaypoint(Road road, MapView map) {
//        /* TODO add support for multiple directions markers type */
//        Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
//        for(int i=0; i<road.mNodes.size(); i++) {
//            RoadNode node = road.mNodes.get(i); // We get the i-ème node of the route
//            Marker nodeMarker = new Marker(map);
//            nodeMarker.setPosition(node.mLocation);
//            nodeMarker.setIcon(nodeIcon);
//            nodeMarker.setTitle(getString(R.string.step_nbr) + " " + i);
//            map.getOverlays().add(nodeMarker);
//
//            // And we fill the bubbles with the directions
//            nodeMarker.setSnippet(node.mInstructions);
//            nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
//
//            // Finally, we add an icon to the bubble
//            Drawable icon = getResources().getDrawable(R.drawable.ic_continue);
//            nodeMarker.setImage(icon);
//        }
//        map.invalidate();
//    }
//}
