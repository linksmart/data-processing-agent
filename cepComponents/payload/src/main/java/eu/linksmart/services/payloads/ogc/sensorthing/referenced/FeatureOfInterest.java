package eu.linksmart.services.payloads.ogc.sensorthing.referenced;

import com.fasterxml.jackson.annotation.*;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="objectID")
public class FeatureOfInterest extends eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest {






    //protected String observationsNavigationLink;
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public String getObservationsNavigationLink() {
        return "FeatureOfInterest("+id+")/Observations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public void setObservationsNavigationLink(String value) {   }

}
