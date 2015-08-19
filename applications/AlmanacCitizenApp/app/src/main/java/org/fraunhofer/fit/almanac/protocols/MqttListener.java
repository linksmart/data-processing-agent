package org.fraunhofer.fit.almanac.protocols;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.fraunhofer.fit.almanac.model.DuplicateIssue;
import org.fraunhofer.fit.almanac.model.PicIssue;
import org.fraunhofer.fit.almanac.model.PicIssueUpdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by devasya on 26.05.2015.
 */
public class MqttListener implements MqttCallback ,IMqttActionListener{

    public static final String UPDATE = "update";
    public static final String DUPLICATE = "duplicate";

    public static interface MqttNotificationListener {
        public void onIssueUpdate(PicIssueUpdate issueUpdate);
        public void onDuplicateIssue(DuplicateIssue duplicateIssue);
    }

    public static interface MqttPublishResultListener{
        public void onPublishSuccess(String response);
        public void  onPublishFailure();
    }
    private static final String TAG = "MqttListener";
    //TODO:name and init_topic are not constants. should be read from strings.xml .. and the server URI too
    private final String PUBLISH_TOPIC = "almanac/citizenapp";

    private static MqttListener mInstance;

    private String mClientId ;
    private MqttAndroidClient mClient ;
    private Context mContext;
    List<MqttNotificationListener> mqttNotificationListenerList = new LinkedList<>();

    List<String> mSubscribeIds = new LinkedList<>();

    Gson mGsonObj = new GsonBuilder().registerTypeAdapter(byte[].class,
            new ByteArrayToBase64TypeAdapter()).disableHtmlEscaping().create();
    private HashMap<IMqttDeliveryToken,MqttPublishResultListener> mPublishListenerMap = new HashMap<>();


    public  static MqttListener getInstance(){

        if(mInstance == null) {
            mInstance = new MqttListener();
        }

        return  mInstance;
    }

//    public static  MqttListener getConnectedInstance(){
//        Log.i(TAG, "getConnectedInstance entry: mInstance:"+ mInstance + "mInstance.mClient"+ mInstance.mClient );
//        if(mInstance != null && mInstance.mClient!= null){
//            return  mInstance;
//        }else{
//            return null;
//        }
//    }

    // Using Android's base64 libraries. This can be replaced with any base64 library.
    private static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
        public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Base64.decode(json.getAsString(), Base64.NO_WRAP);
        }

        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(Base64.encodeToString(src, Base64.DEFAULT));
        }
    }



    public void connect(Context context,String clientID,List<String> subscribeTopics){
        if(mClient == null) {
            mContext = context;
            mClientId = clientID;
            mSubscribeIds.addAll(subscribeTopics);
            new MqttserviceTask().execute(context);
        }
    }

    public  void addNotificationListener(MqttNotificationListener mqttNotificationListener){
        this.mqttNotificationListenerList.add(mqttNotificationListener);
    }
    public boolean publishIssue(final PicIssue picIssue,final MqttPublishResultListener listener){



        new Thread(new Runnable() {
            @Override
            public void run() {

                String issueGson = mGsonObj.toJson(picIssue);

                Log.i(TAG,"Publishing:");
                for(int i=0; i<issueGson.length();i+=500){
                    Log.i(TAG,issueGson.substring(i,i+500>issueGson.length()?issueGson.length():i+500));
                }

                HttpRequester requester = new HttpRequester();
                String response = requester.publishIssue(issueGson);

                if(response == null){
                    listener.onPublishFailure();
                }else{
                    listener.onPublishSuccess(response);
                }
            }
        }).start();
        //IMqttDeliveryToken mqttDeliveryToken= mClient.publish(PUBLISH_TOPIC, issueGson.getBytes(), 1, false);





        return true;
    }

    public void subscribeIssue(String subscribeId){
        try {
            mClient.subscribe("almanac/" + subscribeId + "/issue", 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public  void  unsubscribeIssue(String subscribeId){
        try {
            mClient.unsubscribe("issue/" + subscribeId + "/issue");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void connectionLost(Throwable throwable) {
        Log.e(TAG, "Connection lost");
        mSubscribeIds.clear();
        mClient = null;
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {

        Log.i(TAG, "got message:" + s);
        String issueGson = new String(mqttMessage.getPayload());
        Log.i(TAG,"Json:"+issueGson);
      //  if(s.endsWith(UPDATE)){
            PicIssueUpdate issueUpdate = mGsonObj.fromJson(issueGson,PicIssueUpdate.class);
            for(MqttNotificationListener mqttNotificationListener:mqttNotificationListenerList){
                mqttNotificationListener.onIssueUpdate(issueUpdate);
            }
        //}else if(s.endsWith(DUPLICATE)){
          //  DuplicateIssue duplicateIssue = mGsonObj.fromJson(issueGson,DuplicateIssue.class);

//            for(MqttNotificationListener mqttNotificationListener:mqttNotificationListenerList){
  //              mqttNotificationListener.onDuplicateIssue(duplicateIssue);
    //        }
      //  }


    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.i(TAG, "Publish successfull");

//        MqttPublishResultListener mqttPublishResultListener = mPublishListenerMap.get(iMqttDeliveryToken);
//        mqttPublishResultListener.onPublishSuccess(response);
//        mPublishListenerMap.remove(iMqttDeliveryToken);//we need it no more
        /*if(mIssueToken.equals(iMqttDeliveryToken)) {
            PicIssueUpdate issueUpdate = new PicIssueUpdate();
            issueUpdate.timeToCompletion = new Date();;
            issueUpdate.priority = PicIssueUpdate.Priority.CRITICAL;
            issueUpdate.state = PicIssueUpdate.State.OPEN;
            issueUpdate.name = "SomeTopic";
            issueUpdate.id = "47f445429e523cc992b50e69f418c6b26451150c97cabadca68fa4921bedbba5";
            String jsonString = mGsonObj.toJson(issueUpdate);
            try {
                mClient.publish(mClientId + "/" + issueUpdate.id, jsonString.getBytes(), 1, false);
            } catch (MqttException e) {
                Log.e(TAG, "Failed to publish because " + e.toString());
                mqttNotificationListener.onPublishFailure();
            }
        }*/

    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        if(mClient == null || mClient.isConnected() == false){
            return;
        }
        Log.i(TAG, "connected successfully" );
        mClient.setCallback(this);
        for(String subscribeId: mSubscribeIds){
            subscribeIssue(subscribeId);
        }


    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        Log.i(TAG, "connection failed" + throwable.getMessage());
        mClient = null;
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
        mqttNotificationListenerList.clear();
        mPublishListenerMap.clear();

    }


    private class MqttserviceTask extends AsyncTask<Context,Void,String> {

        @Override
        protected String doInBackground(Context... params) {
            if(mClient == null) {

                Log.i(TAG, "connecting to eclipse");
                mClient = new MqttAndroidClient(params[0], "tcp://m2m.eclipse.org:1883", mClientId);//tcp://m2m.eclipse.org:1883



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

    }
    private void StoretoSdcard(String jsonString){
        File root = Environment.getExternalStorageDirectory();
        FileOutputStream f = null;
        try {
            String folderpath = "/storage/emulated/0/myCiti/";


            File folder = new File(folderpath);
            folder.mkdirs(); //create folders where write files

            File file = new File(folderpath, "citi.json");
            f = new FileOutputStream(file);
            try {
                f.write(jsonString.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

    }
}
