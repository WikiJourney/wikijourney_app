package eu.wikijourney.wikijourney.views;

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

import eu.wikijourney.wikijourney.functions.UI;
import eu.wikijourney.wikijourney.functions.Utils;

public class HomeFragment extends Fragment implements View.OnClickListener {

    // TODO Should this be defined in the Singleton GlobalState, since these are global constants?
    public final static String[] EXTRA_OPTIONS = { "com.wikijourney.wikijourney.MAX_POI",
            "com.wikijourney.wikijourney.RANGE",
            "com.wikijourney.wikijourney.PLACE",
            "com.wikijourney.wikijourney.METHOD",
            "com.wikijourney.wikijourney.URI" };
    public final static int METHOD_AROUND = 0;
    public final static int METHOD_PLACE = 1;
    public final static int METHOD_URI = 2;

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
        View view = inflater.inflate(eu.wikijourney.wikijourney.R.layout.fragment_home, container, false);

        // While creating the layout, we set the OnClickListener on the buttons
        // See https://stackoverflow.com/a/14571018 for more info
        Button goButton = (Button) view.findViewById(eu.wikijourney.wikijourney.R.id.go_place);
        Button goAround = (Button) view.findViewById(eu.wikijourney.wikijourney.R.id.go_around);

        goButton.setOnClickListener(this);
        goAround.setOnClickListener(this);

        // We get now the LocationManager, so we can display the PopUp if the user hasn't enabled it
        locationManager = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE );

        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            UI.openPopUp(this.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_GPS_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_GPS));
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
        // Checking if the user has enabled both Internet access and Geolocation
        // TODO Should this be checked before, so we don't check it twice in each case?
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // The next part depends on which button was clicked
        switch (view.getId()) {
            case eu.wikijourney.wikijourney.R.id.go_place:
                if (networkInfo != null && networkInfo.isConnected()) {
                    boolean emptyString = ((EditText) getActivity().findViewById(eu.wikijourney.wikijourney.R.id.input_place)).getText().toString().matches("");
                    if(!emptyString)
                    {
                        goMap(view.getRootView(), METHOD_PLACE);
                    } else
                        UI.openPopUp(this.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_empty_destination_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_empty_destination));
                } else {
                    UI.openPopUp(this.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet));
                }
                break;
            case eu.wikijourney.wikijourney.R.id.go_around:
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (gpsEnabled) {
                        goMap(view.getRootView(), METHOD_AROUND);
                    } else {
                        UI.openPopUp(this.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_GPS_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_GPS));
                    }
                    break;
                } else {
                    UI.openPopUp(this.getActivity(), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet_title), getResources().getString(eu.wikijourney.wikijourney.R.string.error_activate_internet));
                }
            default:
                break;
        }
    }

    private void goMap(View pView, int method) {
        // We store the Resources to res, so we can get the actual value of the integers instead of their ID
        Resources res = getResources();
        // We get the options entered by the user, and store them in a Bundle
        Bundle args = new Bundle();

        //We find the MaxPOI value
        EditText maxPOIInput = (EditText)pView.findViewById(eu.wikijourney.wikijourney.R.id.input_maxPOI);
        try {
            int maxPOI = Integer.parseInt(maxPOIInput.getText().toString());
            args.putInt(EXTRA_OPTIONS[0], maxPOI);
        } catch (NumberFormatException e) {
            args.putInt(EXTRA_OPTIONS[0], res.getInteger(eu.wikijourney.wikijourney.R.integer.default_maxPOI)); //TODO : Let the user fix this default value thanks to Options Menu
        }

        //We find the range value
        EditText rangeInput = (EditText)pView.findViewById(eu.wikijourney.wikijourney.R.id.input_range);
        try {
            double range = Double.parseDouble(rangeInput.getText().toString());
            args.putDouble(EXTRA_OPTIONS[1], range);
        } catch (NumberFormatException e) {
            args.putDouble(EXTRA_OPTIONS[1], res.getInteger(eu.wikijourney.wikijourney.R.integer.default_range)); //TODO : Let the user fix this default value thanks to Options Menu
        }

        //If mode is around a place, we get the place
        if(method == METHOD_PLACE) {
            EditText placeInput = (EditText) pView.findViewById(eu.wikijourney.wikijourney.R.id.input_place);
            try {
                String place = placeInput.getText().toString();
                args.putString(EXTRA_OPTIONS[2], place);
            } catch (NumberFormatException e) {
                args.putString(EXTRA_OPTIONS[2], "");
            }
        }
        else
            args.putString(EXTRA_OPTIONS[2], "");

        args.putInt(EXTRA_OPTIONS[3], method);

        //We hide the keyboard
        Utils.hideKeyboard(getActivity(), getActivity().getCurrentFocus());

        // We change the Fragment
        // Create fragment and give it an argument specifying the options and the place if exists
        MapFragment newFragment = new MapFragment();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(eu.wikijourney.wikijourney.R.id.fragment_container, newFragment);
        transaction.addToBackStack("MapFragmentFindingPoi");

        // Commit the transaction
        transaction.commit();
    }

}
