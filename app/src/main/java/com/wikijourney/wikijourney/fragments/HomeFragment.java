package com.wikijourney.wikijourney.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wikijourney.wikijourney.R;


public class HomeFragment extends Fragment implements View.OnClickListener {

    public final static String EXTRA_OPTIONS = "com.wikijourney.wikijourney.MESSAGE";

    private LocationManager locationManager;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    // Or delete??
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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

        // 1. Instantiate an AlertDialog.Builder with its constructor
        builder = new AlertDialog.Builder(getActivity());

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("The app cannot work without GPS. Please go to the Settings and activate GPS there.")
                .setTitle("Please activate GPS");

        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        // 3. Get the AlertDialog from create()
        dialog = builder.create();

        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            dialog.show();
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
        switch (view.getId()) {
            case R.id.go_place:
                if (gpsEnabled) {
                    goMap(view.getRootView(), "place");
                } else {
                    dialog.show();
                }
                break;
            case R.id.go_around:
                if (gpsEnabled) {
                    goMap(view.getRootView(), "around");
                } else {
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }

    public void goMap(View pView, String method) {
        // We get the options entered by the user, and store them in a double array

        String[] options = new String[3];

        //We find the MaxPOI value
        EditText maxPOIInput = (EditText)pView.findViewById(R.id.input_maxPOI);
        try {
            options[0] = maxPOIInput.getText().toString();
        } catch (NumberFormatException e) {
            options[0] = Integer.toString(R.integer.default_maxPOI); //TODO : Let the user fix this default value thanks to Options Menu
        }

        //We find the range value
        EditText rangeInput = (EditText)pView.findViewById(R.id.input_range);
        try {
            options[1] = rangeInput.getText().toString();
        } catch (NumberFormatException e) {
            options[1] = Integer.toString(R.integer.default_range); //TODO : Let the user fix this default value thanks to Options Menu
        }

        //If mode is around a place, we get the place
        if(method.equals("place")) {
            EditText placeInput = (EditText) pView.findViewById(R.id.input_place);
            try {
                options[2] = placeInput.getText().toString();
            } catch (NumberFormatException e) {
                options[2] = "null";
            }
        }
        else
            options[2] = "null";

        Bundle args = new Bundle();

        args.putStringArray(EXTRA_OPTIONS, options);
        // We change the Fragment
        // Create fragment and give it an argument specifying the options and the place if exists
        MapFragment newFragment = new MapFragment();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

}
