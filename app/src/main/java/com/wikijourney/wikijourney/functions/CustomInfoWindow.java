package com.wikijourney.wikijourney.functions;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.views.MapView;

/**
 * Created by Thomas on 27/07/2015.
 */
public class CustomInfoWindow extends MarkerInfoWindow {
    private POI mSelectedPoi;

    public CustomInfoWindow(MapView mapView) {
        super(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);

        Button btn = (Button) (mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo));

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mSelectedPoi.getSitelink() != null) {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mSelectedPoi.getSitelink()));
                    view.getContext().startActivity(myIntent);
                }
            }
        });
    }

    @Override public void onOpen(Object item){
        super.onOpen(item);
        mView.findViewById(org.osmdroid.bonuspack.R.id.bubble_moreinfo).setVisibility(View.VISIBLE);

        Marker marker = (Marker)item;
        mSelectedPoi = (POI)marker.getRelatedObject();
    }
}
