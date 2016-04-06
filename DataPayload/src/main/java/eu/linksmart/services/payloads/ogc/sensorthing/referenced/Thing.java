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
package eu.linksmart.services.payloads.ogc.sensorthing.referenced;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;

import java.util.Set;

/**
 * Thing class.
 * <p>
 * <strong>Definition:</strong> We use the ITU-T definition, i.e., with regard
 * to the Internet of Things, a thing is an object of the physical world
 * (physical things) or the information world (virtual things) which is capable
 * of being identified and integrated into communication networks. (ITU-T
 * Y.2060)
 * </p>
 * 
 * @author <a href="mailto:carvajal@fit.fhg.de">Angel Carvajal</a>
 *
 */
public class Thing extends eu.linksmart.services.payloads.ogc.sensorthing.Thing
{



    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "HistoricalLocations@iot.navigationLink")
    public String getHistoricalLocationsNavigationLink() {
        return "Location("+id+")/HistoricalLocations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "HistoricalLocations@iot.navigationLink")
    public void setHistoricalLocationsNavigationLink(String value) {   }
	/**
	 * A thing can have zero-to-many datastreams. A datastream entity can only
	 * link to a thing as a collection of observations or properties.
	 */
    /**
     * A thing can have zero-to-many datastreams. A datastream entity can only
     * link to a thing as a collection of observations or properties.
     */

    @JsonProperty(value = "datastreams")
    private Set<Datastream> datastreams;

    /**navigationLink is the relative URL that retrieves content of related entities. */
    @JsonPropertyDescription("navigationLink is the relative Datastreams that retrieves content of related entities.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public String getDatastreamsNavigationLink() {
        return "Thing("+id+")/Datastreams";
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public void setDatastreamsNavigationLink(String value) {   }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public String getLocationsNavigationLink() {
        return "Thing("+id+")/Locations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Locations@iot.navigationLink")
    public void setLocationsNavigationLink(String value) {}




}