package com.fraunhofer.fit.driverapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by devasya on 02.06.2015.
 */
public class CustomVoiceRecognizer {

    private static final String TAG = "CustomVoiceListener";
    private SpeechRecognizer sr;
    private boolean mStopListening = false;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onPositive();
        public void onNegative();
        public void onNoResult();
    }

    NoticeDialogListener mListener;
    Context mCallercontext ;
    public CustomVoiceRecognizer(NoticeDialogListener listener, Context callercontext){
        mListener = listener;
        mCallercontext = callercontext;

    }

    public  void startListening() {
        Log.i(TAG, "startListening");
        mStopListening = false;
        sr = SpeechRecognizer.createSpeechRecognizer(mCallercontext);
        sr.setRecognitionListener(new VoiceActionListener());
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.fraunhofer.fit.driverapp");
        sr.startListening(intent);
    }

    public  void stopListening(){
        mStopListening = true;
        sr.destroy();
    }
    class VoiceActionListener implements RecognitionListener
    {
        public void onReadyForSpeech(Bundle params)
        {
            Log.i(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.i(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            //do nothing here
            //Log.i(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.i(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.i(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.i(TAG, "error " + error);
            if(!mStopListening) {
                mListener.onNoResult();
                sr.destroy();
            }
        }
        public void onResults(Bundle results)
        {
            Log.i(TAG, "onResults " + results);
            if(mStopListening)
                return;
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            boolean userSaidSomethingICanUnderstand = false;
            for (String key:data)
            {
                Log.i(TAG, "user said " + key);
                if( key.equals("Ok, show me the route") || key.equals("show me the route")|| key.equals("yes")){
                    Log.i(TAG, "Hurray!!User said Yes!!");
                    mListener.onPositive();
                    mStopListening = true;
                    break;
                }else if(key.equals("no")){
                    mListener.onNegative();
                    mStopListening = true;
                    break;
                }

            }
            if(!mStopListening){
                mListener.onNoResult();
            }
            sr.destroy();
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.i(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.i(TAG, "onEvent " + eventType);
        }
    }
}

