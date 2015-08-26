package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.fraunhofer.fit.almanac.model.PicIssue;


import org.fraunhofer.fit.almanac.protocols.MqttListener;
import org.fraunhofer.fit.almanac.util.BitmapUtils;
import org.fraunhofer.fit.almanac.util.UniqueId;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class CaptureActivity extends Activity{


    public static final String RESULT_FIELD = "result";
    public static final String SUBSCRIBE_SET="subscribe";
    private static final String SAVED_CAPTURED_IMAGE = "SavedImage";
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

                    final CheckBox checkBox = (CheckBox) findViewById(R.id.checkSubscribe);

                    if (checkBox.isChecked()) {//TODO:This is to be done by the issueTracker
                        //mqttListener.subscribeForIssues(mPicissue.origin);
                        String filepath = null;
                        if(null != mPictureBitmap)
                            filepath =  storeImageToFile(mPictureBitmap, UniqueId.generateUUID().substring(1,5));

                        mPicissue.id = response;
                        IssueTracker.getInstance().addNewIssue(mPicissue,checkBox.isChecked(),filepath);
                    }
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
    private ActionMode mActionMode;
    private Uri mImageUri;

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
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
//        final ImageButton cancelBotton = (ImageButton) findViewById(R.id.cancelButton);
//        cancelBotton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onclickcancelBotton(v);
//            }
//        });
        final ImageButton cancelBotton = (ImageButton) findViewById(R.id.cancelButton);
        cancelBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclickcancelBotton(v);
            }
        });
        final ImageView image = (ImageView) findViewById(R.id.capturedImage);
        if (savedInstanceState != null) {
            mPictureBitmap = savedInstanceState.getParcelable(SAVED_CAPTURED_IMAGE);
        }
        if(mPictureBitmap != null){
            image.setImageBitmap(mPictureBitmap);
            image.setOnClickListener(new View.OnClickListener() {
                // Called when the user long-clicks on someView
                public void onClick(View view) {

                    openContextMenu(view);
                }
            });
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }else {
            image.setScaleType(ImageView.ScaleType.CENTER);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startCameraActivity();
                }
            });

        }

        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                closeContextMenu();
                return true;
            }
        });

        registerForContextMenu(image);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_CAPTURED_IMAGE,mPictureBitmap);
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

                picIssue.origin = UniqueId.getDeviceId(getApplicationContext());


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

                final CheckBox checkBox = (CheckBox) findViewById(R.id.checkSubscribe);

                mqttListener.publishIssue(picIssue,mqttPublishResultListener,checkBox.isChecked());
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
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "AlmanacCitizen");
        mImageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE_BY_APP);
    }


    public void onActivityResult(int req, int res, Intent data) throws NullPointerException
    {
        super.onActivityResult(req, res, data);
        if(req == REQUEST_CAPTURE_IMAGE_BY_APP)
        {

            if(res == Activity.RESULT_OK){//data != null && data.getExtras() != null) {

                mPictureBitmap =  BitmapUtils.ShrinkBitmap(getRealPathFromURI( mImageUri), 300, 300);//(Bitmap) data.getExtras().get("data");

                final ImageView image = (ImageView) findViewById(R.id.capturedImage);
                image.setImageBitmap(mPictureBitmap);
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                image.setOnClickListener(new View.OnClickListener() {
                    // Called when the user long-clicks on someView
                    public void onClick(View view) {

                        openContextMenu(view);
                    }
                });
                image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        closeContextMenu();
                        return true;
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


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.captureactivity_imageclick_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_show_pic:
                displayImage();
                return true;
            case R.id.menu_change_pic:
                startCameraActivity();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
