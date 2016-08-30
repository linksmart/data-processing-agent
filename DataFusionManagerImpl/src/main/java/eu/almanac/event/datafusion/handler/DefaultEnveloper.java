package eu.almanac.event.datafusion.handler;

import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.datafusion.components.Enveloper;
import eu.linksmart.api.event.datafusion.EventType;

import java.util.Date;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultEnveloper implements Enveloper<String,Object> {
    @Override
    public EventType<String, Object> pack(Object payload, Date date, String id, String idProperty, String description) {
        if (payload instanceof Observation) {
            return (Observation)payload;

            // TODO: Esper Specific code must be moved to esper artifact
            // }else if (event instanceof EventBean){
            //    return handleObject(((EventBean)event).getUnderlying(),description,streamID);
        }else {

            return Observation.factory(payload, description, idProperty, id);
        }
    }

    @Override
    public void close() {

    }
}
