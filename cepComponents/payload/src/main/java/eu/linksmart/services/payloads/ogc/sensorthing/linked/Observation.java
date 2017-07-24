package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;


/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

//@JsonIgnoreProperties({"@iot.id, @iot.selfLink"})
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = Observation.class)
public class Observation extends eu.linksmart.services.payloads.ogc.sensorthing.Observation {

    @JsonGetter("featureOfInterest")
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }
    @JsonSetter("featureOfInterest")
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        this.featureOfInterest = featureOfInterest;
        this.featureOfInterest.addObservations(this);
    }

    @JsonIgnore
    protected FeatureOfInterest featureOfInterest;


    @JsonIgnore
    protected Datastream datastream = null;
    @JsonGetter("datastream")
    public Datastream getDatastream() {
        return datastream;
    }
    @JsonSetter("datastream")
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


    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "Datastream@iot.navigationLink")
    public String getDatastreamNavigationLink() {
        return "Observation("+id+")/Datastream";
    }

    @JsonGetter(value = "FeatureOfInterest@iot.navigationLink")
    public String getFeatureOfInterestNavigationLink() {
        return "Observation("+id+")/FeatureOfInterest";
    }

    @JsonSetter(value = "Datastream@iot.navigationLink")
    public void setDatastreamNavigationLink(String str) {

    }

    @JsonSetter(value = "FeatureOfInterest@iot.navigationLink")
    public void setFeatureOfInterestNavigationLink(String str) {
    }


}
