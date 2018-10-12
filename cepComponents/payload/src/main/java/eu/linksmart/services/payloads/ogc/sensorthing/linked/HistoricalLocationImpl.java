package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.HistoricalLocation;
import eu.linksmart.services.payloads.ogc.sensorthing.Location;
import eu.linksmart.services.payloads.ogc.sensorthing.Thing;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateSerializer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 01.04.2016 a researcher of Fraunhofer FIT.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = HistoricalLocation.class)
public class HistoricalLocationImpl extends CommonControlInfoImpl implements HistoricalLocation {


    @JsonIgnore
    List<Location> locations;
    @JsonIgnore
    protected List<Thing> things;
    @JsonIgnore
    protected Date time;

    @Override
    public List<Location> getLocations() {
        return locations;
    }

    @Override
    public void setLocations(List<Location> locations) {
        if(locations!=null) {
            locations.forEach(d->d.addHistoricalLocation(this));
            this.locations = locations;
        }

    }
    @Override
    public void addLocation(Location locations) {
        if(locations.getHistoricalLocations()==null)
            locations.setHistoricalLocations( new ArrayList<>());
        if(!locations.getHistoricalLocations().contains(this))
            locations.getHistoricalLocations().add(this);
        if(!this.locations.contains(locations))
            this.locations.add(locations);
    }


    @Override
    public void addThing(Thing thing){
        if(thing.getHistoricalLocations()==null)
            thing.setHistoricalLocations( new ArrayList<>());

        if(!thing.getHistoricalLocations().contains(this))
            thing.addHistoricalLocation(this);
        if(!things.contains(thing))
            this.things.add(thing);
    }



    @Override
    public Date getTime() {
        return time;
    }

    @Override
    public void setTime(Date time) {
        this.time = time;
    }
    public List<Thing> getThings() {
        return things;
    }

    public void setThings(List<Thing> things) {
        this.things = things;
    }
}
