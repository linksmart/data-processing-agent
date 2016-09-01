package eu.linksmart.services.event.connectors.Observers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.api.event.types.EventType;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.utils.mqtt.types.Topic;
import org.eclipse.paho.client.mqttv3.MqttException;
import sun.security.pkcs.ParsingException;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttObserver extends IncomingMqttObserver {
    protected Map<Topic,Class> topicToClass= new Hashtable<Topic,Class>();
    protected Map<String,String> classToAlias= new Hashtable<String, String>();

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Class> compiledTopicClass = new Hashtable<>();

    public EventMqttObserver() throws MalformedURLException, MqttException, InstantiationException {

        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        try {
            LoadTypesIntoEngines();
        } catch (InstantiationException e) {
            loggerService.error(e.getMessage(),e);
        }

    }

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

                for (CEPEngine i : CEPEngine.instancedEngines.values())
                    i.addEvent(topic, (EventType)event, event.getClass());
            }else
                throw new ParsingException("No suitable class for the received event");
        }catch(Exception e){
            loggerService.error(e.getMessage(),e);

        }


    }
    protected void LoadTypesIntoEngines() throws  InstantiationException {
        List topics =conf.getList(Const.FeederPayloadTopic);
        List classes =conf.getList(Const.FeederPayloadClass);
        List aliases =conf.getList(Const.FeederPayloadAlias);

        if(classes.size()!=aliases.size()&&aliases.size()!=topics.size())
            throw new InstantiationException(
                    "The configuration parameters of "
                            +Const.FeederPayloadAlias+" "
                            +Const.FeederPayloadClass+" "
                            +Const.FeederPayloadTopic+" do not match"
            );
        for (CEPEngine dfw: CEPEngine.instancedEngines.values()) {
            for(int i=0; i<classes.size();i++) {
                try {
                    Object aClassObject = Class.forName(classes.get(i).toString()).newInstance();
                    topicToClass.put(new Topic(topics.get(i).toString()),aClassObject.getClass());
                    classToAlias.put(aClassObject.getClass().getCanonicalName(),aliases.get(i).toString());
                    dfw.addEventType(aliases.get(i).toString(), aClassObject);
                } catch (ClassNotFoundException|IllegalAccessException e) {
                    loggerService.error(e.getMessage(), e);
                }
            }

        }
    }


}
