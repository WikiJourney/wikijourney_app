package eu.wikijourney.wikijourney.functions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import eu.wikijourney.wikijourney.R;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

public class Routing {

    private final Context context;

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

    /**
     * Converts list of POI to ArrayList<GeoPoint>
     * @param poiList The ArrayList of POIs to convert
     * @return An ArrayList of GeoPoints (includes only the latitude and longitude of the POI)
     */
    public ArrayList<GeoPoint> poiListToGeopointArraylist(ArrayList<POI> poiList) {
        ArrayList<GeoPoint> result = new ArrayList<>();

        for (POI poi:poiList) {
            result.add(new GeoPoint(poi.getLatitude(), poi.getLongitude()));
        }
        return result;
    }

    /**
     * Builds a route between points
     * @param roadManager The RoadManager chosen to calculate the itinerary
     * @param arrayList The list of GeoPoints to calculate the itinireray between
     * @return The Road between all points
     */
    public Road buildRoute(RoadManager roadManager, ArrayList<GeoPoint> arrayList) {
        Road route = roadManager.getRoad(arrayList);
        return route;
    }

    /**
     * Creates Polyline to bind nodes of the route, and draw it
     * @param route The Road calcuated between each GeoPoint
     * @param map The MapView to draw the Road on
     * @param context Needed to draw the Road, should be the Activity containing the MapView
     */
    public void drawPolyline(Road route, MapView map, Context context) {
        Polyline roadOverlay = RoadManager.buildRoadOverlay(route, context);
        map.getOverlays().add(roadOverlay);
        map.invalidate();
    }

    /**
     * Draws the WayPoints of the Road, with instructions on them
     * @param road The Road we are going to add the WayPoints to
     * @param map The MapView on which the WayPoints will be added
     */
    public void drawRoadWithWaypoints(Road road, MapView map) {
        /* TODO add support for even more directions markers type */
        Drawable nodeIcon = ContextCompat.getDrawable(context, R.drawable.marker_node);
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
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_continue);
            switch (node.mManeuverType) {
                case 1:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_continue);
                    break;
                case 3:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_slight_left);
                    break;
                case 4:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_turn_left);
                    break;
                case 5:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_sharp_left);
                    break;
                case 6:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_slight_right);
                    break;
                case 7:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_turn_right);
                    break;
                case 8:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_sharp_right);
                    break;
                case 12:
                    icon = ContextCompat.getDrawable(context, R.drawable.ic_u_turn);
                    break;
                default:
                    break;

            }
            nodeMarker.setImage(icon);
        }
        map.invalidate();
    }
}