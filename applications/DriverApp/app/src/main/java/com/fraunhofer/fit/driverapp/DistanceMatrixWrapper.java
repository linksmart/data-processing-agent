package com.fraunhofer.fit.driverapp;

import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.fit.fraunhofer.almanac.RouteEndpoint;
import org.fit.fraunhofer.almanac.RouteEndpointsList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by devasya on 10.06.2015.
 */
public class DistanceMatrixWrapper {
    private final String mKey;
    private  final String TAG = "DistanceMatrixWrapper";
    public  interface  DistanceMatrixListener{
        public void onResult();
    }

    public DistanceMatrixWrapper(String APIkey){
        mKey = APIkey;
    }
   /* public int calculateDistance(List<RouteEndpoint> routeEndpointList){
        //https://maps.googleapis.com/maps/api/distancematrix/json?origins=Vancouver+BC|Seattle&destinations=San+Francisco|Victoria+BC&mode=bicycling&language=fr-FR&key=API_KEY
        StringBuilder uri = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?origins=");
        for(RouteEndpoint routeEndpoint: routeEndpointList){
            uri.append(routeEndpoint.getLatLng().latitude +","+routeEndpoint.getLatLng().longitude);
        }
        uri.append("&destinations=");
        for(RouteEndpoint routeEndpoint: routeEndpointList){
            uri.append(routeEndpoint.getLatLng().latitude +","+routeEndpoint.getLatLng().longitude);
        }
        uri.append("&mode=driving&key="+mKey);

        //TODO: as HTTPclient is deprecated
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(uri.toString());
        HttpResponse response = null;
        try {
            response = client.execute(get);

            int responseCode =  response.getStatusLine().getStatusCode();
            Log.i(TAG, "fetchImages:got response" + responseCode);
            if((200 != responseCode)){
                return 0;
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                jsonString.append(line);
            }
            rd.close();
            Log.i(TAG, "fetchImages:json retrieved:" + jsonString.toString());

            Type listType = new TypeToken<List<Images>>() {
            }.getType();
            Gson gson = new Gson();
            List<Distances> images = gson.fromJson(jsonString.toString(),listType );


        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/
}
