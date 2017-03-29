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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.HashSet;
import java.util.Set;


/**
 * <strong>Definition:</strong> A sensor is an instrument that can observe a
 * property or phenomenon with the goal of producing an estimate of the value of
 * the property. In some cases, the sensor in this data model can also be seen
 * as the procedure (method, algorithm, or instrument) defined in OGC 07-022r1.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Sensor extends eu.linksmart.services.payloads.ogc.sensorthing.Sensor
{


    /**
     * A thing can have zero-to-many datastreams. A datastream entity can only
     * link to a thing as a collection of events or properties.
     */
    @JsonBackReference(value = "datastreams")
    @JsonDeserialize(as=HashSet.class)
    protected Set<Datastream> datastreams;


    /**navigationLink is the relative URL that retrieves content of related entities. */
    @JsonPropertyDescription("navigationLink is the relative Datastreams that retrieves content of related entities.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public String getDatastreamsNavigationLink() {
        return "Sensor("+id+")/Datastreams";
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Datastreams@iot.navigationLink")
    public void setDatastreamsNavigationLink(String value) {   }
}