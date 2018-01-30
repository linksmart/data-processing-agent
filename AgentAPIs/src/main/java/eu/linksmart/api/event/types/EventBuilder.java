package eu.linksmart.api.event.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.exceptions.UntraceableException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 21.06.2017 a researcher of Fraunhofer FIT.
 */
public interface EventBuilder<IDType, ValueType, Event extends EventEnvelope<IDType,ValueType>> {

    @JsonIgnore
    default EventEnvelope factory(Object id, Object attributeID, Object value, long time, Map<String, Object> additionalAttributes) throws UntraceableException {
        EventEnvelope event;
        try {
            event = BuilderOf().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw  new UntraceableException(e);
        }

        if (value instanceof EventEnvelope ) {
            return refactory((EventEnvelope) value);
        }
        else {
            event.setDate(new Date(time));
            event.setId(id);
            event.setAttributeId(attributeID);
            event.setUnsafeValue(value);
            event.setAdditionalData(additionalAttributes);
        }


        return event;

    }
    @JsonIgnore
    default EventEnvelope factory(String id, String attributeID, Object value, long time, Map<String, Object> additionalAttributes) throws UntraceableException {
        return factory((Object)id, (Object)attributeID,value,time,additionalAttributes);

    }

    default Event refactory(EventEnvelope event) throws UntraceableException{
        EventEnvelope ret = null;
        try {
            ret = BuilderOf().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw  new UntraceableException(e);
        }
        if (event.getClass().isInstance(ret))
            return (Event) event;
        else {
            ret.setDate(event.getDate());
            ret.setId( event.getId());
            ret.setUnsafeValue(event.getValue());
            ret.setAttributeId(event.getAttributeId());
            ret.setAdditionalData(event.getAdditionalData());
        }

        return (Event) ret;
    }
    Class<Event> BuilderOf();

    default void Register(){
        if(!containsBuilder(BuilderOf()))
            registerBuilder(BuilderOf(),this);
    }

    @JsonIgnore
    default EventEnvelope factory(Object id, Object attributeID, Object value, Date date, Map<String, Object> additionalAttributes) throws UntraceableException {
        return factory( id, attributeID, value, date.getTime(),additionalAttributes);
    }
    static <T> void registerBuilder(Class<T> to, EventBuilder builder){
        EventEnvelope.builders.put(to.getCanonicalName(),builder);
        EventEnvelope.builders.put(to.getName(),builder);
        EventEnvelope.builders.put(to.getSimpleName(),builder);

    }

    static <T> void setAsDefaultBuilder(Class<T> to, EventBuilder builder, boolean force) throws UntraceableException{
        if(!force && containsBuilder(to))
            throw new UntraceableException("Default builder is already set. If you want to overwrite force it!");

        registerBuilder(to,builder);
        EventEnvelope.builders.put("default",builder);
        EventEnvelope.builders.put("",builder);
        EventEnvelope.builders.put(null,builder);


    }
    static <T> void setAsDefaultBuilder(Class<T> to, EventBuilder builder) throws UntraceableException{
        setAsDefaultBuilder(to,builder,false);
    }

    static <T> EventBuilder getBuilder(Class<T> to){

        return EventEnvelope.builders.getOrDefault(
                to.getCanonicalName(),
                EventEnvelope.builders.getOrDefault(
                        to.getSimpleName(),
                        EventEnvelope.builders.getOrDefault(to.getName(),
                                EventEnvelope.builders.getOrDefault("default",
                                        EventEnvelope.builders.getOrDefault("",
                                                EventEnvelope.builders.getOrDefault(null,
                                                        null
                                                ))))));

    }
    static EventBuilder getBuilder(String klass){

        return EventEnvelope.builders.get(klass);

    }
    static EventBuilder getBuilder(){

        return EventEnvelope.builders.get(null);

    }
    static <T> boolean containsBuilder(Class<T> to){


        return  EventEnvelope.builders.containsKey(to.getCanonicalName()) ||
        EventEnvelope.builders.containsKey(to.getName()) ||
        EventEnvelope.builders.containsKey(to.getSimpleName());

    }


}
