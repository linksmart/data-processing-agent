package org.fit.fraunhofer.almanac;

/**
 * Created by Werner-Kytölä on 18.05.2015.
 */
public class RouteEndpoint {

    private String id;
    private double latitude;
    private double longitude;

    public void setId(String id){ this.id = id; }
    public void setGeoLocation(double lat, double longi) {
        this.latitude = lat;
        this.longitude = longi;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public void setLongitude(double longi) {
        this.longitude = longi;
    }
}
