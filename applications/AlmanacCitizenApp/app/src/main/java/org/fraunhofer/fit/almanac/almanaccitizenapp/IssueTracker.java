package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.ContentValues;
import android.content.Context;

import org.fraunhofer.fit.almanac.db.IssueStatusDBWrapper;
import org.fraunhofer.fit.almanac.model.IssueEvent;
import org.fraunhofer.fit.almanac.model.IssueStatus;
import org.fraunhofer.fit.almanac.model.PicIssue;
import org.fraunhofer.fit.almanac.model.PicIssueUpdate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by devasya on 01.07.2015.
 */
public class IssueTracker {
    private static IssueTracker mInstance;

    private IssueStatusDBWrapper mIssueStatusDBWrapper ;

    private HashMap<String,IssueStatus> mIssueMap = new HashMap<>();

    public void resetUpdated(String id) {
        IssueStatus issueStatus =  mIssueMap.get(id);
        issueStatus.setUpdated(false);
        mIssueStatusDBWrapper.updateIssue(issueStatus.id, issueStatus);
        //do not notify here
    }


    public static interface ChangeListener{
        public void onChange();
    }

    private List<ChangeListener> mChangeListeners = new LinkedList<>();

    public void subscribeChange(ChangeListener changeListener){
        mChangeListeners.add(changeListener);
    }

    public void unSubscribeChange(ChangeListener changeListener){
        mChangeListeners.remove(changeListener);
    }

    private void notifyChange(){
        for(ChangeListener changeListener:mChangeListeners){
            changeListener.onChange();
        }
    }
    public  static IssueTracker getInstance(){
        if(mInstance == null){
            mInstance = new IssueTracker();

        }
        return mInstance;
    }

    //TODO: use a better JAVA pattern for this scenario
    public void setContext(Context context){
        mIssueStatusDBWrapper = new IssueStatusDBWrapper(context);
        List<IssueStatus> issueStatusList = mIssueStatusDBWrapper.getAllIssues();

        for (IssueStatus issueStatus:issueStatusList){
            mIssueMap.put(issueStatus.id, issueStatus);
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -1);
            Date yesterday = cal.getTime();
            if(issueStatus.updationDate.before(yesterday)){
                resetUpdated(issueStatus.id);
            }
        }
    }

    public void addNewIssue(PicIssue picIssue,boolean subscribe,String filePath){
        IssueStatus newIssue = new IssueStatus();
        newIssue.setPicIssue(picIssue,filePath,subscribe);

        mIssueMap.put(picIssue.id, newIssue);
        mIssueStatusDBWrapper.addIssue(newIssue);
        notifyChange();
    }

    public  void deleteIssue(String id){
        mIssueStatusDBWrapper.deleteIssue(id);
        mIssueMap.remove(id);
        notifyChange();
    }
    public Collection<IssueStatus> getAllIssues(){
        return mIssueMap.values();
    }


    public Collection<IssueStatus> getSubScribedIssues(){
        Collection<IssueStatus> issueList = mIssueMap.values();
        Collection<IssueStatus> subscribedIssues = new LinkedList<>();
        for(IssueStatus issueStatus:issueList){
            if(issueStatus.isSubscribed==true){
                subscribedIssues.add(issueStatus);
            }
        }
        return subscribedIssues;
    }

    public IssueStatus getIssue(String id){
        return  mIssueMap.get(id);
    }
    public void updateIssue(IssueEvent issueEvent){
        IssueStatus issueStatus =  mIssueMap.get(issueEvent.ticketId);
        issueStatus.updateStatus(issueEvent);
        issueStatus.setUpdated(true);
        mIssueStatusDBWrapper.updateIssue(issueStatus.id, issueStatus);
        notifyChange();

    }

    //When the issue is duplicate, we replace the id with the original issue id
    public void updateIdToOriginalIssue(String id, String OriginalId){
        IssueStatus issueStatus = mIssueMap.get(id);
        issueStatus.setId(OriginalId);
        //TODO update the DB
        mIssueMap.remove(id);
        mIssueMap.put(OriginalId,issueStatus);
        mIssueStatusDBWrapper.updateIssue(id,issueStatus);
        notifyChange();

    }



}
