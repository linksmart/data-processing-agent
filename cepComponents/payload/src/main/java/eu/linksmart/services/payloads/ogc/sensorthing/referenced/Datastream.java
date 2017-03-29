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

/**
 * <strong>Definition:</strong> A datastream groups a collection of events
 * that are related in some way. The one constraint is that the events in
 * a datastream must measure the same observed property (i.e., one phenomenon).
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Datastream extends eu.linksmart.services.payloads.ogc.sensorthing.Datastream
{




    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Sensor@iot.navigationLink")
    public String getSensorNavigationLink() {
        return "Datastream("+id+")/Sensor";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Sensor@iot.navigationLink")
    public void setSensorNavigationLink(String value) {   }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Thing@iot.navigationLink")
    public String getThingNavigationLink() {
        return "Datastream("+id+")/Thing";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Thing@iot.navigationLink")
    public void setThingNavigationLink(String value) {   }

    @JsonProperty(value = "Observations@iot.navigationLink")
    public String getObservationsNavigationLink() {
        return "Datastream("+id+")/Observations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public void setObservationsNavigationLink(String value) {   }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "ObservedProperty@iot.navigationLink")
    public String getObservedPropertNavigationLink() {
        return "Datastream("+id+")/ObservedProperty";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "ObservedProperty@iot.navigationLink")
    public void setObservedPropertyNavigationLink(String value) {   }
}