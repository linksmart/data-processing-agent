package it.ismb.pertlab.pwal.api.events.pubsub.topics;

public class PWALTopicsUtility
{
    public static String createNewDataFromDeviceTopic(String networkId,
            String deviceId)
    {
        String newDataAvailableTopic = "devices/%s/newdata/%s";
        return String.format(newDataAvailableTopic, networkId, deviceId);
    }
    
    public static String createNewDeviceAddedTopic(String networkId)
    {
        String newDeviceAddedTopic = "devices/%s/newdevice";
        return String.format(newDeviceAddedTopic, networkId);
    }
    
    public static String createDeviceRemovedTopic(String networkId)
    {
        String deviceRemovedTopic = "devices/%s/deviceremoved";
        return String.format(deviceRemovedTopic, networkId);
    }
}
