package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="objectID")

//@JsonIgnoreProperties({"@iot.id", "@iot.selfLink"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@iot.id", scope = FeatureOfInterest.class)
public class FeatureOfInterest extends eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest {


    /** TBD. */
   // @OneToMany(mappedBy="featureOfInterest", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JsonBackReference
    protected List<Observation> observations;


    public List<Observation> getObservations() {
        return observations;
    }

    public void setObservations(List<Observation> observations) {
        if(observations==null)
            observations= new ArrayList<>();
        this.observations = observations;
    }
    public void addObservations(Observation observation) {
        if(observations==null)
            observations= new ArrayList<>();
        this.observations.add(observation);
    }



  //  @JsonProperty(value = "selfLink")
   // protected long selfLink;

    //protected String observationsNavigationLink;
   /* @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public String getObservationsNavigationLink() {
        return "FeatureOfInterest("+id+")/Observations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public void setObservationsNavigationLink(String value) {   }*/

}
