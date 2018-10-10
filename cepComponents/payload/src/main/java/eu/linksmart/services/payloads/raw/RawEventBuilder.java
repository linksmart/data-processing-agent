package eu.linksmart.services.payloads.raw;

import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;

import java.util.Date;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 12.01.2018 a researcher of Fraunhofer FIT.
 */
public class RawEventBuilder implements EventBuilder<Object, Map, RawEvent> {

    @Override
    public Class BuilderOf() {
        return RawEvent.class;
    }
}
