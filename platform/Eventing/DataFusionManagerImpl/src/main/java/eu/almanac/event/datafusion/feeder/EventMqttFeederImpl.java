package eu.almanac.event.datafusion.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.almanac.event.datafusion.intern.Const;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.gc.utils.mqtt.types.Topic;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import org.eclipse.paho.client.mqttv3.MqttException;
import sun.security.pkcs.ParsingException;

import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttFeederImpl extends MqttFeederImpl {
    private ObjectMapper mapper = new ObjectMapper();
    protected Map<Topic,Class> topicToClass= new Hashtable<Topic,Class>();
    protected Map<String,String> classToAlias= new Hashtable<String, String>();
    public EventMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException, InstantiationException {
        super(brokerName, brokerPort, topic);

        LoadTypesIntoEngines();

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
        for (DataFusionWrapper dfw:dataFusionWrappers.values()) {
            for(int i=0; i<classes.size();i++) {
                try {
                    Class aClass = Class.forName(classes.get(i).toString());
                    topicToClass.put(new Topic(topics.get(i).toString()),aClass);
                    classToAlias.put(aClass.getCanonicalName(),aliases.get(i).toString());
                    dfw.addEventType(aliases.get(i).toString(), aClass);
                } catch (ClassNotFoundException e) {
                    loggerService.error(e.getMessage(), e);
                }
            }

        }
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
                }
            }


            if(event!=null) {
                // hardcoded analysis for ALMANAC project
                if(event instanceof Observation) {
                    String id = getThingID(topic);
                    ((Observation)event).setId(id);
                }

                for (DataFusionWrapper i : dataFusionWrappers.values())
                    i.addEvent(topic, event, event.getClass());
            }else
                throw new ParsingException("No suitable class for the received event");
        }catch(Exception e){
            loggerService.error(e.getMessage(),e);

        }
    }
}
