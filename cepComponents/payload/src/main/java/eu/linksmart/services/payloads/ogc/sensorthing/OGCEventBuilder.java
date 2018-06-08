package eu.linksmart.services.payloads.ogc.sensorthing;

import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 12.01.2018 a researcher of Fraunhofer FIT.
 */
public class OGCEventBuilder implements EventBuilder<Object, Object,ObservationImpl>{


    @Override
    public EventEnvelope factory(Object id, Object attributeID, Object value, long time, Object url, Map<String, Object> additionalAttributes) throws UntraceableException {
        Observation event;

       if (value instanceof EventEnvelope ) {
            return refactory((EventEnvelope) value);
        }
        else {

           event = factory(value,value!=null?value.toString():"",attributeID,id,time);
        }

        return event;
    }

    @Override
    public ObservationImpl refactory(EventEnvelope event) throws UntraceableException {
        Observation ret ;

        if (event instanceof Observation)
             ret = (Observation) event;
        else {
           ret = factory(event.getValue(),event.getValue()!=null?event.getValue().toString():"",event.getAttributeId(),event.getId(),event.getDate().getTime());
        }
        ret.setAdditionalData(event.getAdditionalData());

        return (ObservationImpl) ret;
    }

    @Override
    public Class<ObservationImpl> BuilderOf() {
        return ObservationImpl.class;
    }

    static Observation factory(Object event, String resultType, String StreamID, String sensorID) {
        return factory(event,resultType,StreamID,sensorID,(new Date()).getTime());

    }
    static Observation factory(Object event, String resultType, Object StreamID, Object sensorID, long time ) {
        // Construct Sensor and Thing with the the Agent id.
        Sensor sen = new SensorImpl();
        sen.setId(sensorID);

        Thing th = new ThingImpl();
        th.setId(sensorID);

        // construct the a Datastream with the Statement Id
        Datastream ds = new DatastreamImpl();
        ds.setId(StreamID);
        ds.setSensor(sen);

        // add related objects
        ds.setSensor(sen);
        ds.setThing(th);

        // construct feature of interest with Id made by the hash of the name of the statement
      //  FeatureOfInterest fi = new FeatureOfInterestImpl();
       // fi.setId(UUID.randomUUID());
        //fi.setDescription(resultType);



        // construct Observation with random ID
        Observation ob = new ObservationImpl();
        ob.setId(UUID.randomUUID());
        ob.setDatastream(ds);
        ob.setPhenomenonTime(new Date());
//        ob.setFeatureOfInterest(fi);
        ob.setDatastream(ds);
        ob.setResult(event);
        ob.setFeatureOfInterest(null);
        ob.setDate(new Date(time));
        // add related objects
        //fi.addObservations(ob);

        ArrayList<Observation> obs = (new ArrayList<>());
        obs.add(ob);
        ds.setObservations(obs);

        return ob;
    }
}
