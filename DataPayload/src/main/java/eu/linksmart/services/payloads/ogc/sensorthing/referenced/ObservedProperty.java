package eu.linksmart.services.payloads.ogc.sensorthing.referenced;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
public class ObservedProperty extends eu.linksmart.services.payloads.ogc.sensorthing.ObservedProperty {

    /**navigationLink is the relative URL that retrieves content of related entities. */
    @JsonPropertyDescription("navigationLink is the relative Datastreams that retrieves content of related entities.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public String getDatastreamsNavigationLink() {
        return "ObservedProperty("+id+")/Datastreams";
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public void setDatastreamsNavigationLink(String value) {   }

}
