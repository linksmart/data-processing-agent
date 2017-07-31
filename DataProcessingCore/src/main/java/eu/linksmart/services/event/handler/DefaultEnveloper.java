package eu.linksmart.services.event.handler;

import eu.linksmart.api.event.components.Enveloper;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;

import java.util.Date;

/**
 * Created by José Ángel Carvajal on 23.08.2016 a researcher of Fraunhofer FIT.
 */
public class DefaultEnveloper implements Enveloper  {

    @Override
    public <IDType , ValueType > EventEnvelope pack(ValueType payload, Date date, IDType id, IDType idProperty, String description, String name) {
        if (payload instanceof EventEnvelope) {
            return (EventEnvelope) payload;

        }else {

            return Observation.factory(payload, description, idProperty.toString(), id.toString(), name);
        }
    }

    @Override
    public void close() {

    }
}
