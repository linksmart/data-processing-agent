package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.fraunhofer.fit.almanac.model.PicIssue;


import org.fraunhofer.fit.almanac.protocols.MqttListener;
import org.fraunhofer.fit.almanac.util.UniqueId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class CaptureActivity extends Activity{


    public static final String RESULT_FIELD = "result";
    public static final String SUBSCRIBE_SET="subscribe";
    private final int REQUEST_CAPTURE_IMAGE_BY_APP = 0x10;//just a random number.
    private final String TAG = "CaptureFragment";
    private MqttListener mqttListener;


    MqttListener.MqttPublishResultListener mqttPublishResultListener = new MqttListener.MqttPublishResultListener() {
        @Override
        public void onPublishSuccess(final String response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ImageButton uploadButton = (ImageButton) findViewById(R.id.uploadButton);
                    uploadButton.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Uploaded successfully", Toast.LENGTH_LONG).show();
                    String filepath = null;
                    if(null != mPictureBitmap)
                        filepath = storeImageToFile(mPictureBitmap, UniqueId.generateUUID().substring(1,5));

                    final CheckBox checkBox = (CheckBox) findViewById(R.id.checkSubscribe);
                    mPicissue.id = response;
                    if (checkBox.isChecked()) {//TODO:This is to be done by the issueTracker
                        mqttListener.subscribeIssue(mPicissue.origin);
                    }

                    IssueTracker.getInstance().addNewIssue(mPicissue,checkBox.isChecked(),filepath);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(RESULT_FIELD, "publish_success");

                    setResult(RESULT_OK, returnIntent);
                    finish();


                }
            });
        }

        @Override
        public void onPublishFailure() {
            Log.i(TAG, "Publish failed");

            Intent returnIntent = new Intent();
            returnIntent.putExtra(RESULT_FIELD, "publish_failed");
            setResult(RESULT_OK, returnIntent);
            finish();

        }

    }   ;
    private PicIssue mPicissue;
    private Bitmap mPictureBitmap;

    //returns complete path of the file after storage. null on failure
    private String storeImageToFile(Bitmap bmp, String fileName) {
        String folderpath = getFilesDir()+ getString(R.string.folderPath);


        File folder = new File(folderpath);
        folder.mkdirs(); //create folders where write files

        File file = new File(folderpath, fileName + ".jpeg");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            Log.i(TAG,"Storing to the path:"+folderpath+fileName+".jpeg" );
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            return folderpath+fileName+".jpeg";
        } catch (FileNotFoundException e) {
            Log.i(TAG,"Storing to the file failed, error:"+e.getMessage());
            return null;
        }

    }

    @Override
    public void onCreate(
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_capture);

        mqttListener = MqttListener.getInstance();

        final ImageButton uploadButton = (ImageButton) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickUploadButton(v);
            }
        });
        final ImageButton cancelBotton = (ImageButton) findViewById(R.id.cancelButton);
        cancelBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickcancelBotton(v);
            }
        });
        final ImageButton startCameraBotton = (ImageButton) findViewById(R.id.captureImage);
        startCameraBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });


        mPictureBitmap = null;

    }



    private Location getLastKnownLocation() {
        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    private void onclickUploadButton(View v) {
        {

            Log.i(TAG,"Im onclick");
// Or use LocationManager.GPS_PROVIDER
            Location lastKnownLocation = getLastKnownLocation();
            if (lastKnownLocation != null) {
                Log.i(TAG,"locationManager is not null");


                PicIssue picIssue = new PicIssue();


                picIssue.latitude = lastKnownLocation.getLatitude();
                picIssue.longitude = lastKnownLocation.getLongitude();

                picIssue.origin = ((TelephonyManager) getBaseContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE)).getDeviceId();


                Log.i(TAG, "publishing " + picIssue.getString());
                if(null != mPictureBitmap) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    mPictureBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    picIssue.pic = stream.toByteArray();
                    picIssue.contentType = "image/jpeg";
                }
                EditText nameOfIssue = (EditText) findViewById(R.id.issueName);
                picIssue.name = nameOfIssue.getText().toString();

                EditText commentBox = (EditText) findViewById(R.id.commentBox);
                picIssue.comment = commentBox.getText().toString();

                v.setEnabled(false);

                mPicissue = picIssue;
                mqttListener.publishIssue(picIssue,mqttPublishResultListener);


            }
            else{
                Toast.makeText(getApplicationContext(), getString(R.string.locationFail), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void onclickcancelBotton(View v) {
        Log.i(TAG,"Cancel pressed. Finishing the activity");
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();

    }

    public void startCameraActivity() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE_BY_APP);
    }


    public void onActivityResult(int req, int res, Intent data) throws NullPointerException
    {
        super.onActivityResult(req, res, data);
        if(req == REQUEST_CAPTURE_IMAGE_BY_APP)
        {

            if(data != null && data.getExtras() != null) {
                mPictureBitmap = (Bitmap) data.getExtras().get("data");
                final ImageView image = (ImageView) findViewById(R.id.capturedImage);
                image.setImageBitmap(mPictureBitmap);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayImage();
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), "No picture taken", Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent returnIntent = new Intent();
                        //setResult(RESULT_CANCELED, returnIntent);
                       // finish();
                    }
                });
            }
        }

    }//onActivityResult

    private void displayImage( ) {
        Intent intent = new Intent(this,DisplayImageActivity.class);
        intent.putExtra(DisplayImageActivity.IMAGE_BITMAP,mPictureBitmap);
        startActivity(intent);
    }


}
