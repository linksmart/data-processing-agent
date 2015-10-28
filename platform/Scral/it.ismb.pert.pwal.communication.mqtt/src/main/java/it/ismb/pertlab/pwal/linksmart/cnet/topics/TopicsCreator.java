package it.ismb.pertlab.pwal.linksmart.cnet.topics;

public class TopicsCreator
{
    public static String createAlmanacTopic(String eventType, String payloadType,
            String deviceAbout, String iotPropertyId)
    {
    	String topic = String.format("/impress/%s/%s/%s/%s", eventType, payloadType,
                deviceAbout, iotPropertyId);
    	if(topic.endsWith("/"))
    		topic = topic.substring(0, topic.length()-1);
    	
        return topic;
    }
}
