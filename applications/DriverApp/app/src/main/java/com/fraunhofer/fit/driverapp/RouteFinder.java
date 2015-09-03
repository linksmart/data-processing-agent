package com.fraunhofer.fit.driverapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Shreekanth on 18-May-15.
 */
public class RouteFinder {
    private List<LatLng> mPath  ;
    public RouteFinder(List<LatLng> points){
        mPath = points;
    }

    public void createRoute(){
        
    }
}
