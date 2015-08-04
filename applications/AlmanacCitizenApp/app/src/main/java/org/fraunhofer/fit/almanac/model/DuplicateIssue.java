package org.fraunhofer.fit.almanac.model;

/**
 * Created by devasya on 06.07.2015.
 */
public class DuplicateIssue {
    //The original, already exisiting issue id
    private String issueId;

    //Duplicate issue raised by the user, this will be replaced by issueid in Android UI when user acknowledges.
    private String dupIssueId;

    public DuplicateIssue(String issueId, String dupIssueId){
        this.issueId = issueId;
        this.dupIssueId = dupIssueId;
    }

    public String issueId(){ return issueId; }
    public String dupIssueId(){ return dupIssueId; }
}