package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.Thing;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 01.04.2016 a researcher of Fraunhofer FIT.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = HistoricalLocation.class)
public class HistoricalLocation extends eu.linksmart.services.payloads.ogc.sensorthing.HistoricalLocation {



    /**
     * A Location can have zero-to-many Locations. One HistoricalLocation SHALL have one or many Locations.
     * */


    @JsonIgnore
    List<Location> locations;
    @JsonGetter("locations")
    public List<Location> getLocations() {
        return locations;
    }

    @JsonSetter("locations")
    public void setLocations(List<Location> locations) {
        if(locations!=null) {
            locations.forEach(d->d.addHistoricalLocation(this));
            this.locations = locations;
        }

    }
    public void addLocation(Location locations) {
        if(locations.historicalLocations==null)
            locations.historicalLocations= new ArrayList<>();
        if(!locations.historicalLocations.contains(this))
            locations.historicalLocations.add(this);
        if(!this.locations.contains(locations))
            this.locations.add(locations);
    }

    @JsonIgnore
    protected List<Thing> things;

    public void addThing(Thing thing){
        if(thing.getHistoricalLocations()==null)
            thing.setHistoricalLocations( new ArrayList<>());

        if(!thing.getHistoricalLocations().contains(this))
            thing.addHistoricalLocation(this);
        this.things.add(thing);
    }
    @JsonGetter(value = "Locations@iot.navigationLink")
    public String getLocationsNavigationLink() {
        return "HistoricalLocation("+id+")/Locations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonSetter(value = "Locations@iot.navigationLink")
    public void setLocationsNavigationLink(String value) {   }

    @JsonGetter(value = "Thing@iot.navigationLink")
    public String getThingNavigationLink() {
        return "HistoricalLocation("+id+")/Things";
    }
    //@JsonPropertyDescription("TBD.")
    @JsonSetter(value = "Thing@iot.navigationLink")
    public void setThingNavigationLink(String value) {   }
}
