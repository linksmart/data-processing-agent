package eu.linksmart.services.payloads.gprt;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.generic.Event;

/**
 * Created by angel on 12/11/15.
 */
public class GPRTtype extends Event implements Serializable {

    public GPRTtype() {
        super();

    }
    @JsonPropertyDescription("The time point/period of when the observation happens. To be rendered as ISO8601 time point/period string.")
    @JsonProperty(value = "timestamp")
    public Date getTimestamp() {
        return getDate();
    }
    @JsonPropertyDescription("The time point/period of when the observation happens. To be rendered as ISO8601 time point/period string.")
    @JsonProperty(value = "timestamp")
    public void setTimestamp(Date timestamp) {
        setDate(timestamp);
    }
    @JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
    @JsonProperty(value = "value")
    public Double getValue() {
        return (Double)super.getValue();
    }

    @JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
    @JsonProperty(value = "value")
    public void setValue(Double value) {
        super.setValue(value);
    }

    @JsonPropertyDescription("Id of the device which reported the measurement.")
    @JsonProperty(value = "deviceID")
    public int getDeviceID() {
        return (Integer)super.getId();
    }
    @JsonPropertyDescription("Id of the device which reported the measurement.")
    @JsonProperty(value = "deviceID")
    public void setDeviceID(int deviceID) {
        setId(deviceID);
    }

    @JsonPropertyDescription("Id of the variable or observedProperty of the reported value.")
    @JsonProperty(value = "variableID")
    public int getVariableID() {
        return (Integer)getAttributeId();
    }
    @JsonPropertyDescription("Id of the variable or observedProperty of the reported value.")
    @JsonProperty(value = "variableID")
    public void setVariableID(int variableID) {
        setAttributeId(variableID);
    }

    @Override
    public void topicDataConstructor(String topic) {

        String[] aux = topic.split("/");
        if(aux.length>3)
            setDeviceID(Integer.valueOf(aux[3]));
        if(aux.length>5)
            setVariableID(Integer.valueOf(aux[5]));
    }


}
