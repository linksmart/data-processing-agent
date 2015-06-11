package com.fraunhofer.fit.driverapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
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
import com.google.android.gms.maps.model.Marker;
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
   // PolygonOptions mPolygonOptions;
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

   /* RouteEndpoint LOC_A = new RouteEndpoint("LOC_A",50.737207, 7.101218);
    RouteEndpoint LOC_B = new RouteEndpoint("LOC_B",50.748273, 7.199395);
    RouteEndpoint LOC_C = new RouteEndpoint("LOC_C",50.758089,7.172432);
    RouteEndpoint LOC_D = new RouteEndpoint("LOC_D",50.811548, 7.237281);
    RouteEndpoint LOC_E = new RouteEndpoint("LOC_E",50.814422, 7.205180);*/



    RouteEndpoint LOC_A = new RouteEndpoint("LOC_A",45.067104, 7.680549);//45.07277297973633, 7.693530082702637 );

    RouteEndpoint LOC_C1 = new RouteEndpoint("LOC_C1",45.06967544555664, 7.682311058044434 );

    RouteEndpoint LOC_B = new RouteEndpoint("LOC_B",45.070838928222656,7.677274703979492);//45.06967544555664,7.682311058044434 );//45.07394027709961,7.687668323516846

    RouteEndpoint LOC_C = new RouteEndpoint("LOC_C",45.072725, 7.671748 );


    RouteEndpoint LOC_D = new RouteEndpoint("LOC_D", 45.06962585449219,7.665708541870117 );

    RouteEndpoint LOC_E = new RouteEndpoint("LOC_E",  45.06730091,    7.66843825 );//45.06818793,7.70531799);

    RouteEndpoint LOC_F = new RouteEndpoint("LOC_F",45.062345, 7.679798);

    RouteEndpoint LOC_G = new RouteEndpoint("LOC_G",45.061375, 7.693145);



   // RouteEndpoint LOC_G = new RouteEndpoint("LOC_G",45.06748203,7.70223518);

         /*   45.06814859
            7.69576776

            45.06694006
            7.69901877*/

    private static final int REQUEST_CODE = 1001;


    private TextToSpeech ttObj;
    private String mUtteranceId;/*To store the utter for temporarily for the first time*/
    MqttListener mqttListener;
    CustomVoiceRecognizer mCustomVoiceRecognizer;
    List<Polygon> mPolygonList = new LinkedList<>();
    List<Marker> markerList = new LinkedList<>();

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
            removeUpdate();
            tellToDriver(UTTER_NOTHING_UPDATED);

        }

        @Override
        public void onNoResult() {
            stopGettingUserInput();
            tellToDriver(UTTER_YES_NO);
        }
    };
    private int mVisibility = View.GONE;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG_LIFE, "onDestroy called");
        clearMap();
        stopGettingUserInput();
        if(ttObj != null) {
            ttObj.shutdown();  // releases resources used by TextToSpeech engine
        }
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
     * LOC_F user can return to this FragmentActivity after following the prompt and correctly
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
        BitmapDescriptor trashIcon = BitmapDescriptorFactory.fromResource(R.drawable.waste_container);

        MarkerOptions markerOptions = new MarkerOptions()
                .position(routeEndpoint.getLatLng())
                .title(routeEndpoint.getId())
                .icon(trashIcon);
        Marker marker = mMap.addMarker(markerOptions);
        markerList.add(marker);
        mRouteEndpointList.add(routeEndpoint);

    }

    private void removeDustBinMap(RouteEndpoint routeEndpoint){

        for (Marker marker:markerList){
            if(marker.getTitle().equals(routeEndpoint.getId())){
                marker.remove();
                markerList.remove(marker);
                break;
            }
        }

        for (RouteEndpoint routeEndpoint1:mRouteEndpointList){
            if(routeEndpoint1.getId().equals(routeEndpoint.getId())){
                mRouteEndpointList.remove(routeEndpoint1);
                break;
            }
        }
     }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        BitmapDescriptor curLocIcon = BitmapDescriptorFactory.fromResource(R.drawable.waste_truck);

        mOriginalLocation = LOC_A;
        mMap.addMarker(new MarkerOptions()
                .position(mOriginalLocation.getLatLng())
                .title("Your location")
                .icon(curLocIcon).rotation(300));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOriginalLocation.getLatLng(), (float) 15));
        if(getResources().getBoolean(R.bool.testLocally)) {
            addDustBinMap(LOC_C1);
            addDustBinMap(LOC_B);

            addDustBinMap(LOC_C);

            addDustBinMap(LOC_D);
            addDustBinMap(LOC_E);
            addDustBinMap(LOC_F);
            addDustBinMap(LOC_G);

            CreateRoute(mRouteEndpointList, getResources().getColor(R.color.polyline_color), 1, true);
        }

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.ToastOnStartup), Toast.LENGTH_SHORT).show();   // shows a toast with the text spoken
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
        if(mMap != null) {
            mMap.clear();
        }
        mRouteEndpointList.clear();
    }

     private void startMqttListener(){
         final ImageView logoImage = (ImageView) findViewById(R.id.logo);
         logoImage.setClickable(false);
         mqttListener = new MqttListener();
         mqttListener.connect(getApplicationContext(), MapsActivity.this);
     }

    private void stopMqttListener(){
        if(mqttListener != null) {
            mqttListener.disconnect();
        }
    }
     public void CreateRoute(final List<RouteEndpoint> routeEndpointList, final int color, final int zindex, boolean optimize){
         Log.i("DriverApp", "Sending requests");
         if(routeEndpointList.isEmpty()){ /*Nothing to do*/
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
         for(RouteEndpoint routeEndpoint:routeEndpointList){
             String nextElement = routeEndpoint.getLatLngString();
             Log.i("DriverApp",nextElement);
             wayPointList.add(nextElement);

         }

         request.waypoints(wayPointList.toArray(new String[wayPointList.size()]));
        if(optimize)
             request.optimizeWaypoints(true);
         else
             request.optimizeWaypoints(false);

         request.setCallback(new PendingResult.Callback<DirectionsRoute[]>() {
             @Override
             public void onResult(DirectionsRoute[] result) {
                 final PolygonOptions polygonOptions = new PolygonOptions();
                 for (DirectionsRoute directionsRoute : result) {
                     Log.i(TAG, "got the result successfully");

                     Log.i(TAG, directionsRoute.summary);
                     List<com.google.maps.model.LatLng> pathlist = directionsRoute.overviewPolyline.decodePath();
                     for (com.google.maps.model.LatLng path : pathlist) {
                         polygonOptions.add(new LatLng(path.lat, path.lng));
                     }
                 }

                 Resources res = getResources();
                 polygonOptions.strokeColor(res.getColor(R.color.polyline_color));
                 Log.i(TAG, "setting width=" + res.getInteger(R.integer.map_polygon_width));
                 polygonOptions.strokeWidth(res.getInteger(R.integer.map_polygon_width));
                 polygonOptions.strokeColor(color);
                 polygonOptions.zIndex(zindex);

                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {

                         Polygon currentPolygon = mMap.addPolygon(polygonOptions);
                         mPolygonList.add(currentPolygon);
                      /*   List<LatLng> pointList = currentPolygon.getPoints();
                         double distance = 0;
                        LatLng prev = pointList.get(0);
                         for(int i=1;i<pointList.size()){
                            distance += Location.distanceBetween();
                         }*/

                     }
                 });

             }

             @Override
             public void onFailure(Throwable e) {
                 Log.e("DriverApp", "result failed," + e.toString());
             }
         });


     }


    public void addTemporaryPathForConfirmation() {
        //TODO:Should this be synchronized for mPendingRoutesToAdd?
        for(RouteEndpoint routeEndpoint:mPendingRoutesToAdd){
            addDustBinMap(routeEndpoint);
        }
     //   mPendingRoutesToAdd.clear();
        CreateRoute(mRouteEndpointList, getResources().getColor(R.color.polyline_update_route_color), 0, true);
        tellToDriver(UTTER_UPDATE_SUCCESS);
        // getUserConfirmationForUpdate();

    }

    public void updateMapWithNewRoute() {
        //TODO:Should this be synchronized for mPendingRoutesToAdd?
        mPendingRoutesToAdd.clear();
        clearRoute();
        CreateRoute(mRouteEndpointList, getResources().getColor(R.color.polyline_color),1,true);
        tellToDriver(UTTER_UPDATE_SUCCESS);
       // getUserConfirmationForUpdate();

    }

    public void removeUpdate() {
        //TODO:Should this be synchronized for mPendingRoutesToAdd?
        for(RouteEndpoint routeEndpoint:mPendingRoutesToAdd){
            removeDustBinMap(routeEndpoint);
        }
        mPendingRoutesToAdd.clear();
        clearRoute();
        CreateRoute(mRouteEndpointList, getResources().getColor(R.color.polyline_color), 1, true);
        // getUserConfirmationForUpdate();

    }
    private void clearRoute() {
        for (Polygon polygon:mPolygonList){
            polygon.remove();
        }
        mPolygonList.clear();
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
            //customVoiceRecognizer.show(getFragmentManager(),"Say yes or no!!");
            setVisibilityForUserConfirmation(View.VISIBLE);
        }else if(UTTER_UPDATE_SUCCESS.equals(utterId)){
            toSpeak = getString(R.string.message_on_update_route_success);

        }else if(UTTER_UPDATING_PROGRESS.equals(utterId)){
            toSpeak = getString(R.string.message_on_update_route_progress);
        }else if(UTTER_NOTHING_UPDATED.equals(utterId)){
            toSpeak = getString(R.string.message_on_user_reject);
        }else if(UTTER_YES_NO.equals(utterId)){
            toSpeak = getString(R.string.message_please_say_yes_no);
            //customVoiceRecognizer.show(getFragmentManager(),"Say yes or no!!");
            setVisibilityForUserConfirmation(View.VISIBLE);
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
                                if(mVisibility == View.VISIBLE)
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


                   mCustomVoiceRecognizer.startListening();
               }
           });

        }
    }

    private void stopGettingUserInput(){
        if(mCustomVoiceRecognizer != null){
            mCustomVoiceRecognizer.stopListening();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                 setVisibilityForUserConfirmation(View.GONE);

            }
        });

     }

     private void setVisibilityForUserConfirmation(final int visibility) {
         mVisibility = visibility;
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
         if(!mRouteEndpointList.isEmpty()) {
             if (alreadyAdded(routeEndpointsList)) {
                 Log.i(TAG, "already added");
                 return;
             }

             addTemporaryPathForConfirmation();
             tellToDriver(UTTER_ASK_FOR_UPDATE);
         }
     }

    @Override
    public void handleInitNodeList(ArrayList<RouteEndpoint> routeEndpointsList) {
        if(mRouteEndpointList.isEmpty()){
            for(RouteEndpoint routeEndpoint:routeEndpointsList){
                addDustBinMap(routeEndpoint);
            }
            CreateRoute(mRouteEndpointList, getResources().getColor(R.color.polyline_color), 1, true);
            final ImageView logoImage = (ImageView) findViewById(R.id.logo);
            logoImage.setClickable(true);
        }
    }

    private boolean alreadyAdded(ArrayList<RouteEndpoint> routeEndpointsList) {
        boolean bRetVal = true;
        for(RouteEndpoint routeEndpoint:routeEndpointsList){
            boolean alreadyAdded = false;
            for(RouteEndpoint addedNode:mRouteEndpointList){
                if(routeEndpoint.getId().equals(addedNode.getId())){
                    alreadyAdded = true;
                    break;
                }
            }
            if(!alreadyAdded) {
                mPendingRoutesToAdd.add(routeEndpoint);
                bRetVal = false;
            }

        }
        return bRetVal;
    }


}
 /* private final double degreesPerRadian = 180.0 / Math.PI;

    private void DrawArrowHead(GoogleMap mMap, LatLng from, LatLng to){
        // obtain the bearing between the last two points
        double bearing = GetBearing(from, to);

        // round it to a multiple of 3 and cast out 120s
        double adjBearing = Math.round(bearing / 3) * 3;
        while (adjBearing >= 120) {
            adjBearing -= 120;
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // Get the corresponding triangle marker from Google
        URL url;
        Bitmap image = null;

        try {
            url = new URL("http://www.google.com/intl/en_ALL/mapfiles/dir_" + String.valueOf((int)adjBearing) + ".png");
            try {
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (image != null){

            // Anchor is ratio in range [0..1] so value of 0.5 on x and y will center the marker image on the lat/long
            float anchorX = 0.5f;
            float anchorY = 0.5f;

            int offsetX = 0;
            int offsetY = 0;

            // images are 24px x 24px
            // so transformed image will be 48px x 48px

            //315 range -- 22.5 either side of 315
            if (bearing >= 292.5 && bearing < 335.5){
                offsetX = 24;
                offsetY = 24;
            }
            //270 range
            else if (bearing >= 247.5 && bearing < 292.5){
                offsetX = 24;
                offsetY = 12;
            }
            //225 range
            else if (bearing >= 202.5 && bearing < 247.5){
                offsetX = 24;
                offsetY = 0;
            }
            //180 range
            else if (bearing >= 157.5 && bearing < 202.5){
                offsetX = 12;
                offsetY = 0;
            }
            //135 range
            else if (bearing >= 112.5 && bearing < 157.5){
                offsetX = 0;
                offsetY = 0;
            }
            //90 range
            else if (bearing >= 67.5 && bearing < 112.5){
                offsetX = 0;
                offsetY = 12;
            }
            //45 range
            else if (bearing >= 22.5 && bearing < 67.5){
                offsetX = 0;
                offsetY = 24;
            }
            //0 range - 335.5 - 22.5
            else {
                offsetX = 12;
                offsetY = 24;
            }

            Bitmap wideBmp;
            Canvas wideBmpCanvas;
            Rect src, dest;

            // Create larger bitmap 4 times the size of arrow head image
            wideBmp = Bitmap.createBitmap(image.getWidth() * 2, image.getHeight() * 2, image.getConfig());

            wideBmpCanvas = new Canvas(wideBmp);

            src = new Rect(0, 0, image.getWidth(), image.getHeight());
            dest = new Rect(src);
            dest.offset(offsetX, offsetY);

            wideBmpCanvas.drawBitmap(image, src, dest, null);

            mMap.addMarker(new MarkerOptions()
                    .position(to)
                    .icon(BitmapDescriptorFactory.fromBitmap(wideBmp))
                    .anchor(anchorX, anchorY));
        }
    }

    private double GetBearing(LatLng from, LatLng to){
        double lat1 = from.latitude * Math.PI / 180.0;
        double lon1 = from.longitude * Math.PI / 180.0;
        double lat2 = to.latitude * Math.PI / 180.0;
        double lon2 = to.longitude * Math.PI / 180.0;

        // Compute the angle.
        double angle = - Math.atan2( Math.sin( lon1 - lon2 ) * Math.cos( lat2 ), Math.cos( lat1 ) * Math.sin( lat2 ) - Math.sin( lat1 ) * Math.cos( lat2 ) * Math.cos( lon1 - lon2 ) );

        if (angle < 0.0)
            angle += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * degreesPerRadian;

        return angle;
    }*/