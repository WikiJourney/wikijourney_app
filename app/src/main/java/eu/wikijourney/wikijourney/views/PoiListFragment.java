package eu.wikijourney.wikijourney.views;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.wikijourney.wikijourney.GlobalState;
import eu.wikijourney.wikijourney.functions.POI;
import eu.wikijourney.wikijourney.functions.PoiListAdapter;

import java.util.ArrayList;

public class PoiListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public PoiListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(eu.wikijourney.wikijourney.R.layout.fragment_poi_list, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(eu.wikijourney.wikijourney.R.id.poi_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        // We first retrieve the PoiList stored in the Singleton
        GlobalState gs = ((GlobalState) getActivity().getApplicationContext());
        ArrayList<POI> poiList = gs.getPoiList();
        // We then pass it to the Adapter
        mAdapter = new PoiListAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

/*
    public void updatePoiList(String extract) {
        // TODO Update the POI extract
    }
*/
}
