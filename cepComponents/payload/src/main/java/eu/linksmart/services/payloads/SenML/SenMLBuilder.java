package eu.linksmart.services.payloads.SenML;

import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

/**
 * Created by José Ángel Carvajal on 12.01.2018 a researcher of Fraunhofer FIT.
 */
public class SenMLBuilder implements EventBuilder<String,Vector<SenML.Measurement>> {
    @Override
    public EventEnvelope<String, Vector<SenML.Measurement>> factory(String id, String attributeID, Vector<SenML.Measurement> value, long time, Map<String, Object> additionalAttributes) {
        SenML event = new SenML();
        event.setBaseName(id);
        event.setValue(value);
        event.setBaseTime(time);

        return event;
    }

    @Override
    public EventEnvelope factory(String id, String attributeID, Object value, long time, Map<String, Object> additionalAttributes) {
        SenML event = new SenML();
        if(value instanceof Map ){
            ((Map) value).forEach((k,v)->event.addValue(k.toString(),v));


        }if(value instanceof Collection){
            int i = 0;
            for (Object e : (Collection) value){
                event.addValue(attributeID+"["+i+"]",e);
                i++;
            }

        }else {
            event.addValue(attributeID,value);
        }

        return factory(id, attributeID, event.getE(),time, additionalAttributes);
    }

    @Override
    public Class BuilderOf() {
        return SenML.class;
    }


}
