package com.wikijourney.wikijourney.functions;

import android.graphics.drawable.Drawable;

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

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
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
