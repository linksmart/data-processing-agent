package eu.linksmart.services.payloads.generic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.JsonSerializable;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 16.08.2016 a researcher of Fraunhofer FIT.
 */
public class Event<IDType, ValueType> implements EventEnvelope<IDType,ValueType > {
   @JsonIgnore
    protected Date date;
    @JsonIgnore
    protected IDType id;
    @JsonIgnore
    protected IDType attributeId;
    @JsonIgnore
    protected ValueType value;

    protected Event(){
        date = new Date();
        id = null;
        attributeId = null;
        value = null;
    }
    protected Event(Date date, IDType id, IDType attributeId, ValueType value){
        this.date = date;
        this.id = id;
        this.attributeId = attributeId;
        this.value = value;
    }
    @Override
    public void topicDataConstructor(String topic) {
        // there is non-generic method
    }
    @JsonIgnore
    @Override
    public Date getDate() {
        return date;
    }

    @JsonIgnore
    @Override
    public String getIsoTimestamp() {

        return DateTimeFormatter.ISO_INSTANT.format(date.toInstant());

    }
    @JsonIgnore
    @Override
    public IDType getId() {
        return id;
    }
    @JsonIgnore
    @Override
    public IDType getAttributeId() {
        return attributeId;
    }
    @JsonIgnore
    @Override
    public ValueType getValue() {
        return value;
    }
    @JsonIgnore
    @Override
    public void setDate(Date value) {
        date=value;
    }
    @JsonIgnore
    @Override
    public void setId(IDType value) {
        id=value;
    }
    @JsonIgnore
    @Override
    public void setAttributeId(IDType value) {
        attributeId=value;
    }
    @JsonIgnore
    @Override
    public void setValue(ValueType value) {
        this.value=value;
    }
    @Override
    public String toString(){
        return getIsoTimestamp();
    }

    static public <T, M> EventEnvelope factory(Date date, T id, T attributeId, M value) {
        return new Event(date,id,attributeId,value);
    }
    static public<T extends Object> EventEnvelope factory(Date date) {
        EventEnvelope event= new Event();
        event.setDate(date);
        return event;
    }
    static public<T , M> EventEnvelope factory(Date date, T id, M value) {
        EventEnvelope event= new Event();
        event.setDate(date);
        event.setId(id);
        return event;
    }
    static public<T extends Object> EventEnvelope factory(Date date, T value) {
        EventEnvelope event= new Event();
        event.setDate(date);
        event.setValue(value);
        return event;
    }
    static public<T extends Object> EventEnvelope factory(Object value) {
        EventEnvelope event= new Event();
        event.setValue(value);
        return event;
    }

    @Override
    public JsonSerializable build() throws TraceableException, UntraceableException {
        return this;
    }

    @Override
    public void destroy() throws Exception {
        // nothing
    }
}
