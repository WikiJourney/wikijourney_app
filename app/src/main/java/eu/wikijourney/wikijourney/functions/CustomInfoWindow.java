package eu.wikijourney.wikijourney.functions;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * Custom info bubble, so we can get the related POI object and its properties<br/><br/>
 * Created by Thomas on 27/07/2015.
 */
public class CustomInfoWindow extends MarkerInfoWindow {
    private POI mSelectedPoi;

    /**
     * Public constructor for the CustomInfoWindow<br/>
     * It adds a More Info arrow, that sends to the Wikipedia page
     * @param mapView The MapView that will contain the bubble
     */
    public CustomInfoWindow(MapView mapView) {
        super(eu.wikijourney.wikijourney.R.layout.bonuspack_bubble, mapView);

        Button btn = (Button) (mView.findViewById(eu.wikijourney.wikijourney.R.id.bubble_moreinfo));

        // When the button is clicked, send to the Wikipedia page
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mSelectedPoi.getSitelink() != null) {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSelectedPoi.getSitelink()));
                    view.getContext().startActivity(myIntent);
                }
            }
        });
    }

    @Override
    public void onOpen(Object item){
        super.onOpen(item);
        // The button is in the bubble, but hidden; we need to make it visible
        mView.findViewById(eu.wikijourney.wikijourney.R.id.bubble_moreinfo).setVisibility(View.VISIBLE);

        // When the Marker is tapped, get the related POI, so the Listener set before works
        Marker marker = (Marker)item;
        mSelectedPoi = (POI)marker.getRelatedObject();
    }
}
