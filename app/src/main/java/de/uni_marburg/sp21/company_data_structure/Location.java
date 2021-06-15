package de.uni_marburg.sp21.company_data_structure;

import java.io.Serializable;

public class Location implements Serializable {
    private double latitude;
    private double longitude;

    /**
     * Constructor
     * @param latitude The Latitude of the Location
     * @param longitude The Longitude of the Location
     */
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //------------------ GET / SET -------------------

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
