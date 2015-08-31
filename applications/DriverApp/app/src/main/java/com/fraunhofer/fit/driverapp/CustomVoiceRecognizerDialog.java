package com.fraunhofer.fit.driverapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import java.util.ArrayList;


public class CustomVoiceRecognizerDialog extends DialogFragment {
    private static final String TAG = "CustomVoiceListener";
    private SpeechRecognizer sr;
    boolean mbUserDecided = false;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onPositive();
        public void onNegative();
        public void onNoResult();
    }

    NoticeDialogListener mListener;

    public CustomVoiceRecognizerDialog(NoticeDialogListener listener){
        mListener = listener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_custom_voice_recognizer, null))  // Add action buttons
                .setPositiveButton(getString(R.string.update_route), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mbUserDecided = true;
                        mListener.onPositive();

                    }
                })
                .setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mbUserDecided = true;
                        mListener.onNegative();
                    }
                });

        sr = SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        sr.setRecognitionListener(new Listener());
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
       intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
            "com.fraunhofer.fit.driverapp");
        sr.startListening(intent);
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.i(TAG, "onOptionsItemSelected");
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class Listener implements RecognitionListener
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
            if(!mbUserDecided) {
                mListener.onNoResult();
                sr.destroy();
                CustomVoiceRecognizerDialog.this.dismiss();
            }
        }
        public void onResults(Bundle results)
        {
            Log.i(TAG, "onResults " + results);
            if(mbUserDecided)
                return;
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            boolean userSaidSomethingICanUnderstand = false;
            for (String key:data)
            {
                Log.i(TAG, "user said " + key);
                if( key.equals("Ok, show me the route") || key.equals("show me the route")|| key.equals("yes")){
                    Log.i(TAG, "Hurray!!User said Yes!!");
                    mListener.onPositive();
                    mbUserDecided = true;
                    break;
                }else if(key.equals("no")){
                    mListener.onNegative();
                    mbUserDecided = true;
                    break;
                }

            }
            if(!mbUserDecided){
                mListener.onNoResult();
            }
            sr.destroy();
            CustomVoiceRecognizerDialog.this.dismiss();
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
