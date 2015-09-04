package com.fraunhofer.fit.driverapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;

/**
 * Created by devasya on 27.05.2015.
 */
public class VoiceRecognizer extends Activity {
    private Context mContext;
    private static final int REQUEST_CODE = 1001;
    public void VoiceRecognizer(Context context){
        mContext = context;

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, REQUEST_CODE);

    }


}
