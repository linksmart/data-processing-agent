package eu.almanac.event.datafusion.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventMqttFeederImpl extends MqttFeederImpl {
    private ObjectMapper mapper = new ObjectMapper();
    public EventMqttFeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException {
        super(brokerName, brokerPort, topic);

    }

    private static String getThingID(String topic){
        String [] aux = topic.split("/");
        return aux[aux.length-2];
    }
    @Override
    protected void mangeEvent(String topic,byte[] rawEvent) {
        try {
            String id= getThingID(topic);
            if(mapper==null)
                mapper = new ObjectMapper();

            //try {

                Observation event = mapper.readValue(rawEvent,Observation.class);
                event.setId(id);
                for (DataFusionWrapper i : dataFusionWrappers.values())
                    i.addEvent(topic, event, event.getClass());

            /*}catch (InvalidFormatException e){
                Observation event1 = mapper.readValue(rawEvent,Observation.class);

                event1.setId(id);
                for (DataFusionWrapper i : dataFusionWrappers.values())
                    i.addEvent(topic, event1, event1.getClass());

            }*/





        }catch(Exception e){
            loggerService.error(e.getMessage(),e);

        }
    }
}