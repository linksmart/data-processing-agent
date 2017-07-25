package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.types.SerializationFactory;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoImpl;
import eu.linksmart.services.payloads.serialization.DefaultSerializationFactory;
import eu.linksmart.services.utils.function.Utils;

import java.time.Period;
import java.util.Date;


/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

//@JsonIgnoreProperties({"@iot.id, @iot.selfLink"})
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = Observation.class)
public class ObservationImpl extends CommonControlInfoImpl implements Observation, EventEnvelope {

    @Override
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }
    @Override
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        this.featureOfInterest.addObservations(this);
    }

    @JsonIgnore
    protected FeatureOfInterest featureOfInterest;


    @JsonIgnore
    protected Datastream datastream = null;
    @Override
    public Datastream getDatastream() {
        return datastream;
    }
    @Override
    public void setDatastream(Datastream datastream) {
        this.datastream = datastream;
        this.datastream.addObservation(this);
    }

    @Override
    public Object getAttributeId() {
        return datastream.getId();
    }

    @Override
    public void setAttributeId(Object id) {
        datastream.setId(id);

    }


    @Override
    public String getDatastreamNavigationLink() {
        return "Observation("+id+")/Datastream";
    }

    @Override
    public String getFeatureOfInterestNavigationLink() {
        return "Observation("+id+")/FeatureOfInterest";
    }

    @Override
    public void setDatastreamNavigationLink(String str) {

    }

    @Override
    public void setFeatureOfInterestNavigationLink(String str) {
    }

    @JsonIgnore
    protected Date phenomenonTime;
    @Override
    public Date getPhenomenonTime() {
        return phenomenonTime;
    }
    @Override
    public void setPhenomenonTime(Date phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    @JsonIgnore
    protected Date resultTime;
    @Override
    public Date getResultTime() {
        return resultTime;
    }
    @Override
    public void setResultTime(Date resultTime) {
        this.resultTime = resultTime;
    }

    @JsonIgnore
    protected Object result;
    @Override
    public Object getResult() {
        return result;
    }
    @Override
    public void setResult(Object result) {
        this.result = result;
    }


    @JsonIgnore
    protected Period validTime;
    @Override
    public Period getValidTime() {
        return validTime;
    }
    @Override
    public void setValidTime(Period validTime) {
        this.validTime = validTime;
    }

    @Override
    public void topicDataConstructor(String topic) {
        // nothing
    }

    @Override
    public Date getDate() {
        return phenomenonTime;
    }

    @Override
    public String getIsoTimestamp() {
        return Utils.getIsoTimestamp(phenomenonTime);
    }



    @Override
    public Object getValue() {
        return result;
    }

    @Override
    public void setDate(Date time) {
        phenomenonTime = time;
    }


    @Override
    public void setValue(Object value) {
        result = value;
    }

    @Override
    public SerializationFactory getSerializationFacotry() {
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
