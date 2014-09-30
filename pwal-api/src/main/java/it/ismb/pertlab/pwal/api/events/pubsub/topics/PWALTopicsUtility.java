package it.ismb.pertlab.pwal.api.events.pubsub.topics;

public class PWALTopicsUtility
{
    public static String createDeviceNewDataTopic(String networkId,
            String deviceId)
    {
        String newDataAvailableTopic = "newdata/devices/%s/%s";
        return String.format(newDataAvailableTopic, networkId, deviceId);
    }
    
    public static String newDeviceAddedTopic(String networkId)
    {
        String newDeviceAddedTopic = "newdevice/%s";
        return String.format(newDeviceAddedTopic, networkId);
    }
}
