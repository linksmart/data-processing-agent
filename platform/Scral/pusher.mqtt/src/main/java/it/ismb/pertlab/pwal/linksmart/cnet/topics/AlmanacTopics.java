package it.ismb.pertlab.pwal.linksmart.cnet.topics;

public class AlmanacTopics
{
    public static String createAlmanacTopic(String eventType, String payloadType,
            String deviceId, String iotPropertyId)
    {
        return String.format("/almanac/%s/%s/%s", eventType, payloadType,
                deviceId);
    }
}
