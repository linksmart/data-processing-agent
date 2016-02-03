package eu.almanac.event.datafusion.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.EventType;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.gc.utils.mqtt.types.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;
import sun.security.pkcs.ParsingException;

import java.net.MalformedURLException;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttFeederImpl extends MqttFeederImpl {
    private ObjectMapper mapper = new ObjectMapper();
    public EventMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException, InstantiationException {
        super(brokerName, brokerPort, topic,EventMqttFeederImpl.class.getSimpleName(),"Provides a MQTT API to insert events into the CEP engine",MqttFeederImpl.class.getSimpleName(), Feeder.class.getSimpleName());


    }


    // extract the ID of the topic for ALMANAC project
    private static String getThingID(String topic){
        String [] aux = topic.split("/");
        return aux[aux.length-2];
    }
    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
        try {
            if(mapper==null)
                mapper = new ObjectMapper();
            Object event=null;
            for(Topic t: topicToClass.keySet()) {
                if(t.equals(topic)) {
                   event =mapper.readValue(rawEvent,topicToClass.get(t));
                    break;
                }
            }


            if(event!=null) {
                // if the event needs data from the topic
                if(event instanceof EventType) {
                    ((EventType)event).topicDataConstructor(topic);
                }

                for (CEPEngine i : dataFusionWrappers.values())
                    i.addEvent(topic, event, event.getClass());
            }else
                throw new ParsingException("No suitable class for the received event");
        }catch(Exception e){
            loggerService.error(e.getMessage(),e);

        }
    }
}
