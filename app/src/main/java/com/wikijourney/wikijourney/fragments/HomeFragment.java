package com.wikijourney.wikijourney.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wikijourney.wikijourney.MapActivity;
import com.wikijourney.wikijourney.R;


public class HomeFragment extends Fragment implements View.OnClickListener {

    public final static String EXTRA_COORD = "com.wikijourney.wikijourney.MESSAGE";

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
        Button goButton = (Button) view.findViewById(R.id.go);
        Button goToDestButton = (Button) view.findViewById(R.id.go_dest);

        goButton.setOnClickListener(this);
        goToDestButton.setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go:
                goMap(v.getRootView());
                break;
            case R.id.go_dest:
                goToDest(v.getRootView());
                break;
            default:
                break;
        }
    }

    public void goMap(View pView) {

        // We change the Fragment
        // Create fragment (no argument needed)
        MapFragment newFragment = new MapFragment();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void goToDest(View pView) {
        // We get the values entered by the user, and store them in a double array
        double[] coord = new double[2];
        EditText nsCoordInput = (EditText)pView.findViewById(R.id.n_s_coord);
        try {
            coord[0] = Double.parseDouble(nsCoordInput.getText().toString());
        } catch (NumberFormatException e) {
            coord[0] = 42.0;
        }
        EditText ewCoordInput = (EditText)pView.findViewById(R.id.e_w_coord);
        try {
            coord[1] = Double.parseDouble(ewCoordInput.getText().toString());
        } catch (NumberFormatException e) {
            coord[1] = 2.0;
        }

        Bundle args = new Bundle();
        args.putDoubleArray(EXTRA_COORD, coord);
        // We change the Fragment
        // Create fragment and give it an argument specifying the coordinates wanted
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
