package it.ismb.pertlab.pwal.api.events.base;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public abstract class PWALBaseEvent // implements Event<PWALBaseEvent>
{
    /**
     * Event time stamp in UTC format
     */
    private DateTime timeStamp;
    /**
     * Event sender identification
     */
    private String senderId;
    
    public PWALBaseEvent(String timeStamp, String senderId)
    {
        this.timeStamp = new DateTime(timeStamp, DateTimeZone.UTC);
        this.senderId = senderId;
    }
    
    public DateTime getTimeStamp()
    {
        return timeStamp;
    }
    
    public void setTimeStamp(DateTime timeStamp)
    {
        this.timeStamp = timeStamp;
    }
    
    public String getSenderId()
    {
        return senderId;
    }
    
    public void setSenderId(String senderId)
    {
        this.senderId = senderId;
    }
}
