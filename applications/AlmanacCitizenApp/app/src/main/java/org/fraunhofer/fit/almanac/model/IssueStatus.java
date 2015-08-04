package org.fraunhofer.fit.almanac.model;

import java.util.Date;

/**
 * Created by devasya on 01.07.2015.
 */
public class IssueStatus {
    public String id;
    public String name;
    public String comment;
    public String picPath ;

    public PicIssueUpdate.State state;
    public PicIssueUpdate.Priority priority;
    public Date timeToCompletion;
    public boolean isSubscribed;

    public String displayString(){
        return "State:"+state +"\nPriority:" + priority+ "\nDue Time:"+timeToCompletion;
    }
    public void updateStatus(PicIssueUpdate picIssueUpdate){
        priority = picIssueUpdate.priority;
        priority =  picIssueUpdate.priority;
        timeToCompletion = picIssueUpdate.timeToCompletion;
    }

    public void setPicIssue(PicIssue picIssue, String filePath,boolean subscribe){
        name = picIssue.name;
        comment = picIssue.comment;
        id = picIssue.id;
        picPath = filePath;
        isSubscribed = subscribe;
        state = PicIssueUpdate.State.OPEN;
    }

    public void setId(String id){
        this.id = id;
    }
}
