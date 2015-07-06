package org.fraunhofer.fit.almanac.almanaccitizenapp;

import android.content.ContentValues;
import android.content.Context;

import org.fraunhofer.fit.almanac.db.IssueStatusDBWrapper;
import org.fraunhofer.fit.almanac.model.IssueStatus;
import org.fraunhofer.fit.almanac.model.PicIssue;
import org.fraunhofer.fit.almanac.model.PicIssueUpdate;

import java.util.Collection;
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
            mIssueMap.put(issueStatus.id,issueStatus);
        }
    }

    public void addNewIssue(PicIssue picIssue,boolean subscribe,String filePath){
        IssueStatus newIssue = new IssueStatus();
        newIssue.setPicIssue(picIssue,filePath,subscribe);

        mIssueMap.put(picIssue.id,newIssue);
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

    public void updateIssue(PicIssueUpdate picIssueUpdate){
       IssueStatus issueStatus =  mIssueMap.get(picIssueUpdate.id);
       issueStatus.updateStatus(picIssueUpdate);
    }

    //When the issue is duplicate, we replace the id with the original issue id
    public void updateIdToOriginalIssue(String id, String OriginalId){
        IssueStatus issueStatus = mIssueMap.get(id);
        issueStatus.setId(OriginalId);
        //TODO update the DB
        mIssueMap.remove(id);
        mIssueMap.put(OriginalId,issueStatus);
    }



}
