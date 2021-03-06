package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.SensorImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.links.DatastreamsNavigationLink;

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
 *       Sensor is an instrument that observes a property or phenomenon with the goal of producing an estimate of the value of the property[3].
 *
 *  @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#29"> OGC Sensor Things Part I: Sensor Definition  </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */
@JsonDeserialize(as = SensorImpl.class)
@JsonSerialize(as = SensorImpl.class)
public interface Sensor extends CCIEncoding, DatastreamsNavigationLink{
    /**
     * Provides the list of datastreams generated by this Thing. The returned
     * set is Live reference to the internal data structure which is not
     * Thread-safe. Synchronization and concurrent modification issues might
     * arise in multi-threaded environments.
     *
     * @return the {@link List}:{@link Datastream}  of datastreams generated by
     *         this {@link Thing} instance.
     */
    @JsonGetter("datastreams")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    List<Datastream> getDatastreams();
    /**
     * Sets the list of datastreams generated by this thing. Removes any list
     * previously existing.
     *
     * @param datastreams
     *            the datastreams to set.
     */
    @JsonSetter("datastreams")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    void setDatastreams(List<Datastream> datastreams);

    /**
     * Provides back the metadata describing this {@link Sensor} instance.
     *
     * @return The metadata as a {@link String}. It contains the detailed
     *         description of the sensor or system. The content is open to
     *         accommodate changes to SensorML or to support other description
     *         languages.
     */
    @JsonGetter(value = "metadata")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    Object getMetadata();

    /**
     * Sets the metadata describing this {@link Sensor} instance.
     *
     * @param metadata
     *            the metadata to set, it contains the detailed description of
     *            the sensor or system. The content is open to accommodate
     *            changes to SensorML or to support other description languages.
     */
    @JsonSetter(value = "metadata")
    @JsonPropertyDescription("The detailed description of the Sensor or system. The metadata type is defined by encodingType.")
    void setMetadata(Object metadata);

    void addDatastream(Datastream datastream);

}
