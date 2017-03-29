package eu.linksmart.services.payloads.ogc.sensorthing.referenced;

import com.fasterxml.jackson.annotation.*;


/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

public class Observation extends eu.linksmart.services.payloads.ogc.sensorthing.Observation {



    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastream@iot.navigationLink")
    public String getDatastreamNavigationLink() {
        return "Observation("+id+")/Datastream";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastream@iot.navigationLink")
    public void setDatastreamNavigationLink(String value) {   }



    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "FeatureOfInterest@iot.navigationLink")
    public String getFeatureOfInterestNavigationLink() {
        return "Observation("+id+")/FeatureOfInterest";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "FeatureOfInterest@iot.navigationLink")
    public void setFeatureOfInterestNavigationLink(String value) {   }




}
