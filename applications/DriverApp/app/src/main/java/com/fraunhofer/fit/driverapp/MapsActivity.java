package com.fraunhofer.fit.driverapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsRoute;

import org.fit.fraunhofer.almanac.RouteEndpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements TextToSpeech.OnInitListener, RouteUpdateHandler {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    PolygonOptions mPolygonOptions;
    private List<RouteEndpoint> mRouteEndpointList = new LinkedList<RouteEndpoint>();
    private List<RouteEndpoint> mPendingRoutesToAdd = new LinkedList<RouteEndpoint>();
    private RouteEndpoint mOriginalLocation;

    static final String TAG = "DriverApp";
    static final String TAG_TTS = "DriverApp/TTS";
    static final String TAG_LIFE = "DriverApp/Life";

    static final String UTTER_ASK_FOR_UPDATE = "AskForUpdate";
    static final String UTTER_UPDATE_SUCCESS = "UpdateSuccess";
    static final String UTTER_UPDATING_PROGRESS = "UpdateProgress";
    static final String UTTER_NOTHING_UPDATED = "Updatenothing";
    static final String UTTER_YES_NO = "PleaseSayYesNo";

    RouteEndpoint BETHOVEN_HAUS = new RouteEndpoint("BETHOVEN_HAUS",50.737207, 7.101218);
    RouteEndpoint SANKT_AUGUSTIN = new RouteEndpoint("SANKT_AUGUSTIN",50.748273, 7.199395);
    RouteEndpoint HANGELAR = new RouteEndpoint("HANGELAR",50.758089,7.172432);
    RouteEndpoint ZEITHSTRASSE = new RouteEndpoint("ZEITHSTRASSE",50.811548, 7.237281);
    RouteEndpoint AULGASSE = new RouteEndpoint("AULGASSE",50.814422, 7.205180);

    private static final int REQUEST_CODE = 1001;


    private TextToSpeech ttObj;
    private String mUtteranceId;/*To store the utter for temporarily for the first time*/
    MqttListener mqttListener;
    CustomVoiceRecognizer mCustomVoiceRecognizer;
    CustomVoiceRecognizer.NoticeDialogListener mVoiceListener = new CustomVoiceRecognizer.NoticeDialogListener() {
        @Override
        public void onPositive() {
            Log.i(TAG, "onPositive");
            stopGettingUserInput();
            tellToDriver(UTTER_UPDATING_PROGRESS);
            updateMapWithNewRoute();
        }

        @Override
        public void onNegative() {
            Log.i(TAG, "onNegative");
            stopGettingUserInput();
            tellToDriver(UTTER_NOTHING_UPDATED);

        }

        @Override
        public void onNoResult() {
            tellToDriver(UTTER_YES_NO);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(TAG_LIFE, "onCreate called");
        if(isConnectedtoNetwork()) {
            setUpMapIfNeeded();
            final Button updateButton = (Button) findViewById(R.id.updateButton);
            updateButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mVoiceListener.onPositive();
                }
            });

            final Button cancelButton = (Button) findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mVoiceListener.onNegative();
                }
            });

            final ImageView logoImage = (ImageView) findViewById(R.id.logo);
            logoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restartDemo();
                }
            });

        }else{
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

            dlgAlert.setMessage(getString(R.string.message_on_no_nw_connection));
            dlgAlert.setTitle(getString(R.string.title_on_no_nw_connection));
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dlgAlert.create().show();
        }
    }

    @Override
    protected void onResume() {
        Log.i(TAG_LIFE, "onResume called");
        super.onResume();
        /*if(isConnectedtoNetwork()) {
            setUpMapIfNeeded();
        }else{
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

            dlgAlert.setMessage(getString(R.string.message_on_no_nw_connection));
            dlgAlert.setTitle(getString(R.string.title_on_no_nw_connection));
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dlgAlert.create().show();

        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG_LIFE, "onDestroy called");
        clearMap();
        stopGettingUserInput();
        ttObj.shutdown();  // releases resources used by TextToSpeech engine
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void addDustBinMap(RouteEndpoint routeEndpoint){
        BitmapDescriptor trashIcon = BitmapDescriptorFactory.fromResource(R.drawable.bin_container_32);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(routeEndpoint.getLatLng())
                .title(routeEndpoint.getId())
                .icon(trashIcon);
        mMap.addMarker(markerOptions);
        mRouteEndpointList.add(routeEndpoint);

    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        BitmapDescriptor curLocIcon = BitmapDescriptorFactory.fromResource(R.drawable.favicon);

        mOriginalLocation = BETHOVEN_HAUS;
        mMap.addMarker(new MarkerOptions()
                .position(mOriginalLocation.getLatLng())
                .title("Bethoven haus")
                .icon(curLocIcon));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOriginalLocation.getLatLng(), 12));
        addDustBinMap(HANGELAR);
        addDustBinMap(SANKT_AUGUSTIN);
        addDustBinMap(ZEITHSTRASSE);
        addDustBinMap(AULGASSE);

        CreateRoute();
      /*  PolygonOptions polygonOptions = new PolygonOptions();

        for(MarkerOptions markerOptions:mMarkerOptionsList ){
                polygonOptions.add(markerOptions.getPosition());
        }
        Polygon polygon =  mMap.addPolygon(polygonOptions);
        */
        startMqttListener();
    }

    private void restartDemo(){
        clearMap();
        stopGettingUserInput();
        setUpMap();

    }
    private void clearMap(){
        stopMqttListener();
        mMap.clear();
        mRouteEndpointList.clear();

    }

     private void startMqttListener(){
     //    new Thread(new Runnable() {
     //        public void run() {
                 mqttListener = new MqttListener();
                 mqttListener.connect(getApplicationContext(), MapsActivity.this);
     //        }
     //    });

     }

    private void stopMqttListener(){
        mqttListener.disconnect();
    }
     public void CreateRoute(){
         Log.i("DriverApp", "Sending requests");
         if(mRouteEndpointList.isEmpty()){ /*Nothing to do*/
             Log.i("DriverApp","Empty marker list");
             return;
         }
         GeoApiContext context = new GeoApiContext().setApiKey(getString(R.string.google_maps_server_key));
         DirectionsApiRequest request = DirectionsApi.newRequest(context);

         /*Insert way points , origin and destination*/
         String startpoint = mOriginalLocation.getLatLngString();
         request.origin(startpoint).
                 destination(startpoint);




         List<String> wayPointList = new LinkedList<String>();
         for(RouteEndpoint routeEndpoint:mRouteEndpointList){
             String nextElement = routeEndpoint.getLatLngString();
             Log.i("DriverApp",nextElement);
             wayPointList.add(nextElement);

         }

         request.waypoints(wayPointList.toArray(new String[wayPointList.size()]));
         request.optimizeWaypoints(true);

         request.setCallback(new PendingResult.Callback<DirectionsRoute[]>() {
             @Override
             public void onResult(DirectionsRoute[] result) {
                 mPolygonOptions = new PolygonOptions();
                 for (DirectionsRoute directionsRoute : result) {
                     Log.i(TAG, "got the result successfully");

                     Log.i(TAG, directionsRoute.summary);
                     List<com.google.maps.model.LatLng> pathlist = directionsRoute.overviewPolyline.decodePath();
                     for (com.google.maps.model.LatLng path : pathlist) {
                         mPolygonOptions.add(new LatLng(path.lat, path.lng));
                     }
                 }

                 Resources res = getResources();
                 mPolygonOptions.strokeColor(res.getColor(R.color.polyline_color));
                 Log.i(TAG, "setting width=" + res.getInteger(R.integer.map_polygon_width));
                 mPolygonOptions.strokeWidth(res.getInteger(R.integer.map_polygon_width));
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         Polygon polygon = mMap.addPolygon(mPolygonOptions);
                     }
                 });

             }

             @Override
             public void onFailure(Throwable e) {
                 Log.e("DriverApp", "result failed," + e.toString());
             }
         });


     }


    public void updateMapWithNewRoute() {
        //TODO:Should this be synchronized for mPendingRoutesToAdd?
        for(RouteEndpoint routeEndpoint:mPendingRoutesToAdd){
            addDustBinMap(routeEndpoint);
        }
        mPendingRoutesToAdd.clear();
        CreateRoute();
        tellToDriver(UTTER_UPDATE_SUCCESS);
       // getUserConfirmationForUpdate();

    }

    public void tellToDriver(String utterId){
        mUtteranceId =utterId;
        if(ttObj == null) {
            ttObj = new TextToSpeech(getApplicationContext(), MapsActivity.this);
        }else{
            utterToDriver(utterId);
        }
    }

    private void utterToDriver(String utterId){

        HashMap<String, String> param = new HashMap<String, String>();


        String toSpeak = null;

        if(UTTER_ASK_FOR_UPDATE.equals(utterId)){
            toSpeak = getString(R.string.message_to_driver_on_new_root);
            /*disable restarting in this stage because the UI may go to unwanted state */
            final ImageView logoImage = (ImageView) findViewById(R.id.logo);
            logoImage.setClickable(false);
        }else if(UTTER_UPDATE_SUCCESS.equals(utterId)){
            toSpeak = getString(R.string.message_on_update_route_success);

        }else if(UTTER_UPDATING_PROGRESS.equals(utterId)){
            toSpeak = getString(R.string.message_on_update_route_progress);
        }else if(UTTER_NOTHING_UPDATED.equals(utterId)){
            toSpeak = getString(R.string.message_on_user_reject);
        }else if(UTTER_YES_NO.equals(utterId)){
            toSpeak = getString(R.string.message_please_say_yes_no);
        }
        if(toSpeak != null ) {
            param.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mUtteranceId);

            Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();   // shows a toast with the text spoken
            //ttObj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, "ToDriver");
            ttObj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, param);//TODO enable above code for API level 21
        }
    }
    @Override
    public void onInit(int status) {

        if(status == TextToSpeech.SUCCESS){
            int result = ttObj.setLanguage(Locale.UK);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG_TTS, "This Language is not supported");
            } else {

                    ttObj.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            // when the route change message has been spoken, it's time for the driver to answer

                            if (utteranceId.equals(UTTER_ASK_FOR_UPDATE) || utteranceId.equals(UTTER_YES_NO)) {
                                getUserConfirmationForUpdate();
                                final ImageView logoImage = (ImageView) findViewById(R.id.logo);
                                logoImage.setClickable(true);
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {

                        }
                    });

                utterToDriver(mUtteranceId);
            }
        }
    }



    public void getUserConfirmationForUpdate(){

       if(false){
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

            startActivityForResult(intent, REQUEST_CODE);
       }else{
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   if (null == mCustomVoiceRecognizer)
                       mCustomVoiceRecognizer = new CustomVoiceRecognizer(mVoiceListener, getApplicationContext());

                   //customVoiceRecognizer.show(getFragmentManager(),"Say yes or no!!");
                   setVisibilityForUserConfirmation(View.VISIBLE);
                   mCustomVoiceRecognizer.startListening();
               }
           });

        }
    }

    private void stopGettingUserInput(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 setVisibilityForUserConfirmation(View.GONE);
                 mCustomVoiceRecognizer.stopListening();
            }
        });

     }

     private void setVisibilityForUserConfirmation(final int visibility) {

         Button updateButton = (Button) findViewById(R.id.updateButton);
         Button cancelButton = (Button) findViewById(R.id.cancelButton);
         ImageView diversionView = (ImageView) findViewById(R.id.diversion);

         updateButton.setVisibility(visibility);
         cancelButton.setVisibility(visibility);
         diversionView.setVisibility(visibility);

     }

     public boolean isConnected() {
         ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo net = connectMgr.getActiveNetworkInfo();
         if (net != null && net.isAvailable() && net.isConnected()) {
             return true;
         } else {
             return false;
         }
     }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            // get recognition results in descending order of speech recognizer confidence
            ArrayList<String> spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            boolean userSaidSomethingICanUnderstand = false;
            for (String key : spokenText) {
                if( key.equals("Ok, show me the route") || key.equals("show me the route")|| key.equals("yes")){
                    Log.i(TAG_TTS, "Hurray!!User said Yes!!");
                    tellToDriver(UTTER_UPDATING_PROGRESS);
                    updateMapWithNewRoute();

                    userSaidSomethingICanUnderstand = true;
                    break;
                }else if(key.equals("no")){
                    Log.i(TAG_TTS, "User said no");
                    userSaidSomethingICanUnderstand = true;
                    break;
                }else{
                    Log.i(TAG_TTS, "User said:"+key);
                }
            }

            if(!userSaidSomethingICanUnderstand){
                tellToDriver(UTTER_ASK_FOR_UPDATE);
            }

        }
    }*/

     private boolean isConnectedtoNetwork() {
         ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo net = connectMgr.getActiveNetworkInfo();
         if (net != null && net.isAvailable() && net.isConnected()) {
             return true;
         } else {
             return false;
         }
     }

     @Override
     public void handleUpdateNodeList(ArrayList<RouteEndpoint> routeEndpointsList) {
         mPendingRoutesToAdd.addAll(routeEndpointsList);
         tellToDriver(UTTER_ASK_FOR_UPDATE);
     }


 }
