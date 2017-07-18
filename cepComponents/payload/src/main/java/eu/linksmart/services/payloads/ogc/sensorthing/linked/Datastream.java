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

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <strong>Definition:</strong> A datastream groups a collection of events
 * that are related in some way. The one constraint is that the events in
 * a datastream must measure the same observed property (i.e., one phenomenon).
 *
 *
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "object.id")
public class Datastream extends eu.linksmart.services.payloads.ogc.sensorthing.Datastream
{
    @JsonPropertyDescription("The detailed description of the sensor or system. The content is open to accommodate changes to SensorML or to support other description languages.")
    @JsonProperty(value = "observationType")
    protected String observationType;



    /*
       // @JsonProperty(value = "observationType")
        @JsonPropertyDescription("TBD")
    */
    @JsonIgnore
	protected List<Observation> observations;
    @JsonGetter("observations") // <--- this is intentional
    public List<Observation> getObservations() {
        return observations;
    }
    @JsonSetter("observations")
    public void SetObservations(List<Observation> observation) {
        if(observations== null)
            observations=new ArrayList<>();
        observations = (observation);
    }
    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "Observations@iot.navigationLink")
    public String getObservationsNavigationLink() {
        return "Datastream("+id+")/Observations";
    }
    @JsonSetter(value = "Observations@iot.navigationLink")
    public void setObservationsNavigationLink(String value) {

    }

    @JsonProperty(value = "observedProperty")
	protected ObservedProperty observedProperty;
    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "ObservedProperty@iot.navigationLink")
    public String getObservedPropertNavigationLink() {
        return "Datastream("+id+")/ObservedProperty";
    }
    @JsonPropertyDescription("TBD.")
    @JsonSetter(value = "ObservedProperty@iot.navigationLink")
    public void setObservedPropertyNavigationLink(String value) {   }
    @JsonGetter(value = "sensor")
    public Sensor getSensor() {
        return sensor;
    }
    @JsonSetter(value = "sensor")
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
        if(this.sensor.getDatastreams() == null) {
            this.sensor.datastreams = new ArrayList<>();
            this.sensor.datastreams.add(this);
        } else if (!this.sensor.datastreams.contains(this)) {
            this.sensor.datastreams.add(this);
        }


    }

    @JsonIgnore
    protected Sensor sensor;
    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "Sensor@iot.navigationLink")
    public String getSensorNavigationLink() {
        return "Datastream("+id+")/Sensor";
    }
    @JsonPropertyDescription("TBD.")
    @JsonSetter(value = "Sensor@iot.navigationLink")
    public void setSensorNavigationLink(String value) {   }

    // @JsonGetter("observations") // <--- this is intentional
    public Thing getThing() {
        return thing;
    }

    @JsonSetter(value = "thing")
    public void setThing(Thing thing) {
        this.thing = thing;
    }

    @JsonIgnore
    protected Thing thing;
   // @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "Thing@iot.navigationLink")
    public String getThingNavigationLink() {
        return "Datastream("+id+")/Thing";
    }
    //@JsonPropertyDescription("TBD.")
    @JsonSetter(value = "Thing@iot.navigationLink")
    public void setThingNavigationLink(String value) {   }

	/**
	 * Provides a Live reference to the list of events belonging to this
	 * {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Datastream} instance. The underlying data structure is not
	 * Thread-safe, therefore synchronization and concurrent modification issues
	 * might arise in multi-threaded environments.
	 *
	 * @return the live reference to the inner {@link java.util.Set} {@link eu.almanac.ogc.sensorthing.api.datamodel.Observation}.
	 */
	//@JsonProperty(value = "events")
	/*public Set<Observation> getObservations()
	{
		return observations;
	}*/

	/**
	 * Updates the {@link java.util.Set} {@link eu.almanac.ogc.sensorthing.api.datamodel.Observation}  gbelonging to this
	 * {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Datastream} instance. Any previously existing information is
	 * discarded.
	 *
	 * @param observations
	 *            the events to set.
	 */
/*	@JsonProperty(value = "events")
	public void setObservations(Set<Observation> observations)
	{
		this.observations = observations;
	}
*/
	/**
	 * Adds a single {@link eu.almanac.ogc.sensorthing.api.datamodel.Observation} to the set of events belonging to
	 * this {@link eu.linksmart.services.payloads.ogc.sensorthing.linked.Datastream} instance.
	 *
	 * @param observation
	 *            The observation to add.
	 */
	public void addObservation(Observation observation) {
        // check not null
        if (observation != null){
            if ((this.observations == null))
                this.observations = new ArrayList<>();

            if(!this.observations.contains(observation))
                // add the observation
                this.observations.add(observation);

        }
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