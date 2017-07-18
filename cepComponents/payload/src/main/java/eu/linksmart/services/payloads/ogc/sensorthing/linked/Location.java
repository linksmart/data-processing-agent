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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.Set;

/**
 * <strong>Definition:</strong> A location is an absolute geographical position
 * at a specific time point.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Location extends eu.linksmart.services.payloads.ogc.sensorthing.Location
{



	/* {see=http://blog.opendatalab.de/hack/2013/07/16/geojson-jackson/} */

   /* @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Things@iot.navigationLink")
    public String getThingsNavigationLink() {
        return "Location("+id+")/Things";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Things@iot.navigationLink")
    public void setThingsNavigationLink(String value) {   }


    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "HistoricalLocations@iot.navigationLink")
    public String getHistoricalLocationsNavigationLink() {
        return "Location("+id+")/HistoricalLocations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "HistoricalLocations@iot.navigationLink")
    public void setHistoricalLocationsNavigationLink(String value) {   }

    @JsonBackReference(value = "things")
    protected Set<Thing> Things = null;
*/

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



}