package eu.linksmart.services.payloads.ogc.sensorthing.referenced;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Created by José Ángel Carvajal on 01.04.2016 a researcher of Fraunhofer FIT.
 */
public class HistoricalLocation extends eu.linksmart.services.payloads.ogc.sensorthing.HistoricalLocation {



    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Thing@iot.navigationLink")
    public String getThingNavigationLink() {
        return "HistoricalLocation("+id+")/Thing";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Thing@iot.navigationLink")
    public void setThingNavigationLink(String value) {   }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public String getLocationsNavigationLink() {
        return "HistoricalLocation("+id+")/Locations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public void setLocationsNavigationLink(String value) {   }




}
