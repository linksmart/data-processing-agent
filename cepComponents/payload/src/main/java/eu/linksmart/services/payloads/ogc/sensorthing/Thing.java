package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ThingImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.links.DatastreamsNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.HistoricalLocationsNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.LocationsNavigationLink;

import java.util.List;
import java.util.Map;
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
 *      The OGC SensorThings API follows the ITU-T definition, i.e., with regard to the Internet of Things, a thing is an object of the physical world (physical things) or the information world (virtual things) that is capable of being identified and integrated into communication networks [ITU-T Y.2060].
 *
 *  @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#25"> OGC Sensor Things Part I: Thing Definition  </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */
@JsonDeserialize(as = ThingImpl.class)
@JsonSerialize(as = ThingImpl.class)
public interface Thing extends CommonControlInfoDescription, DatastreamsNavigationLink,HistoricalLocationsNavigationLink, LocationsNavigationLink{
    /**
     * Gets the properties of the Thing
     * The properties of a thing is a Map Object containing user-annotated properties as key-value pairs.
     *
     * @return  propeties assosiated to this thing
     *
     */
    @JsonGetter(value = "properties")
    @JsonPropertyDescription("A Map Object containing user-annotated properties as key-value pairs.")
    Map<String,Object> getProperties();
    /**
     * Sets the properties of the Thing
     * The properties of a thing is a Map containing user-annotated properties as key-value pairs.
     *
     * @param properties as a map of String/Object
     *
     */
    @JsonSetter(value = "properties")
    @JsonPropertyDescription("A Map Object containing user-annotated properties as key-value pairs.")
    void setProperties(Map<String, Object> properties);
    /**
     * Add a single properties to the map of properties using the key and the value
     *
     * @param key the value that address the property
     * @param property the value to be added by the address of the key
     *
     */
    void addProperty(String key, Object property);

    /**
     * Provides the list of datastreams generated by this Thing. The returned
     * set is Live reference to the internal data structure which is not
     * Thread-safe. Synchronization and concurrent modification issues might
     * arise in multi-threaded environments.
     *
     * @return the {@link List}:{@link Datastream}  of datastreams generated by
     *         this {@link Thing} instance.
     */
    @JsonPropertyDescription("List of datastreams generated by this Thing.")
    @JsonGetter("datastreams")
    List<Datastream> getDatastreams();
    /**
     * Sets the list of datastreams generated by this thing. Removes any list
     * previously existing.
     *
     * @param datastreams
     *            the datastreams to set.
     */
    @JsonPropertyDescription("List of datastreams generated by this Thing.")
    @JsonSetter("datastreams")
    void setDatastreams(List<Datastream> datastreams);
    /**
     * Add a single datastream to the list of datastreams generated by this
     * {@link Thing} instance.
     *
     * @param datastream
     *            The {@link Datastream} instance to add.
     */
    void addDatastreams(Datastream datastream);

    /**
     * Not part of the standard. Provide the Datastream with the given ID if exist
     *
     * @return the {@link Datastream}  with given ID if exists, null otherwise
     */
    @JsonIgnore
    Datastream getDatastream(Object id);

    /**
     * Not part of the standard. True if the given Datastream ID if exist
     *
     * @return <code>true</code>  if given Datastream ID exists, <code>false</code> otherwise
     */
    @JsonIgnore
    boolean containsDatastreams(Object id);

    /**
     * Provides the list of locations in which this Thing has been registered.
     * The returned set is Live reference to the internal data structure which
     * is not Thread-safe. Synchronization and concurrent modification issues
     * might arise in multi-threaded environments.
     *
     * @return the locations
     */
    @JsonPropertyDescription("The list of locations in which this Thing has been registered.")
    @JsonGetter("historicalLocations")
    List<HistoricalLocation> getHistoricalLocations();

    /**
     * Sets the list of locations in which this Thing has been registered.
     * Replaces any existing list.
     *
     * @param historicalLocations
     *            the locations to set
     */
    @JsonPropertyDescription("the list of locations in which this Thing has been registered.")
    @JsonSetter("historicalLocations")
    void setHistoricalLocations(List<HistoricalLocation> historicalLocations);
    /**
     * Adds one historical location to the list of historicalLocations in which this Thing has been
     * registered.
     *
     * @param historicalLocation an historical location to add in this thing
     */
    void addHistoricalLocation(HistoricalLocation historicalLocation);

    /**
     * Removes one location from the set of locations in which this
     * {@link Thing} instance was positioned.
     *
     * @param historicalLocations
     *            The location to remove.
     * @return true if removal is successful, false otherwise.
     */
    boolean removeHistoricalLocations(HistoricalLocation historicalLocations);
    /**
     * Provides the The list of locations in which this Thing is currently registered.
     * The returned set is Live reference to the internal data structure which
     * is not Thread-safe. Synchronization and concurrent modification issues
     * might arise in multi-threaded environments.
     *
     * @return the locations
     */
    @JsonPropertyDescription("The list of locations in which this Thing is currently registered")
    @JsonGetter("locations")
    List<Location> getLocations();

    /**
     * Sets the list of locations in which this Thing is currently registered.
     * Replaces any existing list.
     *
     * @param locations
     *            the locations to set
     */
    @JsonPropertyDescription("The list of locations in which this Thing is currently registered")
    @JsonSetter("locations")
    void setLocations(List<Location> locations);
    /**
     * Adds one location to the list of Locations in which this Thing has been
     * registered.
     *
     * @param location a location where this thing is located
     */
    void addLocation(Location location);


    void removeDatastream(Object id);
}
