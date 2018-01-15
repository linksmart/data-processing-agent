package eu.linksmart.services.payloads.raw;

import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 12.01.2018 a researcher of Fraunhofer FIT.
 */
public class RawEventBuilder implements EventBuilder<Object,Map> {

    @Override
    public EventEnvelope<Object, Map> factory(Object id, Object attributeID, Map value, long time, Map<String, Object> additionalAttributes) {
        RawEvent event = new RawEvent();

        event.setId(id);
        event.setAttributeId(attributeID);
        event.setDate(new Date(time));


        event.putAll(additionalAttributes);

        return event;
    }

    @Override
    public EventEnvelope factory(String id, String attributeID, Object value, long time, Map<String, Object> additionalAttributes) {
        if(value instanceof Map)
            return factory((Object)id, (Object)attributeID,(Map)value,time,additionalAttributes);
        else {
            RawEvent event = new RawEvent();

            event.setId(id);
            event.setAttributeId(attributeID);
            event.setDate(new Date(time));
            event.put("result",value);

            event.putAll(additionalAttributes);

            return event;
        }
    }

    @Override
    public Class BuilderOf() {
        return RawEvent.class;
    }
}
