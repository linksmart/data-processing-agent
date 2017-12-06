package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateSerializer;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.DatastreamImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.links.ObservationsNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.ObservedPropertyNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.SensorNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.ThingNavigationLink;
import org.geojson.Polygon;

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
 *      A Datastream groups a collection of Observations measuring the same ObservedProperty and produced by the same Sensor.
 *
 *  @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#28"> OGC Sensor Things Part I: Datastream Definition </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */

@JsonDeserialize(as = DatastreamImpl.class)
@JsonSerialize(as = DatastreamImpl.class)
public interface Datastream extends CommonControlInfoDescription, ThingNavigationLink,ObservationsNavigationLink,SensorNavigationLink, ObservedPropertyNavigationLink {


    /**
     * Adds a single {@link Observation} to the set of events belonging to
     * this {@link Datastream} instance.
     *
     * @param observation
     *            The observation to add.
     */
    void addObservation(Observation observation);

    /**
     * Removes a single {@link Observation} from the set of events
     * belonging to this {@link Datastream} instance.
     *
     * @param observation
     *            The observation to remove.
     * @return true if removal was successful, false otherwise
     */
    boolean removeObservation(Observation observation);

    /**
     * Provides the {@link ObservedProperty} instance describing the property to
     * which events belonging to this data stream belong.
     *
     * @return the observedProperty
     */
    @JsonPropertyDescription("TBD")
    @JsonGetter("observedProperty")
    ObservedProperty getObservedProperty();

    /**
     * Sets the {@link ObservedProperty} instance describing the property to
     * which events belonging to this data stream belong.
     *
     * @param observedProperty
     *            the observedProperty to set
     */
    @JsonPropertyDescription("The Observations of a Datastream SHALL observe the same ObservedProperty. The Observations of different Datastreams MAY observe the same ObservedProperty.")
    @JsonSetter("observedProperty")
    void setObservedProperty(ObservedProperty observedProperty);
    /**
     * Gets the observation type time as a String.
     * The type of Observation (with unique result type), which is used by the service to encode observations.
     *
     * @return the observation type time as String.
     *
     * */
    @JsonPropertyDescription("The type of Observation (with unique result type), which is used by the service to encode observations.")
    @JsonGetter(value = "observationType")
    String getObservationType();

    /**
     * Sets the observation type with the given String.
     * The type of Observation (with unique result type), which is used by the service to encode observations.
     *
     * @param  observationType sets the observation type.
     *
     * */
    @JsonPropertyDescription("The type of Observation (with unique result type), which is used by the service to encode observations.")
    @JsonSetter(value = "observationType")
    void setObservationType(String observationType);
    /**
     * Gets the unit of measurement time as a Map Key / Value
     * A JSON Object containing three key-value pairs. The name property presents the full name of the unitOfMeasurement; the symbol property shows the textual form of the unit symbol; and the definition contains the URI defining the unitOfMeasurement.
     *
     * @return the unit of measurement as Map.
     *
     * */
    @JsonPropertyDescription("A JSON Object containing three key-value pairs. The name property presents the full name of the unitOfMeasurement; the symbol property shows the textual form of the unit symbol; and the definition contains the URI defining the unitOfMeasurement.")
    @JsonGetter(value = "unitOfMeasurement")
    Map<String,Object> getUnitOfMeasurement();

    /**
     * Sets the unit of measurement with the given Map of Key / Value.
     * A JSON Object containing three key-value pairs. The name property presents the full name of the unitOfMeasurement; the symbol property shows the textual form of the unit symbol; and the definition contains the URI defining the unitOfMeasurement.
     *
     * @param  unitOfMeasurement sets the Map.
     *
     * */
    @JsonPropertyDescription("A JSON Object containing three key-value pairs. The name property presents the full name of the unitOfMeasurement; the symbol property shows the textual form of the unit symbol; and the definition contains the URI defining the unitOfMeasurement.")
    @JsonSetter(value = "unitOfMeasurement")
    void setUnitOfMeasurement(Map<String, Object> unitOfMeasurement);
    /**
     * Gets the phenomenon time as a Interval.
     * The temporal interval of the phenomenon times of all observations belonging to this Datastream.
     *
     * @return the phenomenon time as Interval.
     *
     * */
    @JsonPropertyDescription("The temporal interval of the phenomenon times of all observations belonging to this Datastream.")
    @JsonGetter(value = "phenomenonTime")
    @JsonSerialize(using = IntervalDateSerializer.class)
    Interval getPhenomenonTime();

    /**
     * Sets the phenomenon time with the given Interval.
     * The temporal interval of the phenomenon times of all observations belonging to this Datastream.
     *
     * @param  phenomenonTime sets the phenomenon time Interval.
     *
     * */
    @JsonPropertyDescription("The temporal interval of the phenomenon times of all observations belonging to this Datastream.")
    @JsonSetter(value = "phenomenonTime")
    @JsonDeserialize(using = IntervalDateDeserializer.class)
    void setPhenomenonTime(Interval phenomenonTime);
    /**
     * Gets the result time as a Interval.
     * The temporal interval of the result times of all observations belonging to this Datastream.
     *
     * @return the phenomenon time as Interval.
     *
     * */
    @JsonPropertyDescription("The temporal interval of the result times of all observations belonging to this Datastream.")
    @JsonGetter(value = "resultTime")
    @JsonSerialize(using = IntervalDateSerializer.class)
    Interval getResultTime();

    /**
     * Sets the result time with the given Interval.
     *  The temporal interval of the result times of all observations belonging to this Datastream.
     *
     * @param  resultTime sets the phenomenon time.
     *
     * */
    @JsonPropertyDescription("The temporal interval of the result times of all observations belonging to this Datastream.")
    @JsonSetter(value = "resultTime")
    @JsonDeserialize(using = IntervalDateDeserializer.class)
    void setResultTime(Interval resultTime);
    /**
     * Gets the observed area as a Polygon {@link Polygon}.
     * The spatial bounding box of the spatial extent of all FeaturesOfInterest that belong to the Observations associated with this Datastream.
     *
     * @return the observed area as Polygon.
     *
     * */
    @JsonPropertyDescription("The spatial bounding box of the spatial extent of all FeaturesOfInterest that belong to the Observations associated with this Datastream.")
    @JsonGetter(value = "observedArea")
    Polygon getObservedArea();

    /**
     * Sets the observed area as a Polygon {@link Polygon}.
     * The spatial bounding box of the spatial extent of all FeaturesOfInterest that belong to the Observations associated with this Datastream.
     *
     * @param  observedArea sets theobserved area.
     *
     * */
    @JsonPropertyDescription("The spatial bounding box of the spatial extent of all FeaturesOfInterest that belong to the Observations associated with this Datastream.")
    @JsonSetter(value = "observedArea")
    void setObservedArea(Polygon observedArea);
    /**
     * Gets the observations as a List of observations {@link Observation}.
     * A Datastream has zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.
     *
     * @return the observations as a List of observations
     *
     * */
    @JsonPropertyDescription("A Datastream has zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.")
    @JsonGetter("observations")
    List<Observation> getObservations();
    /**
     * Sets the observations as a List of observations {@link Observation}.
     *A Datastream has zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.
     *
     * @param  observation sets the observations.
     *
     * */
    @JsonPropertyDescription("A Datastream has zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.")
    @JsonSetter("observations")
    void setObservations(List<Observation> observation);
    /**
     * Gets the sensor as a Sensor {@link Sensor}.
     * The Observations in a Datastream are performed by one-and-only-one Sensor. One Sensor MAY produce zero-to-many Observations in different Datastreams.
     *
     * @return the sensor as a Sensor.
     *
     * */
    @JsonPropertyDescription("The Observations in a Datastream are performed by one-and-only-one Sensor. One Sensor MAY produce zero-to-many Observations in different Datastreams.")
    @JsonGetter(value = "sensor")
    Sensor getSensor();
    /**
     * Sets the sensor as a Sensor {@link Sensor}.
     * The Observations in a Datastream are performed by one-and-only-one Sensor. One Sensor MAY produce zero-to-many Observations in different Datastreams.
     *
     * @param  sensor the sensor to set as a Sensor.
     *
     * */
    @JsonPropertyDescription("The Observations in a Datastream are performed by one-and-only-one Sensor. One Sensor MAY produce zero-to-many Observations in different Datastreams.")
    @JsonSetter(value = "sensor")
    void setSensor(Sensor sensor);
    /**
     * Gets the thing as a Thing {@link Thing}.
     * A Thing has zero-to-many Datastreams. A Datastream entity SHALL only link to a Thing as a collection of Observations.
     *
     * @return the thing.
     *
     * */
    @JsonPropertyDescription("A Thing has zero-to-many Datastreams. A Datastream entity SHALL only link to a Thing as a collection of Observations.")
    @JsonGetter("thing")
    Thing getThing();
    /**
     *  Sets the sensor as a Thing {@link Thing}.
     * A Thing has zero-to-many Datastreams. A Datastream entity SHALL only link to a Thing as a collection of Observations.
     *
     * @param  thing sets the thing.
     *
     * */
    @JsonPropertyDescription("A Thing has zero-to-many Datastreams. A Datastream entity SHALL only link to a Thing as a collection of Observations.")
    @JsonSetter(value = "thing")
    void setThing(Thing thing);



}
