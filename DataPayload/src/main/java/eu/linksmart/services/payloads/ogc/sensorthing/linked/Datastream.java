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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateSerializer;
import org.geojson.Polygon;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    @JsonPropertyDescription("The detailed description of the sensor or system. The content is open to accommodate changes to SensorML or to support other description languages.")
    @JsonProperty(value = "observationType")
    protected String observationType;
    @JsonProperty(value = "observationType")
    @JsonPropertyDescription("TBD")

	@JsonBackReference(value = "events")
	protected Set<Observation> observations;
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public String getObservationsNavigationLink() {
        return "Datastream("+id+")/Observations";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Observations@iot.navigationLink")
    public void setObservationsNavigationLink(String value) {   }



    @JsonProperty(value = "observedProperty")
	protected ObservedProperty observedProperty;
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "ObservedProperty@iot.navigationLink")
    public String getObservedPropertNavigationLink() {
        return "Datastream("+id+")/ObservedProperty";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "ObservedProperty@iot.navigationLink")
    public void setObservedPropertyNavigationLink(String value) {   }

    @JsonProperty(value = "sensor")
    protected Sensor sensor;
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Sensor@iot.navigationLink")
    public String getSensorNavigationLink() {
        return "Datastream("+id+")/Sensor";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Sensor@iot.navigationLink")
    public void setSensorNavigationLink(String value) {   }

    @JsonProperty(value = "thing")
    public Thing getThing() {
        return thing;
    }

    @JsonProperty(value = "thing")
    public void setThing(Thing thing) {
        this.thing = thing;
    }

    @JsonProperty(value = "thing")
    protected Thing thing;
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Thing@iot.navigationLink")
    public String getThingNavigationLink() {
        return "Datastream("+id+")/Thing";
    }
    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "Thing@iot.navigationLink")
    public void setThingNavigationLink(String value) {   }

	/**
	 * Provides a Live reference to the list of events belonging to this
	 * {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Datastream} instance. The underlying data structure is not
	 * Thread-safe, therefore synchronization and concurrent modification issues
	 * might arise in multi-threaded environments.
	 *
	 * @return the live reference to the inner {@link java.util.Set}<{@link eu.almanac.ogc.sensorthing.api.datamodel.Observation}>.
	 */
	@JsonProperty(value = "events")
	public Set<Observation> getObservations()
	{
		return observations;
	}

	/**
	 * Updates the {@link java.util.Set}<{@link eu.almanac.ogc.sensorthing.api.datamodel.Observation}> gbelonging to this
	 * {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Datastream} instance. Any previously existing information is
	 * discarded.
	 *
	 * @param observations
	 *            the events to set.
	 */
	@JsonProperty(value = "events")
	public void setObservations(Set<Observation> observations)
	{
		this.observations = observations;
	}

	/**
	 * Adds a single {@link eu.almanac.ogc.sensorthing.api.datamodel.Observation} to the set of events belonging to
	 * this {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Datastream} instance.
	 *
	 * @param observation
	 *            The observation to add.
	 */
	public void addObservation(Observation observation)
	{
		// check not null
		if ((this.observations != null) && (observation != null))
			// add the observation
			this.observations.add(observation);
	}

	/**
	 * Removes a single {@link eu.almanac.ogc.sensorthing.api.datamodel.Observation} from the set of events
	 * belonging to this {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Datastream} instance.
	 *
	 * @param observation
	 *            The observation to remove.
	 * @return true if removal was successful, false otherwise
	 */
	public boolean removeObservation(Observation observation)
	{
		// the removal flag
		boolean removed = false;

		// check if the locations set is not null
		if (this.observations != null)
			// remove the location
			removed = this.observations.remove(observation);

		// return the removal result
		return removed;
	}

	/**
	 * Provides the {@link eu.almanac.ogc.sensorthing.api.datamodel.ObservedProperty} instance describing the property to
	 * which events belonging to this data stream belong.
	 *
	 * @return the observedProperty
	 */
	public ObservedProperty getObservedProperty()
	{
		return observedProperty;
	}

	/**
	 * Sets the {@link eu.almanac.ogc.sensorthing.api.datamodel.ObservedProperty} instance describing the property to
	 * which events belonging to this data stream belong.
	 * 
	 * @param observedProperty
	 *            the observedProperty to set
	 */
	public void setObservedProperty(ObservedProperty observedProperty)
	{
		this.observedProperty = observedProperty;
	}


}