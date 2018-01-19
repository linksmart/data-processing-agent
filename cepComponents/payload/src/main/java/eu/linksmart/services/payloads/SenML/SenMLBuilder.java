package eu.linksmart.services.payloads.SenML;

import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

/**
 * Created by José Ángel Carvajal on 12.01.2018 a researcher of Fraunhofer FIT.
 */
public class SenMLBuilder implements EventBuilder<String,Vector<SenML.Measurement>, SenML> {


    @Override
    public Class BuilderOf() {
        return SenML.class;
    }


}
