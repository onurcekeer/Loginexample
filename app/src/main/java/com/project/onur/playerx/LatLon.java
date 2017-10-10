package com.project.onur.playerx;

import java.io.Serializable;

/**
 * Created by onur on 6.10.2017 at 02:18.
 */
@SuppressWarnings("serial")
public class LatLon implements Serializable{
    private Double latitude;
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public LatLon() {}

    public LatLon(double latitude,double longitude) {

        this.latitude = latitude;
        this.longitude = longitude;


    }

}