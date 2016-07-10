package eu.wikijourney.wikijourney.functions;

import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import eu.wikijourney.wikijourney.GlobalState;
import eu.wikijourney.wikijourney.R;
import eu.wikijourney.wikijourney.views.PoiListFragment;
import eu.wikijourney.wikijourney.views.WebFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Adapter linking the POI RecyclerView to the CardViews
 * Created by Thomas on 07/08/2015.
 */
public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {

    private final ArrayList<POI> mPoiList;
    private final Context context;
    private final PoiListFragment mPoiListFragment;
    private final GlobalState gs;

    // This can be used to retrieve the first lines, or summary, of a Wikipedia article
    private String WP_URL_PREFIX = "https://";
    private String language;
    private String WP_URL_TEXT = ".wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";
    public final static String EXTRA_URL = "com.wikijourney.wikijourney.POI_URL";


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // The components of one CardView
        private final ImageView mPoiPicture;
        private final TextView mPoiTitle;
        private final TextView mPoiDescription;
        private final Button mReadMoreButton;

        private ViewHolder(View v) {
            super(v);
            mPoiPicture = (ImageView) v.findViewById(R.id.poi_picture);
            mPoiTitle = (TextView) v.findViewById(R.id.poi_title);
            mPoiDescription = (TextView) v.findViewById(R.id.poi_description);
            mReadMoreButton = (Button) v.findViewById(R.id.read_more_button);
        }
    }

    /**
     * Public constructor for the PoiListAdapter
     * @param pContext The context of the View. It is needed for Picasso to display the WP article image.
     * @param poiListFragment The Fragment containing the PoiList. It is needed to change Fragments with the FragmentManager.
     */
    public PoiListAdapter(Context pContext, PoiListFragment poiListFragment) {
        this.context = pContext;
        this.mPoiListFragment = poiListFragment;
        this.gs = ((GlobalState) pContext.getApplicationContext());
        this.mPoiList = gs.getPoiList();
        this.language = Locale.getDefault().getLanguage();
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
        String poiName = gs.getPoiList().get(position).getName();
        final String mPoiSitelink = mPoiList.get(position).getSitelink();
        String mPoiImageUrl = mPoiList.get(position).getImageUrl();
        String mPoiDescription = mPoiList.get(position).getDescription();

        // We add a Listener, so that a tap on the card opens a WebView to the WP page
        if (mPoiSitelink != null && !"".equals(mPoiSitelink)) {
            holder.mReadMoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebFragment webFragment = new WebFragment();
                    Bundle args = new Bundle();
                    args.putString(EXTRA_URL, mPoiSitelink);
                    webFragment.setArguments(args);

                    FragmentTransaction transaction = mPoiListFragment.getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, webFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }

        if (poiName != null) {
            holder.mPoiTitle.setText(poiName);

            if (mPoiDescription == null) {
                downloadWikipediaExtract(holder, poiName, position);
            } else if ("".equals(mPoiDescription)) {
                holder.mPoiDescription.setVisibility(View.GONE);
            } else {
                holder.mPoiDescription.setVisibility(View.VISIBLE);
                holder.mPoiDescription.setText(mPoiDescription);
            }
        }

        holder.mPoiPicture.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo_cut));
        // We use Picasso to download the Wikipedia article image
        if (mPoiImageUrl != null && !"".equals(mPoiImageUrl)) {
            displayArticleImage(holder, mPoiImageUrl);
        }

    }

    private void downloadWikipediaExtract(final ViewHolder holder, String poiName, final int position) {
        // Download from the WP API
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(10_000); // Set timeout to 10s
        String url = null;
        try {
            url = WP_URL_PREFIX + language + WP_URL_TEXT + URLEncoder.encode(poiName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        client.get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String page_id;
                String extract;
                try {
                    page_id = response.getJSONObject("query").getJSONObject("pages").names().getString(0);
                    extract = response.getJSONObject("query").getJSONObject("pages").getJSONObject(page_id).getString("extract");
                    if (!"".equals(extract)) {
                        holder.mPoiDescription.setVisibility(View.VISIBLE);
                        holder.mPoiDescription.setText(extract);
                    }
                    gs.getPoiList().get(position).setDescription(extract);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                Log.d("progress", "Downloading " + bytesWritten + " of " + totalSize);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                try {
                    Log.e("Error", errorResponse.toString());
                } catch (Exception e) {
                    Log.e("Error", "Error while downloading the Wikipedia extract");
                }
            }

            @Override
            public void onRetry(int retryNo) {
                Log.e("Error", "Retrying for the " + retryNo + " time");
                super.onRetry(retryNo);
            }
        });
    }

    private void displayArticleImage(ViewHolder holder, String mPoiImageUrl) {
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