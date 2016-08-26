package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfo;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @JsonProperty(value = "thing")
    eu.linksmart.services.payloads.ogc.sensorthing.linked.Thing Thing;
    @JsonProperty(value = "thing")
    public eu.linksmart.services.payloads.ogc.sensorthing.linked.Thing getThing() {
        return Thing;
    }
    @JsonProperty(value = "thing")
    public void setThing(eu.linksmart.services.payloads.ogc.sensorthing.linked.Thing thing) {
        Thing = thing;
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public String getLocationsNavigationLink() {
        return "HistoricalLocation("+id+")/Locations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public void setLocationsNavigationLink(String value) {   }

    @JsonProperty(value = "locations")
    public List<Location> getLocations() {
        return locations;
    }

    @JsonProperty(value = "locations")
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }


    /**
     * A Location can have zero-to-many Locations. One HistoricalLocation SHALL have one or many Locations.
     * */


    @JsonDeserialize(using =DateDeserializer.class)
    @JsonSerialize(using = DateSerializer.class)
    @JsonProperty(value = "locations")
    List<Location> locations;

    public List<Location> getHistoricalLocations() {
        return locations;
    }

    public void setHistoricalLocations(List<Location> historicalLocations) {
        this.locations = historicalLocations;
    }




}
