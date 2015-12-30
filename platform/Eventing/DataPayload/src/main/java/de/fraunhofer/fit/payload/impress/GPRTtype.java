package de.fraunhofer.fit.payload.impress;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.EventType;
import eu.linksmart.gc.utils.function.Utils;

/**
 * Created by angel on 12/11/15.
 */
public class GPRTtype extends Component implements Serializable, EventType<Integer,Integer,Double> {

    @JsonPropertyDescription("The time point/period of when the observation happens. To be rendered as ISO8601 time point/period string.")
    @JsonProperty(value = "timestamp")
    protected Date timestamp;
    @JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
    @JsonProperty(value = "value")
    protected Double value;
    @JsonPropertyDescription("Id of the device which reported the measurement.")
    @JsonProperty(value = "deviceID")
    protected int deviceID;
    @JsonPropertyDescription("Id of the variable or observedProperty of the reported value.")
    @JsonProperty(value = "variableID")
    protected int variableID;

    public GPRTtype() {
        super(GPRTtype.class.getSimpleName(), "Payload type used in GPRT institute of the UFPE", EventType.class.getSimpleName());
    }

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

    public int getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }

    public int getVariableID() {
        return variableID;
    }

    public void setVariableID(int variableID) {
        this.variableID = variableID;
    }

    @Override
    public void topicDataConstructor(String topic) {

        String[] aux = topic.split("/");
        if(aux.length>3)
            setDeviceID(Integer.valueOf(aux[3]));
        if(aux.length>5)
            setVariableID(Integer.valueOf(aux[5]));
    }

    @Override
    public Date getDate() {
        return timestamp;
    }

    @Override
    public String getIsoTimestamp() {
        return Utils.getIsoTimestamp(timestamp);
    }

    @Override
    public void setDate(Date value) {
        timestamp =value;
    }
    @Override
    public  void setId(Integer value) {

        deviceID = value;

    }

    @Override
    public void setAttributeId(Integer value) {
        variableID = value;
    }

    @Override
    public Integer getId() {
        return deviceID;
    }

    @Override
    public Integer getAttributeId() {
        return variableID;
    }

}
