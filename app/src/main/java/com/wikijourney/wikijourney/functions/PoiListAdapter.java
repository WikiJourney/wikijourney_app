package com.wikijourney.wikijourney.functions;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wikijourney.wikijourney.R;

import java.util.ArrayList;

/**
 * Adapter linking the POI RecyclerView to the CardViews
 * Created by Thomas on 07/08/2015.
 */
public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {

    private final ArrayList<POI> mPoiList;
    private final Context context;

    private String WP_URL_TEXT = "https://fr.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";
    private String WP_URL_IMG = "https://fr.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&pithumbsize=1000&pilimit=1&titles=";


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView mPoiPicture;
        public final TextView mPoiTitle;
        public final TextView mPoiDescription;

        public ViewHolder(View v) {
            super(v);
            mPoiPicture = (ImageView) v.findViewById(R.id.poi_picture);
            mPoiTitle = (TextView) v.findViewById(R.id.poi_title);
            mPoiDescription = (TextView) v.findViewById(R.id.poi_description);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
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
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String poiName = mPoiList.get(position).getName();
        holder.mPoiTitle.setText(Utils.capitalizeFirstLetter(poiName));
        holder.mPoiDescription.setText(mPoiList.get(position).getSitelink());
        holder.mPoiPicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo_cut));
//        Picasso.with(context).load(WP_URL_IMG + poiName).centerCrop().into(holder.mPoiPicture);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPoiList.size();
    }
}