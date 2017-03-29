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
package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CCIEncoding;


/**
 * <strong>Definition:</strong> A sensor is an instrument that can observe a
 * property or phenomenon with the goal of producing an estimate of the value of
 * the property. In some cases, the sensor in this data model can also be seen
 * as the procedure (method, algorithm, or instrument) defined in OGC 07-022r1.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Sensor extends CCIEncoding
{

    /**
     * The detailed description of the sensor or system. The content is open to
     * accommodate changes to SensorML or to support other description
     * languages.
     */
    @JsonPropertyDescription("The detailed description of the sensor or system. The content is open to accommodate changes to SensorML or to support other description languages.")
    @JsonProperty(value = "metadata")
    protected String metadata;





  //  protected String datastreamsNavigationLink=null;

    public Sensor(String description, String metadata) {
        super(description);
        this.metadata = metadata;
    }
    public Sensor(String description, String encoding, String metadata) {
        super(description, encoding);
        this.metadata = metadata;
    }
    public Sensor(String metadata) {
        this.metadata = metadata;
    }
    public Sensor(){
        this.metadata=null;
    }

    /**
     * Provides back the metadata describing this {@link Sensor} instance.
     *
     * @return The metadata as a {@link String}. It contains the detailed
     *         description of the sensor or system. The content is open to
     *         accommodate changes to SensorML or to support other description
     *         languages.
     */
    @JsonProperty(value = "metadata")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    public String getMetadata()
    {
        return metadata;
    }

    /**
     * Sets the metadata describing this {@link Sensor} instance.
     *
     * @param metadata
     *            the metadata to set, it contains the detailed description of
     *            the sensor or system. The content is open to accommodate
     *            changes to SensorML or to support other description languages.
     */
    @JsonProperty(value = "metadata")
    @JsonPropertyDescription("The detailed description of the Sensor or system. The metadata type is defined by encodingType.")
    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }


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