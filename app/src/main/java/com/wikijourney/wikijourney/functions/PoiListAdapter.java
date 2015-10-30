package com.wikijourney.wikijourney.functions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wikijourney.wikijourney.R;

import java.util.ArrayList;

/**
 * Adapter linking the POI RecyclerView to the CardViews
 * Created by Thomas on 07/08/2015.
 */
public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {

    private final ArrayList<POI> mPoiList;
    private final Context context;

    // This can be used to retrieve the first lines, or summary, of a Wikipedia article
    private String WP_URL_TEXT = "https://fr.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // The components of one CardView
        private final ImageView mPoiPicture;
        private final TextView mPoiTitle;
        private final TextView mPoiDescription;

        private ViewHolder(View v) {
            super(v);
            mPoiPicture = (ImageView) v.findViewById(R.id.poi_picture);
            mPoiTitle = (TextView) v.findViewById(R.id.poi_title);
            mPoiDescription = (TextView) v.findViewById(R.id.poi_description);
        }
    }

    /**
     * Public constructor for the PoiListAdapter
     * @param myPoiList The ArrayList of POIs that should be displayed
     * @param pContext The context of the View. It is needed for Picasso to display the WP article image
     */
    public PoiListAdapter(ArrayList<POI> myPoiList, Context pContext) {
        this.context = pContext;
        this.mPoiList = myPoiList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PoiListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poi_card_view, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from the PoiList at this position
        // - replace the contents of the view with that element
        String poiName = mPoiList.get(position).getName();
        final String mPoiSitelink = mPoiList.get(position).getSitelink();
        String mPoiImageUrl = mPoiList.get(position).getImageUrl();

        // We add a Listener, so that a tap on the card sends to the WP page
        // TODO Replace this with a WebView to integrate the WP page in the app
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPoiSitelink != null) {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPoiSitelink));
                    view.getContext().startActivity(myIntent);
                }
            }
        });

        holder.mPoiTitle.setText(poiName);
        holder.mPoiDescription.setText(mPoiSitelink);
        holder.mPoiPicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo_cut));
        // We use Picasso to download the Wikipedia article image
        Picasso.with(context).load(mPoiImageUrl)
                .placeholder(R.drawable.logo_cut)
                .fit()
                .centerCrop()
                .into(holder.mPoiPicture);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPoiList.size();
    }
}