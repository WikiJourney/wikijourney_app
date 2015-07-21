package com.wikijourney.wikijourney.functions;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.wikijourney.wikijourney.R;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class Routing {

    private Context context;

    public Routing(Context pContext) {
        context = pContext;
    }

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

    // Build route between points
    public Road buildRoute(RoadManager roadManager, ArrayList<GeoPoint> arrayList) {
        Road route = roadManager.getRoad(arrayList);
        return route;
    }

    //
    // Create Polyline to bind nodes of the route, and draw it
    public void drawPolyline(Road route, MapView map, Context context) {
        Polyline roadOverlay = RoadManager.buildRoadOverlay(route, context);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }

    public void drawWaypoints(Road road, MapView map) {
        /* TODO add support for multiple directions markers type */
        Drawable nodeIcon = context.getResources().getDrawable(R.drawable.marker_node);
        for (int i = 0; i < road.mNodes.size(); i++) {
            RoadNode node = road.mNodes.get(i); // We get the i-ème node of the route
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle(context.getString(R.string.step_nbr) + " " + i);
            map.getOverlays().add(nodeMarker);

            // And we fill the bubbles with the directions
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));

            // Finally, we add an icon to the bubble
            Drawable icon = context.getResources().getDrawable(R.drawable.ic_continue);
            switch (node.mManeuverType) {
                case 1:
                    icon = context.getResources().getDrawable(R.drawable.ic_continue);
                    break;
                case 3:
                    icon = context.getResources().getDrawable(R.drawable.ic_slight_left);
                    break;
                case 4:
                    icon = context.getResources().getDrawable(R.drawable.ic_turn_left);
                    break;
                case 5:
                    icon = context.getResources().getDrawable(R.drawable.ic_sharp_left);
                    break;
                case 6:
                    icon = context.getResources().getDrawable(R.drawable.ic_slight_right);
                    break;
                case 7:
                    icon = context.getResources().getDrawable(R.drawable.ic_turn_right);
                    break;
                case 8:
                    icon = context.getResources().getDrawable(R.drawable.ic_sharp_right);
                    break;
                case 12:
                    icon = context.getResources().getDrawable(R.drawable.ic_u_turn);
                    break;
                default:
                    break;

            }
            nodeMarker.setImage(icon);
            map.invalidate();
        }
    }
}