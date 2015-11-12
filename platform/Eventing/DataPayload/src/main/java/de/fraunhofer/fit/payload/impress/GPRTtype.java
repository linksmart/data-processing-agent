package de.fraunhofer.fit.payload.impress;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
/**
 * Created by angel on 12/11/15.
 */
public class GPRTtype implements Serializable {

    @JsonPropertyDescription("The time point/period of when the observation happens. To be rendered as ISO8601 time point/period string.")
    @JsonProperty(value = "timestamp")
    protected Date timestamp;
    @JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
    @JsonProperty(value = "value")
    protected Double value;
    @JsonPropertyDescription("Id of the device which reported the measurement.")
    @JsonProperty(value = "value")
    protected String deviceID;
    @JsonPropertyDescription("Id of the variable or observedProperty of the reported value.")
    @JsonProperty(value = "variableID")
    protected String variableID;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getVariableID() {
        return variableID;
    }

    public void setVariableID(String variableID) {
        this.variableID = variableID;
    }
}
