package org.fit.fraunhofer.almanac;

/**
 * Created by Werner-Kytölä on 18.05.2015.
 */
public class RouteEndpoints {

    private String id;
    private double latitude;
    private double longitude;

    public void id(String id){ this.id = id; }
    public void geoLocation(double lat, double longi) {
        this.latitude = lat;
        this.longitude = longi;
    }

    public void latitude(double lat) {
        this.latitude = lat;
    }

    public void longitude(double longi) {
        this.longitude = longi;
    }
}
