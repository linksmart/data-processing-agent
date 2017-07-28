package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.HistoricalLocationImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.links.LocationsNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.ThingNavigationLink;

import java.util.Date;
import java.util.List;

/*
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
/**
 *
 * In OGC SensorThing 1.0:
 *     A Thing’s HistoricalLocation entity set provides the times of the current (i.e., last known) and previous locations of the Thing.
 *
 *  @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#27" </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */
@JsonDeserialize(as = HistoricalLocationImpl.class)
@JsonSerialize(as = HistoricalLocationImpl.class)
public interface HistoricalLocation extends CommonControlInfo, ThingNavigationLink, LocationsNavigationLink{
    /**
     * Provides the The list of locations in which this Thing is currently registered.
     * The returned set is Live reference to the internal data structure which
     * is not Thread-safe. Synchronization and concurrent modification issues
     * might arise in multi-threaded environments.
     *
     * @return the locations
     */
    @JsonPropertyDescription("A Location can have zero-to-many HistoricalLocations. One HistoricalLocation SHALL have one or many Locations.")
    @JsonGetter("locations")
    List<Location> getLocations();
    /**
     * Sets the list of locations in which this Thing is currently registered.
     * Replaces any existing list.
     *
     * @param locations
     *            the locations to set
     */
    @JsonPropertyDescription("A Location can have zero-to-many HistoricalLocations. One HistoricalLocation SHALL have one or many Locations.")
    @JsonSetter("locations")
    void setLocations(List<Location> locations);
    /**
     * Adds one location to the list of Locations in which this Thing has been
     * registered.
     *
     * @param location
     */
    void addLocation(Location location);
    /**
     * adds one thing was in the list of thing of historical locations.
     * Ignore if repeated.
     *
     * @param thing a Thing in this location
     */
    void addThing(Thing thing);
    /**
     * The time when the Thing is known at the Location.
     *
     * @return the locations
     */
    @JsonPropertyDescription("Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.")
    @JsonGetter("time")
    Date getTime();
    /**
     * The time when the Thing is known at the Location.
     * Replaces any existing list.
     *
     * @param time the thing was in this location
     */
    @JsonPropertyDescription("Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.")
    @JsonSetter("time")
    void setTime(Date time);
    /**
     * Gets the things as a Array of Thing {@link Thing}.
     * Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.
     *
     * @return the things.
     *
     * */
    @JsonPropertyDescription("A HistoricalLocation has one-and-only-one Thing. One Thing MAY have zero-to-many HistoricalLocations.")
    @JsonGetter(value = "things")
    List<Thing> getThings();
    /**
     *  Sets the sensor as a Array of Thing  {@link Thing}.
     * Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.
     *
     * @param  things sets the things.
     *
     * */
    @JsonPropertyDescription("A HistoricalLocation has one-and-only-one Thing. One Thing MAY have zero-to-many HistoricalLocations.")
    @JsonSetter(value = "things")
    void setThings(List<Thing> things);
}
