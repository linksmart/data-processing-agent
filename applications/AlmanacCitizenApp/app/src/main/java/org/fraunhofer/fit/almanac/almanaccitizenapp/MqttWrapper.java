package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.fraunhofer.fit.almanac.model.DuplicateIssue;
import org.fraunhofer.fit.almanac.model.Event;
import org.fraunhofer.fit.almanac.model.IssueEvent;
import org.fraunhofer.fit.almanac.model.IssueStatus;
import org.fraunhofer.fit.almanac.model.Priority;
import org.fraunhofer.fit.almanac.model.Status;
import org.fraunhofer.fit.almanac.protocols.MqttListener;
import org.fraunhofer.fit.almanac.util.UniqueId;

import eu.linksmart.smartcity.issue.TicketEvent;

/**
 * Created by devasya on 20.08.2015.
 */
public class MqttWrapper implements MqttListener.MqttNotificationListener {

    private static final String TAG = "MqttWrapper" ;
    private final Context mContext;
    private final IssueTracker mIssueTracker;
    private final MqttListener mMqttListener;
    private static MqttWrapper mInstance ;

    private  MqttWrapper(Context context) {
        mContext = context;
        mIssueTracker = initializeIssueTracker(context);
        mMqttListener = setupMqtt(context);
        mIssueTracker.subscribeDeletion(new IssueTracker.DeletionListener() {
            @Override
            public void onDelete(String IssueId) {
                mMqttListener.unsubscribeIssue(IssueId);
            }
        });


    }

    protected IssueTracker  initializeIssueTracker(Context context){
        IssueTracker issueTracker = IssueTracker.getInstance();
        issueTracker.setContext(context);
        return issueTracker;
    }


    public static MqttWrapper init(Context context){
        if(mInstance == null){
            mInstance = new MqttWrapper(context);
        }
        return mInstance;
    }

    private MqttListener setupMqtt(Context context) {
        MqttListener mqttListener = MqttListener.getInstance();
        mqttListener.connect(context, UniqueId.getClientId(context), UniqueId.getDeviceId(context));//TODO make a persistent list
        mqttListener.addNotificationListener(this);

        return mqttListener;
    }
    @Override
        public void onIssueUpdate(final TicketEvent issueEvent) {

            //Toast.makeText(getActivity().getApplicationContext(),"Got a message on name"+issueUpdate.name,Toast.LENGTH_SHORT).show();
//                    new AlertDialog.Builder(getApplicationContext())
//                            .setTitle(issueUpdate.name)
//                            .setMessage(issueUpdate.displayString())
//                            .setPositiveButton(R.string.ok_dialog, null)
//                            .show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "onIssueUpdate2:" + issueEvent.getEventType());
                    notifyUser(issueEvent);
                    mIssueTracker.updateIssue(issueEvent);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mImageListFragment.publishDone();
//                        }
//                    });

                    Log.i(TAG, "onIssueUpdate:" + "Done updating");
                }
            }).start();



        }




    public  String getNotificationMessage(TicketEvent issueEvent,IssueStatus issueStatus){

        switch (issueEvent.getEventType()){
            case DELETED:
                return "closed";
            case UPDATED:
                switch (issueEvent.getProperty()){
                    case IssueEvent.STATUS:
                        Status status = Status.valueOf(issueEvent.getValue());
                        return Status.toReadableString(status);
                    case IssueEvent.PRIORITY:
                        Priority priority = Priority.valueOf(issueEvent.getValue());
                        return "Priority changed from "+
                                (issueStatus.priority==null?"unclassified":issueStatus.priority)+" to "+ Priority.readableString(priority);
                    case IssueEvent.TIME2COMPL:
                        return "will be addressed within "+issueEvent.getValue();

                }
                break;


        }
        return null;
    }




    private void notifyUser(TicketEvent issueEvent) {

        IssueStatus issueStatus = mIssueTracker.getIssue(issueEvent.getTicketId());
        if(issueStatus != null) {
            String contentText = getNotificationMessage(issueEvent, issueStatus);
            Log.i(TAG, "notify user:" + contentText);
            if (contentText != null) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(mContext)
                                .setSmallIcon(R.drawable.waste_container)
                                .setContentTitle(issueStatus.name)
                                .setContentText(contentText)
                                .setAutoCancel(true);
                Intent resultIntent = new Intent(mContext, AlmanacCitizen.class);
                PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, resultIntent, 0);

                mBuilder.setContentIntent(pIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(AlmanacCitizen.ID_NEW_UPDATE, mBuilder.build());

            }
        }

    }
}
