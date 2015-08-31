package org.fraunhofer.fit.almanac.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by devasya on 01.07.2015.
 */
public class IssueStatus {

    private static final String TAG = "IssueStatus";
    public String id;
    public String name;
    public String comment;
    public String picPath ;

    public Status status;
    public Priority priority;
    public Date timeToCompletion;
    public boolean isSubscribed;
    public Date creationDate;
    public boolean isUpdated;
    public Date updationDate;

    public String displayString(){
        return "Status:"+ status +"\nPriority:" + priority+ "\nDue Time:"+timeToCompletion;
    }
    public void updateStatus(IssueEvent issueEvent){

       // priority = issueEvent.priority;
        //priority =  issueEvent.priority;
        //timeToCompletion = picIssueUpdate.timeToCompletion;
        Log.i(TAG, "Updating issue eventType: " + issueEvent.eventType);
        Event event = Event.valueOf(issueEvent.eventType);
        switch (event){
            case CREATED:
                status = Status.NEW;
                break;
            case DELETED:
                status = Status.DELETED;
                break;

            case UPDATED:
                switch (issueEvent.property){
                    case IssueEvent.STATUS:
                        status = Status.valueOf(issueEvent.value);
                        break;
                    case IssueEvent.PRIORITY:
                        priority = Priority.valueOf(issueEvent.value);
                        break;
                    case IssueEvent.TIME2COMPL:
                        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
                        try {
                            timeToCompletion = (df).parse(issueEvent.value);
                        } catch (ParseException e) {
                            Log.e(TAG, "unable to parese date format"+ issueEvent.value);
                        }
                        break;
                }
            break;


        }
        updationDate = new Date();
    }

    public void setPicIssue(PicIssue picIssue, String filePath,boolean subscribe){
        name = picIssue.name;
        comment = picIssue.comment;
        id = picIssue.id;
        picPath = filePath;
        isSubscribed = subscribe;
        status = Status.NEW;
        creationDate= updationDate  = new Date(); //set the date to be current date

    }

    public void setId(String id){
        this.id = id;
    }

    public void setUpdated(boolean updated) {
        this.isUpdated = updated;
    }


}
