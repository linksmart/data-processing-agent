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
package it.ismb.pertlab.ogc.sensorthings.api.datamodel;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Definition: An observedProperty specifies the phenomenon of an observation.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class ObservedProperty extends OGCSensorThingsAPIDataModelEntry
{
	/**
	 * The URI of the observedProperty/phenomenon.
	 */
	@JsonProperty(value = "URI")
	@JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
	private String urn;
	
	/**
	 * The unit of measurement for the observations measuring this
	 * observedProperty. This is obtained by exploiting the JSciene library.
	 */
	@JsonPropertyDescription("The unit of measurement for the observations measuring this observedProperty. This is obtained by exploiting the JSciene library.")
	@JsonProperty(value = "UnitOfMeasurement")
	private String unitOfMeasurement;
	
	/**
	 * The observations of a datastream observe the same observedProperty. The
	 * observations of different datastreams could observe the same
	 * observedProperty.
	 */
	@JsonProperty(value = "Datastreams")
	private Set<Datastream> datastreams;
	
	/**
	 * Empty constructor, implements the bean instantiation pattern.
	 */
	public ObservedProperty()
	{
		// common initialization
		this.initCommon();
	}
	
	/**
	 * Builds a new instance of Observed Property, with the give URN (or URI)
	 * and specifies the corresponding unit of measure (if any).
	 * 
	 * @param urn
	 *            The URN(URI) identifying the observed property
	 * @param unitOfMeasurement
	 *            The unit of measure adopted for observations about the
	 *            property. Should in principle be part of the <a
	 *            href="http://unitsofmeasure.org/trac/">UCUM</a> set.
	 */
	public ObservedProperty(String urn, String unitOfMeasurement)
	{
		this.urn = urn;
		this.unitOfMeasurement = unitOfMeasurement;
	}
	
	/**
	 * Performs common initialization tasks. In particular instantiate the
	 * needed instance data structures.
	 */
	private void initCommon()
	{
		// initializes the inner datastream set
		this.datastreams = new HashSet<Datastream>();
	}
	
	/**
	 * Provides the URN(URI) of the observed property or phenomenon modeled by
	 * this instance.
	 * 
	 * @return the urn The phenomenon URI as a {@LINK String}
	 */
	@JsonProperty(value = "URI")
	@JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
	public String getUrn()
	{
		return urn;
	}
	
	/**
	 * Sets the URN(URI) of the observed property or phenomenon modeled by this
	 * instance.
	 * 
	 * @param urn
	 *            the urn to set.
	 */
	@JsonProperty(value = "URI")
	@JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
	public void setUrn(String urn)
	{
		this.urn = urn;
	}
	
	/**
	 * Provides the Unit Of Measure adopted for quantifying the observed
	 * property / phenomenon represented by this instance.
	 * 
	 * @return the unitOfMeasurement associated with this property.
	 */
	@JsonPropertyDescription("The unit of measurement for the observations measuring this observedProperty. This is obtained by exploiting the JSciene library.")
	@JsonProperty(value = "UnitOfMeasurement")
	public String getUnitOfMeasurement()
	{
		return unitOfMeasurement;
	}
	
	/**
	 * Sets the Unit Of Measure adopted for quantifying the observed
	 * property / phenomenon represented by this instance.
	 * @param unitOfMeasurement
	 *            the unitOfMeasurement to set.
	 */
	@JsonPropertyDescription("The unit of measurement for the observations measuring this observedProperty. This is obtained by exploiting the JSciene library.")
	@JsonProperty(value = "UnitOfMeasurement")
	public void setUnitOfMeasurement(String unitOfMeasurement)
	{
		this.unitOfMeasurement = unitOfMeasurement;
	}
	
	/**
	 * Provides the list of associated to this {@link ObservedProperty}. The
	 * returned set is Live reference to the internal data structure which is
	 * not Thread-safe. Synchronization and concurrent modification issues might
	 * arise in multi-threaded environments.
	 * 
	 * @return the {@link Set}<{@link Datastream}> of datastreams gassociated to
	 *         this {@link ObservedProperty} instance.
	 */
	@JsonProperty(value = "Datastreams")
	public Set<Datastream> getDatastreams()
	{
		return datastreams;
	}
	
	/**
	 * Sets the list nof datastreams associated to this {@link ObservedProperty}
	 * . Removes any list previously existing.
	 * 
	 * @param datastreams
	 *            the datastreams to set.
	 */
	@JsonProperty(value = "Datastreams")
	public void setDatastreams(Set<Datastream> datastreams)
	{
		this.datastreams = datastreams;
	}
	
	/**
	 * Add a single datastream to the list of datastreams associated to this
	 * {@link ObservedProperty} instance.
	 * 
	 * @param datastream
	 *            The {@link Datastream} instance to add.
	 */
	public void addDatastream(Datastream datastream)
	{
		// check not null
		if ((this.datastreams != null) && (datastream != null))
			// add the datastream to the existing set of datastreams
			this.datastreams.add(datastream);
	}
	
	/**
	 * Removes the given datastream from the list of datastreams associated to
	 * this {@link ObservedProperty} instance.
	 * 
	 * @param datastream
	 *            The {@link Datastream} instance to remove.
	 * @return true if removal was successful, false otherwise.
	 */
	public boolean removeDataStream(Datastream datastream)
	{
		// the removal flag
		boolean removed = false;
		
		// check if the locations set is not null
		if (this.datastreams != null)
			// remove the location
			removed = this.datastreams.remove(datastream);
		
		// return the removal result
		return removed;
	}
	
}