package eu.linksmart.services.payloads.raw;

import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.SerializationFactory;
import eu.linksmart.services.payloads.serialization.DefaultSerializationFactory;
import eu.linksmart.services.utils.function.Utils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by José Ángel Carvajal on 10.11.2017 a researcher of Fraunhofer FIT.
 */
public class RawEvent extends ConcurrentHashMap implements EventEnvelope<Object,Map>, Map {
    @Override
    public void topicDataConstructor(String topic) {

    }

    @Override
    public Date getDate() {
        return (Date) this.getOrDefault("date", this.getOrDefault("Date",this.getOrDefault("time",this.getOrDefault("Time", null))));
    }

    @Override
    public String getIsoTimestamp() {
        return Utils.getIsoTimestamp(getDate());
    }

    @Override
    public Object getId() {
        return this.getOrDefault("id",this.getOrDefault("ID",this.getOrDefault("Id",null))).toString();
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

    }

    @Override
    public SerializationFactory getSerializationFactory() {
            return new DefaultSerializationFactory();
    }

    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        return this;
    }

    @Override
    public void destroy() throws Exception {

    }
}
