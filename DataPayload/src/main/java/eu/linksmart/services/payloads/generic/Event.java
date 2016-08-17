package eu.linksmart.services.payloads.generic;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.datafusion.EventType;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 16.08.2016 a researcher of Fraunhofer FIT.
 */
public class Event implements EventType<Object,Object > {
    @JsonProperty("Date")
    protected Date date;
    @JsonProperty("ID")
    protected Object id;
    @JsonProperty("AttributeID")
    protected Object attributeId;
    @JsonProperty("Value")
    protected Object value;

    protected Event(){
        date = new Date();
        id = UUID.randomUUID().toString();
        attributeId = UUID.randomUUID().toString();
        value = null;
    }
    protected Event(Date date, Object id, Object attributeId, Object value){
        this.date = date;
        this.id = id;
        this.attributeId = attributeId;
        this.value = value;
    }
    @Override
    public void topicDataConstructor(String topic) {
        // there is non-generic method
    }
    @JsonProperty("Date")
    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public String getIsoTimestamp() {

        return DateTimeFormatter.ISO_INSTANT.format(date.toInstant());

    }
    @JsonProperty("ID")
    @Override
    public Object getId() {
        return id;
    }
    @JsonProperty("AttributeID")
    @Override
    public Object getAttributeId() {
        return attributeId;
    }
    @JsonProperty("Value")
    @Override
    public Object getValue() {
        return value;
    }
    @JsonProperty("Date")
    @Override
    public void setDate(Date value) {
        date=value;
    }
    @JsonProperty("ID")
    @Override
    public void setId(Object value) {
        id=value;
    }
    @JsonProperty("AttributeID")
    @Override
    public void setAttributeId(Object value) {
        attributeId=value;
    }
    @JsonProperty("Value")
    @Override
    public void setValue(Object value) {
        this.value=value;
    }
    @Override
    public String toString(){
        return getIsoTimestamp();
    }

    static public <T, M> EventType factory(Date date, T id, T attributeId, M value) {
        return new Event(date,id,attributeId,value);
    }
    static public<T extends Object> EventType factory(Date date) {
        EventType event= new Event();
        event.setDate(date);
        return event;
    }
    static public<T , M> EventType factory(Date date, T id, M value) {
        EventType event= new Event();
        event.setDate(date);
        event.setId(id);
        return event;
    }
    static public<T extends Object> EventType factory(Date date, T value) {
        EventType event= new Event();
        event.setDate(date);
        event.setValue(value);
        return event;
    }
    static public<T extends Object> EventType factory(Object value) {
        EventType event= new Event();
        event.setValue(value);
        return event;
    }
}
