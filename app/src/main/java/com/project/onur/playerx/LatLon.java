package com.project.onur.playerx;

/**
 * Created by onur on 6.10.2017 at 02:18.
 */

public class LatLon {
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