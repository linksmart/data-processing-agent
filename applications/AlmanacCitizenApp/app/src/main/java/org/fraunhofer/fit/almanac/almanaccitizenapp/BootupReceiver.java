package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.fraunhofer.fit.almanac.protocols.MqttListener;
import org.fraunhofer.fit.almanac.util.UniqueId;

public class BootupReceiver extends BroadcastReceiver {
    private static final String TAG = "BootupReceiver";
    private IssueTracker mIssueTracker;

    public BootupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Received boot event");
        MqttWrapper.init(context);

    }




}
