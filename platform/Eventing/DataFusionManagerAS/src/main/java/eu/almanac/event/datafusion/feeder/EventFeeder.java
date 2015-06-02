package eu.almanac.event.datafusion.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import eu.almanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.payload.OGCSensorThing.ObservationNumber;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;

/**
 * Created by Caravajal on 22.05.2015.
 */
public class EventFeeder extends Feeder {
    private ObjectMapper mapper = new ObjectMapper();

    public EventFeeder(String brokerName, String brokerPort, String topic) {
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
           ObservationNumber event = mapper.readValue(rawEvent,ObservationNumber.class);

            LoggerHandler.report("info", "message arrived with ID: " + event.getSensor().getId());
            if(event.getResultValue() == null) {
                Observation event1 = mapper.readValue(rawEvent,Observation.class);

                event1.setId(id);
                for (DataFusionWrapper i : dataFusionWrappers.values())
                    i.addEvent(topic, event1, event1.getClass());
            }else {
                event.setId(id);
                for (DataFusionWrapper i : dataFusionWrappers.values())
                    i.addEvent(topic, event, event.getClass());
            }
        }catch(Exception e){
            e.printStackTrace();

        }
    }
}
