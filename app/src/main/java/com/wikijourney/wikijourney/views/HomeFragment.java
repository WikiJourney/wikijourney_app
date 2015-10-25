package com.wikijourney.wikijourney.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wikijourney.wikijourney.R;
import com.wikijourney.wikijourney.functions.UI;


public class HomeFragment extends Fragment implements View.OnClickListener {

    public final static String[] EXTRA_OPTIONS = { "com.wikijourney.wikijourney.MAX_POI",
            "com.wikijourney.wikijourney.RANGE",
            "com.wikijourney.wikijourney.PLACE",
            "com.wikijourney.wikijourney.METHOD" };
    public final static int METHOD_AROUND = 0;
    public final static int METHOD_PLACE = 1;

    private LocationManager locationManager;


    public HomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // While creating the layout, we set the OnClickListener on the buttons
        // See https://stackoverflow.com/a/14571018 for more info
        Button goButton = (Button) view.findViewById(R.id.go_place);
        Button goAround = (Button) view.findViewById(R.id.go_around);

        goButton.setOnClickListener(this);
        goAround.setOnClickListener(this);

        locationManager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            UI.openPopUp(this, getResources().getString(R.string.error_activate_GPS_title), getResources().getString(R.string.error_activate_GPS));
        }


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

    @Override
    public void onClick(View view) {
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        switch (view.getId()) {
            case R.id.go_place:
                if (networkInfo != null && networkInfo.isConnected()) {
                    boolean emptyString = ((EditText) getActivity().findViewById(R.id.input_place)).getText().toString().matches("");
                    if(!emptyString)
                    {
                        goMap(view.getRootView(), METHOD_PLACE);
                    } else
                        UI.openPopUp(this, getResources().getString(R.string.error_empty_destination_title), getResources().getString(R.string.error_empty_destination));
                } else {
                    UI.openPopUp(this, getResources().getString(R.string.error_activate_internet_title), getResources().getString(R.string.error_activate_internet));
                }
                break;
            case R.id.go_around:
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (gpsEnabled) {
                        goMap(view.getRootView(), METHOD_AROUND);
                    } else {
                        UI.openPopUp(this, getResources().getString(R.string.error_activate_GPS_title), getResources().getString(R.string.error_activate_GPS));
                    }
                    break;
                } else {
                    UI.openPopUp(this, getResources().getString(R.string.error_activate_internet_title), getResources().getString(R.string.error_activate_internet));
                }
            default:
                break;
        }
    }

    private void goMap(View pView, int method) {
        // We store the Resources to res, so we can get the actual value of the integers instead of their ID
        Resources res = getResources();
        // We get the options entered by the user, and store them in a double array
        Bundle args = new Bundle();

        //We find the MaxPOI value
        EditText maxPOIInput = (EditText)pView.findViewById(R.id.input_maxPOI);
        try {
            int maxPOI = Integer.parseInt(maxPOIInput.getText().toString());
            args.putInt(EXTRA_OPTIONS[0], maxPOI);
        } catch (NumberFormatException e) {
            args.putInt(EXTRA_OPTIONS[0], res.getInteger(R.integer.default_maxPOI)); //TODO : Let the user fix this default value thanks to Options Menu
        }

        //We find the range value
        EditText rangeInput = (EditText)pView.findViewById(R.id.input_range);
        try {
            double range = Double.parseDouble(rangeInput.getText().toString());
            args.putDouble(EXTRA_OPTIONS[1], range);
        } catch (NumberFormatException e) {
            args.putDouble(EXTRA_OPTIONS[1], res.getInteger(R.integer.default_range)); //TODO : Let the user fix this default value thanks to Options Menu
        }

        //If mode is around a place, we get the place
        if(method == METHOD_PLACE) {
            EditText placeInput = (EditText) pView.findViewById(R.id.input_place);
            try {
                String place = placeInput.getText().toString();
                args.putString(EXTRA_OPTIONS[2], place);
            } catch (NumberFormatException e) {
                args.putString(EXTRA_OPTIONS[2], "null");
            }
        }
        else
            args.putString(EXTRA_OPTIONS[2], "null");

        args.putInt(EXTRA_OPTIONS[3], method);


        // We change the Fragment
        // Create fragment and give it an argument specifying the options and the place if exists
        MapFragment newFragment = new MapFragment();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack("MapFragmentFindingPoi");

        // Commit the transaction
        transaction.commit();
    }

}
