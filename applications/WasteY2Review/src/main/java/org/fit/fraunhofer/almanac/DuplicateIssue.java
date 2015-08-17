package org.fit.fraunhofer.almanac;

/**
 * Created by Werner-Kytölä on 02.07.2015.
 */
public class DuplicateIssue {
    private String issueId;
    private String dupIssueId;

    public DuplicateIssue(String issueId, String dupIssueId){
        this.issueId = issueId;
        this.dupIssueId = dupIssueId;
    }

    public String issueId(){ return issueId; }
    public String dupIssueId(){ return dupIssueId; }
}
