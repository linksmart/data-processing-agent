package com.fraunhofer.fit.driverapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by devasya on 26.05.2015.
 */
public class MqttListener implements MqttCallback ,IMqttActionListener{

    private final String TAG = "MqttListener";
    //TODO:topic and init_topic are not constants. should be read from strings.xml .. and the server URI too
    private final String topic = "/almanac/route";
    private final String init_topic = "/almanac/route/initial";
    private MqttAndroidClient mClient ;
    private Context mContext;
    RouteUpdateHandler mRouteUpdateHandler;
    private boolean mUpdateDone;


    public void connect(Context context,RouteUpdateHandler routeUpdateHandler){
        mRouteUpdateHandler = routeUpdateHandler;
        mContext = context;
        new MqttserviceTask().execute(context);


    }

    @Override
    public void connectionLost(Throwable throwable) {
        Log.e(TAG, "Connection lost");
        mClient = null;
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        String issueGson = new String(mqttMessage.getPayload());
        Log.i(TAG, "got message:" +s +" "+ issueGson );
        Gson gsonObj = new Gson();
        Type listType = new TypeToken<List<RouteEndpoint>>() {
        }.getType();
        ArrayList<RouteEndpoint> routeEndpointsList = gsonObj.fromJson(issueGson,listType);
        Log.i(TAG, "object:" + routeEndpointsList.size());
        for(RouteEndpoint routeEndpoint:routeEndpointsList){
            Log.i(TAG,routeEndpoint.getString());
        }
        if(s.equals(topic)) {
            mRouteUpdateHandler.handleUpdateNodeList(routeEndpointsList);
//            if(mContext.getResources().getBoolean(R.bool.testLocally) && !mUpdateDone) {
//
//                ArrayList<RouteEndpoint> routeEndpointsListToPub = new ArrayList<RouteEndpoint>();
//                RouteEndpoint TOY_BIN = new RouteEndpoint();
//                TOY_BIN.geoLocation(45.061148, 7.700376);//45.064537048339844, 7.696494102478027);//45.06723305,    7.70074879);
//                TOY_BIN.setId("TOY_BIN");
//                routeEndpointsListToPub.add(TOY_BIN);
//
//
//                String issueGsonToPub = gsonObj.toJson(routeEndpointsListToPub);
//                Log.i(TAG, "publishing toy bin2");
//                mClient.publish(topic, issueGsonToPub.getBytes(), 1, false);
//                mUpdateDone = true;
//            }
        }else if(s.equals(init_topic)){
            mRouteUpdateHandler.handleInitNodeList(routeEndpointsList);
            Log.i(TAG, "got init "+ mContext.getResources().getBoolean(R.bool.testLocally));
            if(mContext.getResources().getBoolean(R.bool.testLocally)) {

                ArrayList<RouteEndpoint> routeEndpointsListToPub = new ArrayList<RouteEndpoint>();

                RouteEndpoint TOY_BIN = new RouteEndpoint("LOC_G",45.061375, 7.693145);

                routeEndpointsListToPub.add(TOY_BIN);


                String issueGsonToPub = gsonObj.toJson(routeEndpointsListToPub);
                Log.i(TAG, "publishing toy bin");
                mClient.publish(topic, issueGsonToPub.getBytes(), 1, false);
                mUpdateDone = false;
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.i(TAG, "Publish successfull");
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        if(mClient == null || mClient.isConnected() == false){
            return;
        }
        mClient.setCallback(this);
        try {
            mClient.subscribe(topic,1);
            mClient.subscribe(init_topic,1);
        } catch (MqttException e) {
            Log.e(TAG, "Failed to suscribe because " + e.toString());
        }
        if(mContext.getResources().getBoolean(R.bool.testLocally)) {
            try {
                // String myMessage = "Hi hello how are you";
                ArrayList<RouteEndpoint> routeEndpointsList = new ArrayList<RouteEndpoint>();


                RouteEndpoint LOC_C1 = new RouteEndpoint("LOC_C1",45.06967544555664, 7.682311058044434 );
                routeEndpointsList.add(LOC_C1);
                RouteEndpoint LOC_B = new RouteEndpoint("LOC_B",45.070838928222656,7.677274703979492);//45.06967544555664,7.682311058044434 );//45.07394027709961,7.687668323516846
                routeEndpointsList.add(LOC_B);
                RouteEndpoint LOC_C = new RouteEndpoint("LOC_C",45.072725, 7.671748 );

                routeEndpointsList.add(LOC_C);
                RouteEndpoint LOC_D = new RouteEndpoint("LOC_D", 45.06962585449219,7.665708541870117 );
                routeEndpointsList.add(LOC_D);
                RouteEndpoint LOC_E = new RouteEndpoint("LOC_E",  45.06730091, 7.66843825 );//45.06818793,7.70531799);
                routeEndpointsList.add(LOC_E);
                RouteEndpoint LOC_F = new RouteEndpoint("LOC_F",45.062345, 7.679798);
                routeEndpointsList.add(LOC_F);
//                RouteEndpoint LOC_G = new RouteEndpoint("LOC_G",45.061375, 7.693145);
//                routeEndpointsList.add(LOC_G);




                Gson gsonObj = new Gson();
                String issueGson = gsonObj.toJson(routeEndpointsList);

                mClient.publish(init_topic, issueGson.getBytes(), 1, false);
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
            if(mClient != null) {
                if(mClient.isConnected()){
                    mClient.disconnect();
                }
            }
        } catch (MqttException e) {
            Log.e(TAG, "Failed to disconnect because " + e.toString());
        }
        mClient = null;

    }

    public void  publishforTest(){
        Gson gsonObj = new Gson();
        if(mContext.getResources().getBoolean(R.bool.testLocally) && !mUpdateDone) {

            ArrayList<RouteEndpoint> routeEndpointsListToPub = new ArrayList<RouteEndpoint>();
            RouteEndpoint TOY_BIN = new RouteEndpoint();
            TOY_BIN.geoLocation(45.049060, 7.667581);//(45.061148, 7.700376);
            TOY_BIN.setId("TOY_BIN");
            routeEndpointsListToPub.add(TOY_BIN);


            String issueGsonToPub = gsonObj.toJson(routeEndpointsListToPub);
            Log.i(TAG, "publishing toy bin2");
            try {
                mClient.publish(topic, issueGsonToPub.getBytes(), 1, false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mUpdateDone = true;
        }
    }

    private class MqttserviceTask extends AsyncTask<Context,Void,String> {

        @Override
        protected String doInBackground(Context... params) {
            if(mClient == null) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                boolean eclispe_enabled = sharedPref.getBoolean("eclipse_enabled", false);

                if(eclispe_enabled) {
                    Log.i(TAG, "connecting to eclipse");
                    mClient = new MqttAndroidClient(params[0], "tcp://m2m.eclipse.org:1883", "FitClient");//tcp://m2m.eclipse.org:1883
                }else {
                    Log.i(TAG, "connecting to almanac");
                    mClient = new MqttAndroidClient(params[0], "tcp://almanac.fit.fraunhofer.de:1883", "FitClient");//tcp://m2m.eclipse.org:1883
                }


                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                try {
                    mClient.connect(connOpts, null, MqttListener.this);
                } catch (MqttException e) {
                    Log.e(TAG, "Failed to connect because " + e.toString());

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //do nothing
        }

    }}
