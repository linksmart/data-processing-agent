package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.FeatureOfInterestImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.links.ObservationsNavigationLink;

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
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 *
 * In OGC SensorThing 1.0:
     * An Observation results in a value being assigned to a phenomenon.
     * The phenomenon is a property of a feature, the latter being the FeatureOfInterest of the Observation [OGC and ISO 19156:2011].
     * In the context of the Internet of Things, many Observations’ FeatureOfInterest can be the Location of the Thing.
     * For example, the FeatureOfInterest of a wifi-connect thermostat can be the Location of the thermostat (i.e., the living room where the thermostat is located in).
     * In the case of remote sensing, the FeatureOfInterest can be the geographical area or volume that is being sensed.
 *
 * @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#32">OGC Sensor Things Part I: Feature of Interest Definition  </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */

@JsonDeserialize(as = FeatureOfInterestImpl.class)
@JsonSerialize(as = FeatureOfInterestImpl.class)
public interface FeatureOfInterest extends CCIEncoding, ObservationsNavigationLink {
    /**
     * Provides the list of events about this {@link FeatureOfInterestImpl}
     * instance. The returned set is Live reference to the internal data
     * structure which is not Thread-safe. Synchronization and concurrent
     * modification issues might arise in multi-threaded environments.
     *
     * @return the events
     */
    @JsonPropertyDescription("An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.")
    @JsonGetter("observations")
    List<Observation> getObservations();

    /**
     * Sets the list of Observations about this
     * Removes any list previously existing.
     *
     * @param observations the events to set
     */
    @JsonPropertyDescription("An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.")
    @JsonSetter("observations")
    void setObservations(List<Observation> observations);

    /**
     * adds to the list of Observations about this
     * It ignore if the same observation already existed.
     *
     * @param observation the events to set
     */
    void addObservations(Observation observation);

    /**
     * Gets the feature as a Object.
     * The feature is the detailed description of the feature. The data type is defined by encodingType.
     *
     * @return the feature time as Object.
     *
     * */
    @JsonGetter(value = "feature")
    @JsonPropertyDescription(" The feature is the detailed description of the feature. The data type is defined by encodingType.")
    Object getFeature();
    /**
     * Sets the feature time with the given value.
     * The feature is the detailed description of the feature. The data type is defined by encodingType.
     *
     * @param  name sets to the given value.
     *
     * */
    @JsonSetter(value = "feature")
    @JsonPropertyDescription("The feature is the detailed description of the feature. The data type is defined by encodingType.")
    void setFeature(Object name);
}
