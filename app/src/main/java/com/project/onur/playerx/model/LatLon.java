package com.project.onur.playerx.model;

import java.io.Serializable;

/**
 * Created by onur on 6.10.2017 at 02:18.
 */
@SuppressWarnings("serial")
public class LatLon implements Serializable{
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLon() {}

    public LatLon(double latitude,double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;


    }

}