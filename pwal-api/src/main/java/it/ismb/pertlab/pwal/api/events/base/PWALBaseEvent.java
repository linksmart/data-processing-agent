package it.ismb.pertlab.pwal.api.events.base;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

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
    /**
     * Device who generates the event
     */
    private Device sender;
    
    public PWALBaseEvent(String timeStamp, String senderId, Device sender)
    {
        this.timeStamp = new DateTime(timeStamp, DateTimeZone.UTC);
        this.senderId = senderId;
        this.sender = sender;
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

    public Device getSender()
    {
        return sender;
    }

    public void setSender(Device sender)
    {
        this.sender = sender;
    }
}
