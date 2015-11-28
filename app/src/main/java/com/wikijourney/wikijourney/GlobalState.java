package com.wikijourney.wikijourney;

import android.app.Application;

import com.wikijourney.wikijourney.functions.POI;

import java.util.ArrayList;

/**
 * This class should hold all variables useful for the whole app.
 * For now, it contains the list of POIs.
 * http://stackoverflow.com/questions/708012/how-to-declare-global-variables-in-android?answertab=votes#tab-top
 * Created by Thomas on 16/10/15.
 */
public class GlobalState extends Application {
    private ArrayList<POI> poiList;

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the singletons so their instances
        // are bound to the application process.
        poiList = new ArrayList<>();
    }

    public ArrayList<POI> getPoiList() {
        return poiList;
    }

    public void setPoiList(ArrayList<POI> poiList) {
        this.poiList = poiList;
    }
}