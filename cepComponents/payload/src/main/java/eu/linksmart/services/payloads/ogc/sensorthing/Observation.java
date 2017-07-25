package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateSerializer;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;

import java.time.Period;
import java.util.Date;

/**
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 */

@JsonDeserialize(as = ObservationImpl.class)
@JsonSerialize(as = ObservationImpl.class)
public interface Observation extends EventEnvelope, CommonControlInfo{
    @JsonGetter("featureOfInterest")
    FeatureOfInterest getFeatureOfInterest();

    @JsonSetter("featureOfInterest")
    void setFeatureOfInterest(FeatureOfInterest featureOfInterest);

    @JsonGetter("datastream")
    Datastream getDatastream();

    @JsonSetter("datastream")
    void setDatastream(Datastream datastream);

    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "Datastream@iot.navigationLink")
    String getDatastreamNavigationLink();

    @JsonGetter(value = "FeatureOfInterest@iot.navigationLink")
    String getFeatureOfInterestNavigationLink();

    @JsonSetter(value = "Datastream@iot.navigationLink")
    void setDatastreamNavigationLink(String str);

    @JsonSetter(value = "FeatureOfInterest@iot.navigationLink")
    void setFeatureOfInterestNavigationLink(String str);

    @JsonSerialize(using = DateSerializer.class)
    @JsonGetter(value = "phenomenonTime")
    @JsonPropertyDescription("TBD")
    Date getPhenomenonTime();

    @JsonDeserialize(using =DateDeserializer.class)
    @JsonSetter(value = "phenomenonTime")
    @JsonPropertyDescription("TBD.")
    void setPhenomenonTime(Date phenomenonTime);

    @JsonSerialize(using = DateSerializer.class)
    @JsonGetter(value = "resultTime")
    @JsonPropertyDescription("TBD")
    Date getResultTime();

    @JsonDeserialize(using =DateDeserializer.class)
    @JsonSetter(value = "resultTime")
    @JsonPropertyDescription("TBD.")
    void setResultTime(Date resultTime);

    @JsonGetter(value = "result")
    @JsonPropertyDescription("TBD")
    Object getResult();

    @JsonSetter(value = "result")
    @JsonPropertyDescription("TBD.")
    void setResult(Object result);

    @JsonGetter(value = "validTime")
    @JsonPropertyDescription("TBD")
    Period getValidTime();

    @JsonSetter(value = "validTime")
    @JsonPropertyDescription("TBD.")
    void setValidTime(Period validTime);
}
