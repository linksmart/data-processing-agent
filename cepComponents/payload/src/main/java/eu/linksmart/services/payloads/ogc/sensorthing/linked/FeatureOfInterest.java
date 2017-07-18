package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "object.id")
public class FeatureOfInterest extends eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest {


    /** TBD. */
    @JsonIgnore
    protected List<Observation> observations;
    @JsonGetter("observations")
    public List<Observation> getObservations() {
        return observations;
    }
    @JsonGetter("observations")
    public void setObservations(List<Observation> observations) {
        if(observations!= null)
            observations.forEach(o->o.setFeatureOfInterest(this));
        if (this.observations == null ) {
            this.observations=observations;
            return;
        }
        this.observations.addAll(observations);
    }
    public void addObservations(Observation observation) {
        if(observations==null)
            observations= new ArrayList<>();
        this.observations.add(observation);
    }


    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public String getObservationsNavigationLink() {
        return "FeatureOfInterest("+id+")/Observations";
    }

}
