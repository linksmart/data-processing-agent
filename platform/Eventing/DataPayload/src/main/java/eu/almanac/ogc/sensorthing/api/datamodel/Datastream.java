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
package eu.almanac.ogc.sensorthing.api.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.datafusion.EventType;

import java.util.HashSet;
import java.util.Set;

/**
 * <strong>Definition:</strong> A datastream groups a collection of observations
 * that are related in some way. The one constraint is that the observations in
 * a datastream must measure the same observed property (i.e., one phenomenon).
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Datastream extends OGCSensorThingsAPIDataModelEntry
{
	/**
	 * This is the description of the datastream entity. The content is open to
	 * support other description languages.
	 **/
	@JsonPropertyDescription("This is the description of the datastream entity. The content is open to support other description languages.")
	@JsonProperty(value = "Description")
	private String description;

	/**
	 * A thing can have zero-to-many datastreams. A datastream entity can only
	 * link to a thing as a collection of observations or properties.
	 */
	@JsonProperty(value = "Thing")
	private Thing thing;

	/**
	 * A datastream can have zero-to-many observations. One observation must
	 * occur in one and only one datastream.
	 */
	@JsonProperty(value = "Observations")
	private Set<Observation> observations;

	/**
	 * The observations of a datastream observe the same observedProperty. The
	 * observations of different datastreams could observe the same
	 * observedProperty.
	 */
	@JsonProperty(value = "ObservedProperty")
	private ObservedProperty observedProperty;

	/**
	 * Empty constructor, respects the bean instantiation pattern
	 */
	public Datastream()
	{
		// perform common initialization tasks
		this.initCommon();
	}

	/**
	 * Builds a new datastream instance with the given description, belongin to
	 * the given thing and involving the given observed property.
	 *
	 * @param description
	 *            The description ({@link String}) of the datastream entity. The
	 *            content is open to support other description languages.
	 * @param thing
	 *            The {@link Thing} instance generating this stream of data.
	 * @param observedProperty
	 *            The {@link ObservedProperty} about which are observations
	 *            belonging to this datastream.
	 */
	public Datastream(String description, Thing thing, ObservedProperty observedProperty)
	{
		this.description = description;
		this.thing = thing;
		this.observedProperty = observedProperty;
	}

	/**
	 * Common initialization tasks
	 */
	private void initCommon()
	{
		// initialize inner sets
		this.observations = new HashSet<Observation>();
	}

	/**
	 * Provides the description of the datastream entity. The content is open to
	 * support other description languages.
	 *
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the datastream entity. The content is open to
	 * support other description languages.
	 *
	 * @param description
	 *            the description to set, as a {@link String}.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Provides the {@link Thing} instance to which the datastream belongs.
	 *
	 * @return the thing
	 */
	public Thing getThing()
	{
		return thing;
	}

	/**
	 * Sets the {@link Thing} instance to which the datastream must belong.
	 *
	 * @param thing
	 *            the thing to set
	 */
	public void setThing(Thing thing)
	{
		this.thing = thing;
	}

	/**
	 * Provides a Live reference to the list of observations belonging to this
	 * {@link Datastream} instance. The underlying data structure is not
	 * Thread-safe, therefore synchronization and concurrent modification issues
	 * might arise in multi-threaded environments.
	 *
	 * @return the live reference to the inner {@link Set}<{@link Observation}>.
	 */
	@JsonProperty(value = "Observations")
	public Set<Observation> getObservations()
	{
		return observations;
	}

	/**
	 * Updates the {@link Set}<{@link Observation}> gbelonging to this
	 * {@link Datastream} instance. Any previously existing information is
	 * discarded.
	 *
	 * @param observations
	 *            the observations to set.
	 */
	@JsonProperty(value = "Observations")
	public void setObservations(Set<Observation> observations)
	{
		this.observations = observations;
	}

	/**
	 * Adds a single {@link Observation} to the set of observations belonging to
	 * this {@link Datastream} instance.
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
	 * Removes a single {@link Observation} from the set of observations
	 * belonging to this {@link Datastream} instance.
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
	 * Provides the {@link ObservedProperty} instance describing the property to
	 * which observations belonging to this data stream belong.
	 *
	 * @return the observedProperty
	 */
	public ObservedProperty getObservedProperty()
	{
		return observedProperty;
	}

	/**
	 * Sets the {@link ObservedProperty} instance describing the property to
	 * which observations belonging to this data stream belong.
	 * 
	 * @param observedProperty
	 *            the observedProperty to set
	 */
	public void setObservedProperty(ObservedProperty observedProperty)
	{
		this.observedProperty = observedProperty;
	}

    @Override
    public String getImplementationOf() {
        return EventType.class.getSimpleName();
    }
}