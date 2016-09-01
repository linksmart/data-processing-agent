package eu.linksmart.api.event.types;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * Created by angel on 17/11/15.
 */
public interface EventType< IDType, ValueType> {
    public void topicDataConstructor(String topic);

    public Date getDate();
    public String getIsoTimestamp();
    public IDType getId();
    public IDType getAttributeId();
    public ValueType getValue();


    public void setDate(Date value);
    @JsonIgnore
    public void  setId(IDType value);
    public void setAttributeId(IDType value);
    public void setValue(ValueType value);






}
