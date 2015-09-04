package org.fit.fraunhofer.almanac;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Werner on 18.05.2015.
 */
public class RouteEndpoint implements Serializable {

    private String id;
    private double latitude;
    private double longitude;

    public RouteEndpoint(String id, double latitude, double longitude){
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public RouteEndpoint(){
        /*Nothing to do here */
    }

    public void setId(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

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



    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
    public String getString(){
        return id+":("+latitude+","+longitude+")";
    }
    public String getLatLngString(){
        return "("+latitude+","+longitude+")";
    }
}
