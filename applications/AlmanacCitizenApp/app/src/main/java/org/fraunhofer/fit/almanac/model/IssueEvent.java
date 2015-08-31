package org.fraunhofer.fit.almanac.model;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by devasya on 27.07.2015.
 */
public class IssueEvent {
    public static final String STATUS = "status";
    public static final String PRIORITY = "priority";
    public static  final String TIME2COMPL = "time2completion";
    public String ticketId;
    public String eventType;
    public String property;
    public String value;

    public String getString() {
        return "ticketId:" + ticketId + " eventType:"+eventType+ " property:" + property + " value:"+value;
    }


}
