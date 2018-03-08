package eu.linksmart.services.payloads.raw;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.services.utils.function.Utils;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by José Ángel Carvajal on 10.11.2017 a researcher of Fraunhofer FIT.
 */
public class RawEvent extends ConcurrentHashMap implements EventEnvelope<Object,Map>, Map {
    static {
        EventBuilder.registerBuilder(RawEvent.class,new RawEventBuilder());
    }
    @JsonIgnore
    public static transient String defaultTopic = "LS/sensor/"+UUID.randomUUID().toString()+"/RAW/1.0/Datastream/";
    @Override
    public void topicDataConstructor(String topic) {

    }

    @Override
    public Date getDate() {
        Object rawDate=this.getOrDefault("date", this.getOrDefault("Date",this.getOrDefault("time",this.getOrDefault("Time", this.getOrDefault("phenomenonTime",this.getOrDefault("phenomenontime",new Date()))))));
        if(rawDate instanceof Date)
            return (Date) rawDate;
        else if(rawDate instanceof String)
            try {
                return Utils.formISO8601((String) rawDate);
            } catch (IOException e) {
                // nothing
            }

        setDate(new Date());
        return getDate();
    }

    @Override
    public String getIsoTimestamp() {
        return Utils.getIsoTimestamp(getDate());
    }

    @Override
    public Object getId() {
        return this.getOrDefault("id",this.getOrDefault("ID",this.getOrDefault("Id",this.getOrDefault("@iot.id",this.getOrDefault("bn","Auto:"+UUID.randomUUID().toString()))))).toString();
    }

    @Override
    public Object getAttributeId() {
        return getId();
    }

    @Override
    public Map getValue() {
        return this;
    }

    @Override
    public void setDate(Date time) {
        this.put("date",time);
    }

    @Override
    public void setId(Object id) {
        this.put("id",id);

    }

    @Override
    public void setAttributeId(Object id) {
        setId(id);
    }

    @Override
    public void setValue(Map value) {
        this.putAll(value);
    }

    @Override
    public void setUnsafeValue(Object value) {
        if (value instanceof Map)
            setValue((Map) value);

        this.putIfAbsent("result",value);
    }

    @Override
    public String getClassTopic() {
        return defaultTopic;
    }

    @Override
    public void setClassTopic(String topic) {
        defaultTopic = topic;
    }

    @Override
    public Map<String, Object> getAdditionalData() {
        return this;
    }

    @Override
    public void setAdditionalData(Map<String, Object> additionalData) {
        additionalData.putAll(additionalData);
    }

    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        return this;
    }

    @Override
    public void destroy() throws Exception {

    }
}
