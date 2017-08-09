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

import java.util.HashSet;
import java.util.Set;

/**
 * Thing class.
 * <p>
 * <strong>Definition:</strong> We use the ITU-T definition, i.e., with regard
 * to the Internet of Things, a thing is an object of the physical world
 * (physical things) or the information world (virtual things) which is capable
 * of being identified and integrated into communication networks. (ITU-T
 * Y.2060)
 * </p>
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Thing extends OGCSensorThingsAPIDataModelEntry
{
	/**
	 * This is the description of the thing entity. The content is open to
	 * accommodate changes to SensorML and to support other description
	 * languages.
	 **/
	@JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
	@JsonProperty(value = "Description")
	private String description;
	
	/** Meant to host the Thing metadata, e.g. in JSON **/
	@JsonPropertyDescription("Meant to host the Thing metadata, e.g. in JSON")
	@JsonProperty(value = "Metadata")
	private String metadata;
	
	/**
	 * A thing can locate at different geographical positions at different time
	 * points (multiple locations). Multiple things can locate at the same
	 * location at the same time. A thing may not have a location.
	 */
	@JsonProperty(value = "Locations")
	private Set<Location> locations;
	
	/**
	 * A thing can have zero-to-many datastreams. A datastream entity can only
	 * link to a thing as a collection of events or properties.
	 */
	@JsonProperty(value = "Datastreams")
	private Set<Datastream> datastreams;
	
	/**
	 * The empty class constructor, implements the bean instantiation pattern.
	 */
	public Thing()
	{
		// perform simple data structure initialization
		this.initCommon();
	}
	
	/**
	 * The class constructor, takes a Thing textual description and a metadat
	 * description.
	 * 
	 * @param description
	 *            This is the description of the thing entity. The content is
	 *            open to accommodate changes to SensorML and to support other
	 *            description languages.
	 * @param metadata
	 *            Meant to host the Thing metadata, e.g. in JSON
	 */
	public Thing(String description, String metadata)
	{
		// store the Thing description
		this.description = description;
		
		// store the Thing metadata
		this.metadata = metadata;
		
		// perform common initialization
		this.initCommon();
	}
	
	/**
	 * Performs common intialization tasks, basically creating empty instances
	 * of sets needed by this Thing implementation.
	 */
	private void initCommon()
	{
		// only initializes sets
		this.locations = new HashSet<Location>();
		this.datastreams = new HashSet<Datastream>();
	}
	
	/**
	 * Returns the description of this Thing instance, as a {@link String}
	 * 
	 * @return the description
	 */
	@JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
	@JsonProperty(value = "Description")
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Sets the description of this Thing instance.
	 * 
	 * @param description
	 *            the description to set as a {@link String}
	 */
	@JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
	@JsonProperty(value = "Description")
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Provides the metadata information associated to the Thing object, as a
	 * {@link String}
	 * 
	 * @return the metadata
	 */
	@JsonPropertyDescription("Meant to host the Thing metadata, e.g. in JSON")
	@JsonProperty(value = "Metadata")
	public String getMetadata()
	{
		return metadata;
	}
	
	/**
	 * Sets the metadata information of this Thing.
	 * 
	 * @param metadata
	 *            the metadata to set as a {@link String}
	 */
	@JsonPropertyDescription("Meant to host the Thing metadata, e.g. in JSON")
	@JsonProperty(value = "Metadata")
	public void setMetadata(String metadata)
	{
		this.metadata = metadata;
	}
	
	/**
	 * Provides the list of locations in which this Thing has been registered.
	 * The returned set is Live reference to the internal data structure which
	 * is not Thread-safe. Synchronization and concurrent modification issues
	 * might arise in multi-threaded environments.
	 * 
	 * @return the locations
	 */
	@JsonProperty(value = "Locations")
	public Set<Location> getLocations()
	{
		return locations;
	}
	
	/**
	 * Sets the list of locations in which this Thing has been registered.
	 * Replaces any existing list.
	 * 
	 * @param locations
	 *            the locations to set
	 */
	@JsonProperty(value = "Locations")
	public void setLocations(Set<Location> locations)
	{
		this.locations = locations;
	}
	
	/**
	 * Adds one location to the list of Locations in which this Thing has been
	 * registered.
	 * 
	 * @param location the location to add
	 */
	public void addLocation(Location location)
	{
		// check not null
		if ((this.locations != null)&&(location!=null))
			// add the location
			this.locations.add(location);
	}
	
	/**
	 * Removes one location from the set of locations in which this
	 * {@link Thing} instance was positioned.
	 * 
	 * @param location
	 *            The location to remove.
	 * @return true if removal is successful, false otherwise.
	 */
	public boolean removeLocation(Location location)
	{
		// the removal flag
		boolean removed = false;
		
		// check if the locations set is not null
		if (this.locations != null)
			// remove the location
			removed = this.locations.remove(location);
		
		// return the removal result
		return removed;
	}
	
	/**
	 * Provides the list of datastreams generated by this Thing. The returned
	 * set is Live reference to the internal data structure which is not
	 * Thread-safe. Synchronization and concurrent modification issues might
	 * arise in multi-threaded environments.
	 * 
	 * @return the {@link Set} {@link Datastream}  of datastreams generated by
	 *         this {@link Thing} instance.
	 */
	@JsonProperty(value = "Datastreams")
	public Set<Datastream> getDatastreams()
	{
		return datastreams;
	}
	
	/**
	 * Sets the list of datastreams generated by this thing. Removes any list
	 * previously existing.
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
	 * Add a single datastream to the list of datastreams generated by this
	 * {@link Thing} instance.
	 * 
	 * @param datastream
	 *            The {@link Datastream} instance to add.
	 */
	public void addDatastream(Datastream datastream)
	{
		// check not null
		if ((this.datastreams != null)&&(datastream!=null))
			// add the datastream to the existing set of datastreams
			this.datastreams.add(datastream);
	}
	
	/**
	 * Removes the given datastream from the list of datastreams generated by
	 * this {@link Thing} instance.
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