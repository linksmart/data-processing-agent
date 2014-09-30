package it.ismb.pertlab.pwal.api.events.base;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;

import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class PWALNewDataAvailableEvent extends PWALBaseEvent
{

    private HashMap<String, Object> values;
    private Device device;
    private DateTime expirationTime;

    public PWALNewDataAvailableEvent(String timeStamp, String senderId,
            String expirationTime, HashMap<String, Object> values, Device device)
    {
        super(timeStamp, senderId, device);
        
        this.expirationTime = new DateTime(expirationTime, DateTimeZone.UTC);
        this.values = values;
    }

    public HashMap<String, Object> getValues()
    {
        return values;
    }

    public Device getDevice()
    {
        return device;
    }

    public DateTime getExpirationTime()
    {
        return expirationTime;
    }
}
