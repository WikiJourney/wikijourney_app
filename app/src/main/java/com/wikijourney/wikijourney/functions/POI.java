package com.wikijourney.wikijourney.functions;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wikijourney.wikijourney.WikiJourneyApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Thomas on 25/07/2015.
 * This is the class used to store the information we get for each POI using the WikiJourney API.
 */
public class POI {

    // We define every variable returned by the WikiJourney API
    private double latitude;
    private double longitude;
    private String name;
    private String sitelink;
    private String type_name;
    private int type_id;
    private int id;
//    public Drawable image;
    private String description;

    public POI(double latitude, double longitude, String name, String sitelink, String type_name, int type_id, int id) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.sitelink = sitelink;
        this.type_name = type_name;
        this.type_id = type_id;
        this.id = id;
    }

    static ArrayList<POI> parseApiJson(JSONObject pServerResponsePOI, Context pContext) {
        Gson mGson = new Gson();
        JSONArray poiInfoArray = null;

        try {
            poiInfoArray = pServerResponsePOI.getJSONObject("poi").getJSONArray("poi_info");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<POI> mPoiArrayList;
        Type arrayPoiType = new TypeToken<ArrayList<POI>>(){}.getType();
        String responseString = null;
        try {
            responseString = poiInfoArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPoiArrayList = mGson.fromJson(responseString, arrayPoiType);

        // We then store the poiList in HomeActivity, so it can be accessed anywhere
        WikiJourneyApplication appState = ((WikiJourneyApplication)pContext.getApplicationContext());
        appState.setPoiList(mPoiArrayList);
        return mPoiArrayList;
    }

//    public POI(double latitude, double longitude, String name, String sitelink, String type_name, int type_id, int id, Drawable image, String description) {
//        this.latitude = latitude;
//        this.longitude = longitude;
//        this.name = name;
//        this.sitelink = sitelink;
//        this.type_name = type_name;
//        this.type_id = type_id;
//        this.id = id;
//        this.image = image;
//        this.description = description;
//    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return Utils.capitalizeFirstLetter(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSitelink() {
        return sitelink;
    }

    public void setSitelink(String sitelink) {
        this.sitelink = sitelink;
    }

    public String getTypeName() {
        return type_name;
    }

    public void setTypeName(String type_name) {
        this.type_name = type_name;
    }

    public int getTypeId() {
        return type_id;
    }

    public void setTypeId(int type_id) {
        this.type_id = type_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public Drawable getImage() {
//        return image;
//    }
//
//    public void setImage(Drawable image) {
//        this.image = image;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
