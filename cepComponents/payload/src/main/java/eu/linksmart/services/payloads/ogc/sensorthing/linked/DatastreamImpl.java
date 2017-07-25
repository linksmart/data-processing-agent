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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescriptionImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateSerializer;
import org.geojson.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <strong>Definition:</strong> A datastream groups a collection of events
 * that are related in some way. The one constraint is that the events in
 * a datastream must measure the same observed property (i.e., one phenomenon).
 *
 *
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = Datastream.class)
public class DatastreamImpl extends CommonControlInfoDescriptionImpl implements Datastream {
    @JsonPropertyDescription("The detailed description of the sensor or system. The content is open to accommodate changes to SensorML or to support other description languages.")
    @JsonProperty(value = "observationType")
    protected String observationType;



    /*
       // @JsonProperty(value = "observationType")
        @JsonPropertyDescription("TBD")
    */
    @JsonIgnore
	protected List<Observation> observations;
    @Override
    @JsonGetter("observations") // <--- this is intentional
    public List<Observation> getObservations() {
        return observations;
    }
    @Override
    @JsonSetter("observations")
    public void SetObservations(List<Observation> observation) {
        if(observations== null)
            observations=new ArrayList<>();
        observations = (observation);
    }
    @Override
    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "Observations@iot.navigationLink")
    public String getObservationsNavigationLink() {
        return "Datastream("+id+")/Observations";
    }
    @Override
    @JsonSetter(value = "Observations@iot.navigationLink")
    public void setObservationsNavigationLink(String value) {

    }

    @JsonProperty(value = "observedProperty")
	protected ObservedProperty observedProperty;
    @Override
    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "ObservedProperty@iot.navigationLink")
    public String getObservedPropertNavigationLink() {
        return "Datastream("+id+")/ObservedProperty";
    }
    @Override
    @JsonPropertyDescription("TBD.")
    @JsonSetter(value = "ObservedProperty@iot.navigationLink")
    public void setObservedPropertyNavigationLink(String value) {   }
    @Override
    @JsonGetter(value = "sensor")
    public Sensor getSensor() {
        return sensor;
    }
    @Override
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
    @Override
    @JsonPropertyDescription("TBD.")
    @JsonGetter(value = "Sensor@iot.navigationLink")
    public String getSensorNavigationLink() {
        return "Datastream("+id+")/Sensor";
    }
    @Override
    @JsonPropertyDescription("TBD.")
    @JsonSetter(value = "Sensor@iot.navigationLink")
    public void setSensorNavigationLink(String value) {   }

    // @JsonGetter("observations") // <--- this is intentional
    @Override
    public Thing getThing() {
        return thing;
    }

    @Override
    @JsonSetter(value = "thing")
    public void setThing(Thing thing) {
        this.thing = thing;
        if(this.thing.datastreams == null )
            this.thing.datastreams = new ArrayList<>();

        if( !this.thing.datastreams.contains(this))
            this.thing.datastreams.add(this);
    }

    @JsonIgnore
    protected Thing thing;
   // @JsonPropertyDescription("TBD.")
    @Override
    @JsonGetter(value = "Thing@iot.navigationLink")
    public String getThingNavigationLink() {
        return "Datastream("+id+")/Thing";
    }
    //@JsonPropertyDescription("TBD.")
    @Override
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
	 * this {@link Datastream} instance.
	 *
	 * @param observation
	 *            The observation to add.
	 */
	@Override
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
	 * Removes a single {@link Observation} from the set of events
	 * belonging to this {@link Datastream} instance.
	 *
	 * @param observation
	 *            The observation to remove.
	 * @return true if removal was successful, false otherwise
	 */
	@Override
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
	@Override
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
	@Override
    public void setObservedProperty(ObservedProperty observedProperty)
	{
		this.observedProperty = observedProperty;
	}

    @Override
    @JsonProperty(value = "observationType")
    @JsonPropertyDescription("TBD")
    public String getObservationType() {
        return observationType;
    }
    @Override
    @JsonProperty(value = "observationType")
    @JsonPropertyDescription("TBD.")
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }



    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "unitOfMeasurement")
    @JsonDeserialize(as=HashMap.class)
    protected Map<String,Object> unitOfMeasurement;
    @Override
    @JsonProperty(value = "unitOfMeasurement")
    @JsonPropertyDescription("TBD")
    public Map<String,Object>  getUnitOfMeasurement() {
        return unitOfMeasurement;
    }
    @Override
    @JsonProperty(value = "unitOfMeasurement")
    @JsonPropertyDescription("TBD.")
    public void setUnitOfMeasurement(Map<String, Object> unitOfMeasurement) { this.unitOfMeasurement = unitOfMeasurement; }


    /**
     * The time instant or period of when the Observation happens.
     Note: Many resource-constrained sensing devices do not have a clock.
     As a result, a client may omit phenonmenonTime when POST new Observations,
     even though phenonmenonTime is a mandatory property. When a SensorThings service
     receives a POST Observations without phenonmenonTime, the service SHALL
     assign the current server time to the value of the phenomenonTime.
     * */
    @JsonPropertyDescription("The time instant or period of when the Observation happens.")
    @JsonProperty(value = "phenomenonTime")
    @JsonDeserialize(using = IntervalDateDeserializer.class)
    @JsonSerialize(using = IntervalDateSerializer.class)
    protected Interval phenomenonTime;
    @Override
    @JsonProperty(value = "phenomenonTime")
    @JsonPropertyDescription("TBD")
    public Interval getPhenomenonTime() {
        return phenomenonTime;
    }
    @Override
    @JsonProperty(value = "phenomenonTime")
    @JsonPropertyDescription("TBD.")
    public void setPhenomenonTime(Interval phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "resultTime")
    @JsonDeserialize(using = IntervalDateDeserializer.class)
    @JsonSerialize(using = IntervalDateSerializer.class)
    protected Interval resultTime;
    @Override
    @JsonProperty(value = "resultTime")
    @JsonPropertyDescription("TBD")
    public Interval getResultTime() {
        return resultTime;
    }
    @Override
    @JsonProperty(value = "resultTime")
    @JsonPropertyDescription("TBD.")
    public void setResultTime(Interval resultTime) {
        this.resultTime = resultTime;
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "observedArea")
    //@JsonDeserialize(using = GeoJsonObjectDeserializer.class)
    //@JsonSerialize(using = GeoJsonObjectSerializer.class)
    //@JsonDeserialize(as=Polygon.class)
    // FIX: the only form to fit with the standard in the document is this or with a map
    protected Polygon observedArea;
    @Override
    @JsonProperty(value = "observedArea")
    @JsonPropertyDescription("TBD")
    public Polygon  getObservedArea() {
        return observedArea;
    }
    @Override
    @JsonProperty(value = "observedArea")
    @JsonPropertyDescription("TBD.")
    public void setObservedArea(Polygon observedArea) { this.observedArea = observedArea; }


    /**
     * Empty constructor, respects the bean instantiation pattern
     */
    public DatastreamImpl()
    {
        // perform common initialization tasks
        this.initCommon();
    }



    /**
     * Common initialization tasks
     */
    protected void initCommon()
    {
        // initialize inner sets
    }


    /**
     * Provides the {@link eu.almanac.ogc.sensorthing.api.datamodel.Thing} instance to which the datastream belongs.
     *
     * @return the thing
     */
	/*public Thing getThing()
	{
		return thing;
	}*/

    /**
     * Sets the {@link eu.almanac.ogc.sensorthing.api.datamodel.Thing} instance to which the datastream must belong.
     *
     * @param thing
     *            the thing to set
     */
//	public void setThing(Thing thing)
    {
        //this.thing = thing;
    }



}