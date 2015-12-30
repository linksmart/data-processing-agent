package eu.linksmart.api.event.datafusion;

import java.util.Date;

/**
 * Created by angel on 17/11/15.
 */
public interface EventType< IDType, IDAttribute, ValueType> {
    public void topicDataConstructor(String topic);

    public Date getDate();
    public String getIsoTimestamp();
    public IDType getId();
    public IDAttribute getAttributeId();
    public ValueType getValue();


    public void setDate(Date value);
    public void  setId(IDType value);
    public void setAttributeId(IDAttribute value);
    public void setValue(ValueType value);


}
