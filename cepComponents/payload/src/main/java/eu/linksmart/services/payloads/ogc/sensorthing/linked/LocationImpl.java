/*
 * OGC SensorThings API - Data Model
 * 
 * Copyright (c) 2015 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.linksmart.services.payloads.ogc.sensorthing.HistoricalLocation;
import eu.linksmart.services.payloads.ogc.sensorthing.Location;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.Thing;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CCIEncodingImpl;
import org.geojson.GeoJsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * <strong>Definition:</strong> A location is an absolute geographical position
 * at a specific time point.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = Location.class)
public class LocationImpl extends CCIEncodingImpl implements Location {

    protected List<HistoricalLocation> historicalLocations;

    public List<Thing> getThings() {
        return things;
    }

    public void setThings(List<Thing> things) {
        this.things = things;
    }


    @JsonIgnore
    protected List<Thing> things;

    @Override
    public void addThing(Thing thing){
        if(thing.getLocations() == null)
            thing.setLocations(new ArrayList<>());
        if(!thing.getLocations().contains(this))
            thing.addLocation(this);
        if(!things.contains(thing))
            this.things.add(thing);
    }

    @Override
    public List<HistoricalLocation> getHistoricalLocations() {
        return historicalLocations;
    }


    @Override
    public void setHistoricalLocations(List<HistoricalLocation> historicalLocations) {
        if(historicalLocations!=null) {
            historicalLocations.forEach(d->d.addLocation(this));
            this.historicalLocations = historicalLocations;
        }

    }
    @Override
    public void addHistoricalLocation(HistoricalLocation historicalLocation) {

        if(historicalLocation.getLocations() == null)
            historicalLocation.setLocations(new ArrayList<>());

        if(!historicalLocation.getLocations().contains(this))
            historicalLocation.getLocations().add(this);
        if(this.historicalLocations == null)
            this.historicalLocations = new ArrayList<>();

        if(!this.historicalLocations.contains(historicalLocation))
            this.historicalLocations.add(historicalLocation);
    }

    @JsonIgnore
    protected GeoJsonObject location;
	/* {see=http://blog.opendatalab.de/hack/2013/07/16/geojson-jackson/} */

    /**
     * Empty constructor, respects the bean instantiation pattern.
     */
    public LocationImpl()
    {
        this("",null);
    }

    /**
     * Builds a new location object, referred to a specific time instant
     * (expressed as a {@link java.util.Date} instance) and having the given location (as
     * a {@link org.geojson.GeoJsonObject}).
     *
     * @param location
     *            The location of the location identified by this instance.
     */
    public LocationImpl(GeoJsonObject location)
    {
        this("",location);
    }
    /**
     * Builds a new location object, referred to a specific time instant
     * (expressed as a {@link java.util.Date} instance) and having the given location (as
     * a {@link org.geojson.GeoJsonObject}).
     *
     * @param location
     *            The location of the location identified by this instance.
     */
    public LocationImpl(String description,GeoJsonObject location)
    {
        this.description = description;
        this.location = location;
    }

    public GeoJsonObject getLocation()
    {
        return location;
    }

    @Override
    public void setLocation(GeoJsonObject location)
    {
        this.location = location;
    }

    @Override
    public String toString(){
        if(location!=null)
            return "ID: "+id+"; Description: "+description+"; "+"Location: "+ Arrays.toString(location.getBbox());
        return super.toString();
    }
    @Override
    public int hashCode(){
        return location.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Location && Arrays.equals(((LocationImpl) obj).location.getBbox(),location.getBbox());
    }
}