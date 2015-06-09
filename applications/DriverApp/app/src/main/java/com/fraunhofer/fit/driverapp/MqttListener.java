package com.fraunhofer.fit.driverapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by devasya on 26.05.2015.
 */
public class MqttListener implements MqttCallback ,IMqttActionListener{

    private final String TAG = "MqttListener";
    private final String TOPIC = "/almanac/route";
    private MqttAndroidClient mClient ;
    private Context mContext;
    RouteUpdateHandler mRouteUpdateHandler;

    public void connect(Context context,RouteUpdateHandler routeUpdateHandler){
        mRouteUpdateHandler = routeUpdateHandler;
        mContext = context;
        new MqttserviceTask().execute(context);


    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.e(TAG, "Connection lost");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        String issueGson = new String(mqttMessage.getPayload());
        Log.i(TAG, "got message:" + issueGson );
        Gson gsonObj = new Gson();
        Type listType = new TypeToken<List<RouteEndpoint>>() {
        }.getType();
        ArrayList<RouteEndpoint> routeEndpointsList = gsonObj.fromJson(issueGson,listType);
        Log.i(TAG, "object:" + routeEndpointsList.size());
        for(RouteEndpoint routeEndpoint:routeEndpointsList){
            Log.i(TAG,routeEndpoint.getString());
        }
        mRouteUpdateHandler.handleUpdateNodeList(routeEndpointsList);
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
        if(mContext.getResources().getBoolean(R.bool.testLocally)) {
            try {
                // String myMessage = "Hi hello how are you";
                ArrayList<RouteEndpoint> routeEndpointsList = new ArrayList<RouteEndpoint>();


                RouteEndpoint TOY_BIN = new RouteEndpoint();

                TOY_BIN.geoLocation(45.061148, 7.700376);//45.064537048339844, 7.696494102478027);//45.06723305,    7.70074879);
                TOY_BIN.setId("TOY_BIN");
                routeEndpointsList.add(TOY_BIN);


                Gson gsonObj = new Gson();
                String issueGson = gsonObj.toJson(routeEndpointsList);

                mClient.publish(TOPIC, issueGson.getBytes(), 1, false);
            } catch (MqttException e) {
                Log.e(TAG, "Failed to publish because " + e.toString());
            }
        }
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        Log.i(TAG, "connection  failed" + throwable.getMessage());
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

            mClient = new MqttAndroidClient(params[0],"tcp://m2m.eclipse.org:1883","FitClient");//tcp://m2m.eclipse.org:1883


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
