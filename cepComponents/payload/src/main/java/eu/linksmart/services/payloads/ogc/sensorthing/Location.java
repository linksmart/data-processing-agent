package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.HistoricalLocation;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.LocationImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.links.HistoricalLocationsNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.ThingsNavigationLink;
import org.geojson.GeoJsonObject;

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
 *     The Location entity locates the Thing or the Things it associated with. A Thing’s Location entity is defined as the last known location of the Thing.
 *     A Thing’s Location may be identical to the Thing’s Observations’ FeatureOfInterest. In the context of the IoT, the principle location of interest is usually associated with the location of the Thing, especially for in-situ sensing applications. For example, the location of interest of a wifi-connected thermostat should be the building or the room in which the smart thermostat is located. And the FeatureOfInterest of the Observations made by the thermostat (e.g., room temperature readings) should also be the building or the room. In this case, the content of the smart thermostat’s location should be the same as the content of the temperature readings’ feature of interest.
 *
 *     However, the ultimate location of interest of a Thing is not always the location of the Thing (e.g., in the case of remote sensing). In those use cases, the content of a Thing’s Location is different from the content of theFeatureOfInterestof the Thing’s Observations. Section 7.1.4 of [OGC 10-004r3 and ISO 19156:2011] provides a detailed explanation of observation location.
 *
 *  @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#26" </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */
@JsonDeserialize(as = LocationImpl.class)
@JsonSerialize(as = LocationImpl.class)
public interface Location extends CCIEncoding, HistoricalLocationsNavigationLink, ThingsNavigationLink {
    void addThing(Thing thing);
    /**
     * gets the list of locations in which this Thing has been registered.
     * Replaces any existing list.
     *
     * @return  historicalLocations
     *            the locations to set
     */
    @JsonPropertyDescription("A Location can have zero-to-many HistoricalLocations. One HistoricalLocation SHALL have one or many Locations.")
    @JsonGetter("historicalLocations")
    List<HistoricalLocation> getHistoricalLocations();

    /**
     * Sets the list of locations in which this Thing has been registered.
     * Replaces any existing list.
     *
     * @param historicalLocations
     *            the locations to set
     */
    @JsonPropertyDescription("A Location can have zero-to-many HistoricalLocations. One HistoricalLocation SHALL have one or many Locations.")
    @JsonSetter("historicalLocations")
    void setHistoricalLocations(List<HistoricalLocation> historicalLocations);
    /**
     * adds on location to the list of locations in which this Thing has been registered.
     * Ignore if repeated.
     *
     * @param historicalLocation a location
     */
    void addHistoricalLocation(HistoricalLocation historicalLocation);

    /**
     * Provides the absolute geographical position of the location described by
     * this Location instance. Generally a GeoJSON location object
     *
     * @return the location The location location as a {@link org.geojson.GeoJsonObject}
     */
    @JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON location object")
    @JsonGetter(value = "location")
    GeoJsonObject getLocation();

    /**
     * Sets the absolute geographical position of the location described by this
     * Location instance. Generally a GeoJSON location object.
     *
     * @param location
     *            The location location as a {@link GeoJsonObject}
     */
    @JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON location object")
    @JsonSetter(value = "location")
    void setLocation(GeoJsonObject location);
    /**
     * Gets the things as a Array of Thing {@link Thing}.
     * Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.
     *
     * @return the things.
     *
     * */
    @JsonPropertyDescription("Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.")
    @JsonGetter(value = "things")
    List<Thing> getThings();
    /**
     *  Sets the sensor as a Array of Thing  {@link Thing}.
     * Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.
     *
     * @param  things sets the things.
     *
     * */
    @JsonPropertyDescription("Multiple Things MAY locate at the same Location. A Thing MAY not have a Location.")
    @JsonSetter(value = "things")
    void setThings(List<Thing> things);
}
