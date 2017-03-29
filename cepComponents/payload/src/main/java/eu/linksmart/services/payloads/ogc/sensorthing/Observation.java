package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfo;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateSerializer;

import java.util.Date;
import java.time.Period;


/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="objectID", scope = Observation.class)
public class Observation extends CommonControlInfo {

/*
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "resultQuality")
    @JsonDeserialize(as=ArrayList.class)
    protected ArrayList<DQ_Element> resultQuality;
    @JsonProperty(value = "resultQuality")
    @JsonDeserialize(as=ArrayList.class)
    public ArrayList<DQ_Element> getResultQuality() {
        return resultQuality;
    }
    @JsonProperty(value = "resultQuality")
    @JsonPropertyDescription("TBD.")
    @JsonDeserialize(as=ArrayList.class)
    public void setResultQuality(ArrayList<DQ_Element> resultQuality) {
        this.resultQuality = resultQuality;
    }*/


    /**
     * The time instant or period of when the Observation happens.
     Note: Many resource-constrained sensing devices do not have a clock.
     As a result, a client may omit phenonmenonTime when POST new Observations,
     even though phenonmenonTime is a mandatory property. When a SensorThings service
     receives a POST Observations without phenonmenonTime, the service SHALL
     assign the current server time to the value of the phenomenonTime.
     * */

    @JsonDeserialize(using =DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    @JsonPropertyDescription("The time instant or period of when the Observation happens.")
    @JsonProperty(value = "phenomenonTime")
    protected Date phenomenonTime;
    @JsonProperty(value = "phenomenonTime")
    @JsonPropertyDescription("TBD")
    public Date getPhenomenonTime() {
        return phenomenonTime;
    }
    @JsonProperty(value = "phenomenonTime")
    @JsonPropertyDescription("TBD.")
    public void setPhenomenonTime(Date phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    @JsonDeserialize(using =DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "resultTime")
    protected Date resultTime;
    @JsonProperty(value = "resultTime")
    @JsonPropertyDescription("TBD")
    public Date getResultTime() {
        return resultTime;
    }
    @JsonProperty(value = "resultTime")
    @JsonPropertyDescription("TBD.")
    public void setResultTime(Date resultTime) {
        this.resultTime = resultTime;
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "result")
    protected Object result;
    @JsonProperty(value = "result")
    @JsonPropertyDescription("TBD")
    public Object getResult() {
        return result;
    }
    @JsonProperty(value = "result")
    @JsonPropertyDescription("TBD.")
    public void setResult(Object result) {
        this.result = result;
    }


    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "validTime")
    protected Period validTime;
    @JsonProperty(value = "validTime")
    @JsonPropertyDescription("TBD")
    public Period getValidTime() {
        return validTime;
    }
    @JsonProperty(value = "validTime")
    @JsonPropertyDescription("TBD.")
    public void setValidTime(Period validTime) {
        this.validTime = validTime;
    }




}
