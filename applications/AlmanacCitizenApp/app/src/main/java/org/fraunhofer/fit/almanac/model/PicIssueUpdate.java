package org.fraunhofer.fit.almanac.model;

import java.util.Date;

/**
 * Created by devasya on 22.06.2015.
 */

public class PicIssueUpdate {



    //Id of the issue
    public String id;
    public Status status;
    public Priority priority;
    public Date timeToCompletion;

    public String comment;//Textual feedback from the Almanac system employee.

    public String getString(){
        return "Id:"+id +" status:"+ status +" priority:" + priority+ " Timeforcompletion:"+timeToCompletion;
    }
    public String displayString(){
        return "Status:"+ status +"\nPriority:" + priority+ "\nDue Time:"+timeToCompletion;
    }
}
