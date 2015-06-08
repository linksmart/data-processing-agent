package com.fraunhofer.fit.driverapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.fit.fraunhofer.almanac.RouteEndpoint;
import org.fit.fraunhofer.almanac.RouteEndpointsList;

import java.util.ArrayList;

/**
 * Created by devasya on 26.05.2015.
 */
public class MqttListener implements MqttCallback ,IMqttActionListener{

    private final String TAG = "MqttListener";
    private final String TOPIC = "route";
    private MqttAndroidClient mClient ;
    private Context mContext;
    RouteUpdateHandler mRouteUpdateHandler;

    public void connect(Context context,RouteUpdateHandler routeUpdateHandler){
        mRouteUpdateHandler = routeUpdateHandler;
        new MqttserviceTask().execute(context);


    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.e(TAG, "Connection lost");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        String issueGson = new String(mqttMessage.getPayload());

        Gson gsonObj = new Gson();
        RouteEndpointsList routeEndpointsList = gsonObj.fromJson(issueGson, RouteEndpointsList.class);
        Log.i(TAG, "got message:" + issueGson + "object:" + routeEndpointsList.getString());
        mRouteUpdateHandler.handleUpdateNodeList(routeEndpointsList.getRouteList());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.i(TAG, "Publish successfull");
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        mClient.setCallback(this);
        try {
            mClient.subscribe(TOPIC,1);
        } catch (MqttException e) {
            Log.e(TAG, "Failed to suscribe because " + e.toString());
        }

        try {
           // String myMessage = "Hi hello how are you";
            ArrayList<RouteEndpoint>  routeEndpointsList = new ArrayList<RouteEndpoint>();


            RouteEndpoint SIEGBURG = new RouteEndpoint();
            SIEGBURG.geoLocation(50.793748, 7.202097);
            SIEGBURG.setId("SIEGBURG");
            routeEndpointsList.add(SIEGBURG);

            RouteEndpoint TROISDORF = new RouteEndpoint();
            TROISDORF.geoLocation(50.824345, 7.127174);
            TROISDORF.setId("TROISDORF");
            routeEndpointsList.add(TROISDORF);

            Gson gsonObj = new Gson();
            String issueGson = gsonObj.toJson(new RouteEndpointsList(routeEndpointsList));

            mClient.publish(TOPIC, issueGson.getBytes(), 1, false);
        } catch (MqttException e) {
            Log.e(TAG, "Failed to publish because " + e.toString());
        }
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        Log.i(TAG, "connection  failed");
    }

    public void disconnect() {

        try {
            mClient.disconnect();
        } catch (MqttException e) {
            Log.e(TAG, "Failed to disconnect because " + e.toString());
        }

    }


    private class MqttserviceTask extends AsyncTask<Context,Void,String> {

        @Override
        protected String doInBackground(Context... params) {
            mClient = new MqttAndroidClient(params[0],"tcp://m2m.eclipse.org:1883","FitClient");


            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            try {
                mClient.connect(connOpts, null, MqttListener.this);
            } catch (MqttException e) {
                Log.e(TAG,"Failed to connect because "+e.toString());

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //do nothing
        }

    }}
