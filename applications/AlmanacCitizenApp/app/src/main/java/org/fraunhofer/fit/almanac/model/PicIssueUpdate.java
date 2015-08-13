package org.fraunhofer.fit.almanac.model;

import java.util.Date;

/**
 * Created by devasya on 22.06.2015.
 */

public class PicIssueUpdate {
    public enum State {
        OPEN, SCHEDULED, DONE, CLOSED;
        public static State fromInteger(int x) {
            switch(x) {
                case 0:
                    return OPEN;
                case 1:
                    return SCHEDULED;
                case 2:
                    return DONE;
                case 3:
                    return CLOSED;
            }
            return null;
        }
    }

    public enum Priority {
        MINOR, MAJOR, CRITICAL        ;
        public static Priority fromInteger(int x) {
            switch(x) {
                case 0:
                    return MINOR;
                case 1:
                    return MAJOR;
                case 2:
                    return CRITICAL;
            }
            return null;
        }
    }

    //Id of the issue
    public String id;


    public String topic;//Need to know why this was added

    public State state;
    public Priority priority;
    public Date timeToCompletion;

    public String comment;//Textual feedback from the Almanac system employee.

    public String getString(){
        return "Id:"+id+" name:"+topic+" state:"+state +" priority:" + priority+ " Timeforcompletion:"+timeToCompletion;
    }
    public String displayString(){
        return "State:"+state +"\nPriority:" + priority+ "\nDue Time:"+timeToCompletion;
    }
}
