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
import eu.linksmart.services.payloads.ogc.sensorthing.Thing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <strong>Definition:</strong> A location is an absolute geographical position
 * at a specific time point.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@iot.id", scope = Location.class)
public class Location extends eu.linksmart.services.payloads.ogc.sensorthing.Location
{

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected List<HistoricalLocation> historicalLocations;


    /**
     * The Location entity locates the Thing. Multiple Things MAY be
     * located at the same Location. A Thing MAY not have a
     * Location. A Thing SHOULD have only one Location.
     * However, in some complex use cases, a Thing MAY have more
     * than one Location representations. In such case, the Thing MAY
     * have more than one Locations. These Locations SHALL have
     * different encodingTypes and the encodingTypes SHOULD be in
     * different spaces (e.g., one encodingType in Geometrical space and
     * one encodingType in Topological space).
     **/
  //  @JsonBackReference(value = "historicalLocations")
   // protected Set<HistoricalLocation> historicalLocations;
    /**navigationLink is the relative URL that retrieves content of related entities. */

    @JsonIgnore
    protected List<Thing> things;

    public void addThing(Thing thing){
        if(thing.getLocations() == null)
            thing.setLocations(new ArrayList<>());
        if(!thing.getLocations().contains(this))
            thing.addLocation(this);
        this.things.add(thing);
    }

    @JsonGetter("historicalLocations")
    public List<HistoricalLocation> getHistoricalLocations() {
        return historicalLocations;
    }


    /**
     * Sets the list of locations in which this Thing has been registered.
     * Replaces any existing list.
     *
     * @param historicalLocations
     *            the locations to set
     */

    @JsonSetter("historicalLocations")
    public void setHistoricalLocations(List<HistoricalLocation> historicalLocations) {
        if(historicalLocations!=null) {
            historicalLocations.forEach(d->d.addLocation(this));
            this.historicalLocations = historicalLocations;
        }

    }
    public void addHistoricalLocation(HistoricalLocation historicalLocation) {

        if(historicalLocation.locations == null)
            historicalLocation.locations = new ArrayList<>();

        if(!historicalLocation.locations.contains(this))
            historicalLocation.locations.add(this);
        if(this.historicalLocations == null)
            this.historicalLocations = new ArrayList<>();

        if(!this.historicalLocations.contains(historicalLocation))
            this.historicalLocations.add(historicalLocation);
    }
    @JsonGetter(value = "HistoricalLocations@iot.navigationLink")
    public String getHistoricalLocationsNavigationLink() {
        return "Location("+id+")/HistoricalLocations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonSetter(value = "HistoricalLocations@iot.navigationLink")
    public void setHistoricalLocationsNavigationLink(String value) {   }

    @JsonGetter(value = "Things@iot.navigationLink")
    public String getThingNavigationLink() {
        return "Location("+id+")/Things";
    }
    //@JsonPropertyDescription("TBD.")
    @JsonSetter(value = "Things@iot.navigationLink")
    public void setThingNavigationLink(String value) {   }
}