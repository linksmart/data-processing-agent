package eu.linksmart.services.event.datafusion.feeder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.datafusion.components.CEPEngine;
import eu.linksmart.api.event.datafusion.types.EventType;
import eu.linksmart.api.event.datafusion.components.Feeder;
import eu.linksmart.services.utils.mqtt.types.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;
import sun.security.pkcs.ParsingException;

import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttFeederImpl extends MqttFeederImpl {

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Class> compiledTopicClass = new Hashtable<>();
    public EventMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException, InstantiationException {
        super(brokerName, brokerPort, topic,EventMqttFeederImpl.class.getSimpleName(),"Provides a MQTT API to insert events into the CEP engine",MqttFeederImpl.class.getSimpleName(), Feeder.class.getSimpleName());

        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);

    }



    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
          try {
          if(mapper==null)
                mapper = new ObjectMapper();
            Object event=null;
            if(!compiledTopicClass.containsKey(topic)) {
                if(topicToClass.isEmpty())
                    event = mapper.readValue(rawEvent, Observation.class);
                else
                    for (Topic t : topicToClass.keySet()) {
                        if (t.equals(topic)) {
                            event = mapper.readValue(rawEvent, topicToClass.get(t));
                            compiledTopicClass.put(topic, topicToClass.get(t));
                            break;
                        }
                    }
            }else{
                event = mapper.readValue(rawEvent, compiledTopicClass.get(topic));
            }

            if(event!=null) {
                // if the event needs data from the topic
                if(event instanceof EventType) {
                    ((EventType)event).topicDataConstructor(topic);
                }

                for (CEPEngine i : dataFusionWrappers.values())
                    i.addEvent(topic, (EventType)event, event.getClass());
            }else
                throw new ParsingException("No suitable class for the received event");
        }catch(Exception e){
            loggerService.error(e.getMessage(),e);

        }
    }
}
