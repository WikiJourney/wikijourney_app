package eu.wikijourney.wikijourney.functions;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.wikijourney.wikijourney.GlobalState;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * This is the class used to store the information we get for each POI using the WikiJourney API.<br/>
 * Created by Thomas on 25/07/2015.
 */
public class POI {

    // We define every variable returned by the WikiJourney API, with the same name as in the JSON,
    // so Gson car work.
    private double latitude;
    private double longitude;
    private String name;
    private String sitelink;
    private String type_name;
    private int type_id;
    private int id;
    private String image_url;
    private String description;

    /**
     * Public constructor, maybe needed by Gson
     */
    public POI(double latitude, double longitude, String name, String sitelink, String type_name, int type_id, int id, String image_url) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.sitelink = sitelink;
        this.type_name = type_name;
        this.type_id = type_id;
        this.id = id;
        this.image_url = image_url;
    }

    /**
     * Parses the WikiJourney server's response.<br/>
     * It also sets the Singleton variable poiList.
     * @param pServerResponsePOI The JSON response of the server
     * @param method The method used to call the API (around the user or around a specific place
     * @param pContext A context of the app, so we can get the ApplicationContext and store the poiList
     * @return An ArrayList of POI
     */
    public static ArrayList<POI> parseApiJson(JSONObject pServerResponsePOI, int method, Context pContext) {
        Gson mGson = new Gson();
        JSONArray poiInfoArray = null;

        // The useful part of the response is in the poi.poi_info part of the JSON
        // TODO We currently have NO error handling!
        try {
            poiInfoArray = pServerResponsePOI.getJSONObject("poi").getJSONArray("poi_info");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // We create a local ArrayList of POI, to retrieve its Type, so Gson knows how to store the
        // JSON in the ArrayList<POI>
        ArrayList<POI> mPoiArrayList = new ArrayList<>();
        Type arrayPoiType = new TypeToken<ArrayList<POI>>(){}.getType();
        String responseString = null;
        try {
            responseString = poiInfoArray.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (responseString != null && !"".equals(responseString)) {
            mPoiArrayList = mGson.fromJson(responseString, arrayPoiType);
        } else {
            mPoiArrayList = null;
        }

        // We then store the poiList in HomeActivity, so it can be accessed anywhere
        GlobalState gs = ((GlobalState)pContext.getApplicationContext());
        gs.setPoiList(mPoiArrayList);
        return mPoiArrayList;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }
}
