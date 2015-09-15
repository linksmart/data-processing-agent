package it.ismb.pertlab.pwal.event.format.linksmart.cnet.topics;

public class AlmanacTopics
{
    public static String createAlmanacTopic(String eventType, String payloadType,
            String deviceAbout, String iotPropertyId)
    {
        return String.format("/almanac/%s/%s/%s/%s", eventType, payloadType,
                deviceAbout, iotPropertyId);
    }
}
