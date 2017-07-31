package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservedPropertyImpl;
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
 *     An ObservedProperty specifies the phenomenon of an Observation.
 *
 *  @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#30" </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */
@JsonDeserialize(as = ObservedPropertyImpl.class)
@JsonSerialize(as = ObservedPropertyImpl.class)
public interface ObservedProperty extends CommonControlInfoDescription, DatastreamsNavigationLink {

    /**
     * Provides the URN(URI) of the observed property or phenomenon modeled by
     * this instance.
     *
     * @return the uri The phenomenon URI as a {@link String}
     */
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    @JsonGetter("definition")
    String getDefinition();

    /**
     * Sets the URN(URI) of the observed property or phenomenon modeled by this
     * instance.
     *
     * @param uri
     *            the uri to set.
     */
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    @JsonSetter("definition")
    void setDefinition(String uri);

    /**
     * Gets the related Datastreams of this Observed property .
     *
     * {@link Datastream} The Observations of a Datastream observe the same ObservedProperty. The Observations of different Datastreams MAY observe the same ObservedProperty.
     *
     * @return Datastreams of this Observed property
     *
     * */
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    @JsonGetter("datastreams")
    List<Datastream> getDatastreams();
    /**
     * Sets the related Datastreams of this Observed property.
     *
     * {@link Datastream} The Observations of a Datastream observe the same ObservedProperty. The Observations of different Datastreams MAY observe the same ObservedProperty.
     *
     * @param  datastreams to be set in this Observed property
     *
     * */
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    @JsonSetter("datastreams")
    void setDatastreams(List<Datastream> datastreams);
}
