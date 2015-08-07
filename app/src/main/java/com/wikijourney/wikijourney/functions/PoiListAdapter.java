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

/**
 * Created by Thomas on 07/08/2015.
 */
public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {

    private POI[] mPoiList;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mPoiPicture;
        public TextView mPoiTitle;
        public TextView mPoiDescription;

        public ViewHolder(View v) {
            super(v);
            mPoiPicture = (ImageView) v.findViewById(R.id.poi_picture);
            mPoiTitle = (TextView) v.findViewById(R.id.poi_title);
            mPoiDescription = (TextView) v.findViewById(R.id.poi_description);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PoiListAdapter(POI[] myPoiList, Context pContext) {
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
        holder.mPoiTitle.setText(mPoiList[position].getName());
        holder.mPoiDescription.setText(mPoiList[position].getSitelink());
        holder.mPoiPicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo_cut));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mPoiList.length;
    }
}