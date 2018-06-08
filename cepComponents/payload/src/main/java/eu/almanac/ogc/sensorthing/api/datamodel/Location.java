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
import org.geojson.GeoJsonObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * <strong>Definition:</strong> A location is an absolute geographical position
 * at a specific time point.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
@Deprecated
public class Location extends OGCSensorThingsAPIDataModelEntry
{
	/**
	 * The time point/period of the location. Shall be rendered as ISO8601 time
	 * point/period string
	 **/
	@JsonPropertyDescription("The time point/period of the location. Shall be rendered as ISO8601 time point/period string")
	@JsonProperty(value = "Time")
	private Date time;

	/**
	 * The absolute geographical position of the location. This is generally a
	 * GeoJSON geometry object.
	 */
	@JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON geometry object")
	@JsonProperty(value = "Geometry")
	private GeoJsonObject geometry;
	/* {see=http://blog.opendatalab.de/hack/2013/07/16/geojson-jackson/} */

	/**
	 * A thing can locate at different geographical positions at different time
	 * points (multiple locations). Multiple things can locate at the same
	 * location at the same time. A thing may not have a location.
	 */
	@JsonProperty(value = "Things")
	private Set<Thing> things;

	/**
	 * Empty constructor, respects the bean instantiation pattern.
	 */
	public Location()
	{
		// perform common initialization
		this.initCommon();
	}

	/**
	 * Builds a new location object, referred to a specific time instant
	 * (expressed as a {@link Date} instance) and having the given geometry (as
	 * a {@link GeoJsonObject}).
	 *
	 * @param time
	 *            The time instant to which this location object is referred.
	 * @param geometry
	 *            The geometry of the location identified by this instance.
	 */
	public Location(Date time, GeoJsonObject geometry)
	{
		this.time = time;
		this.geometry = geometry;
	}

	/**
	 * Common initialization tasks.
	 */
	private void initCommon()
	{
		// create the set of Things positioned at this location, at a given time
		this.things = new HashSet<Thing>();
	}

	/**
	 * Provides the time point/period to which this locations description is
	 * referred
	 *
	 * @return the time
	 */
	@JsonPropertyDescription("The time point/period of the location. Shall be rendered as ISO8601 time point/period string")
	@JsonProperty(value = "Time")
	public Date getTime()
	{
		return time;
	}

	/**
	 * Sets the time point/period to which this locations description is
	 * referred
	 *
	 * @param time
	 *            the time to set
	 */
	@JsonPropertyDescription("The time point/period of the location. Shall be rendered as ISO8601 time point/period string")
	@JsonProperty(value = "Time")
	public void setTime(Date time)
	{
		this.time = time;
	}

	/**
	 * Provides the absolute geographical position of the location described by
	 * this Location instance. Generally a GeoJSON geometry object
	 *
	 * @return the geometry The location geometry as a {@link GeoJsonObject}
	 */
	@JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON geometry object")
	@JsonProperty(value = "Geometry")
	public GeoJsonObject getGeometry()
	{
		return geometry;
	}

	/**
	 * Sets the absolute geographical position of the location described by this
	 * Location instance. Generally a GeoJSON geometry object.
	 *
	 * @param geometry
	 *            The location geometry as a {@link GeoJsonObject}
	 */
	@JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON geometry object")
	@JsonProperty(value = "Geometry")
	public void setGeometry(GeoJsonObject geometry)
	{
		this.geometry = geometry;
	}

	/**
	 * Provides a Live reference to the {@link Set}:{@link Thing} being in this
	 * location at the represented time instant. As the reference is live and
	 * the underlying data-structure is not Thread-safe, synchronization and
	 * concurrent access issues might arise in multi-threaded environments.
	 *
	 * @return the set of thing being in this location at the represented time.
	 */
	@JsonProperty(value = "Things")
	public Set<Thing> getThings()
	{
		return things;
	}

	/**
	 * Sets the list of {@link Thing}s being in this location at the represented
	 * instant of time. Replaces any previously existing list.
	 *
	 * @param things
	 *            the things to set as being in this location at the represented
	 *            time instant.
	 */
	@JsonProperty(value = "Things")
	public void setThings(Set<Thing> things)
	{
		this.things = things;
	}

	/**
	 * Adds a {@link Thing} instance to the set of Things being in the location
	 * represented by this object at the time instant to which this object is
	 * referred.
	 *
	 * @param thing
	 *            The thing to add.
	 */
	public void addThing(Thing thing)
	{
		// check not null
		if ((this.things != null) && (thing != null))
		{
			// add the thing
			this.things.add(thing);
		}
	}

	/**
	 * Removes a {@link Thing} instance from the set of things being in the location
	 * represented by this object at the time instant to which this object is
	 * referred.
	 * 
	 * @param thing The thing to be removed.
	 * @return true if removal was successful, false otherwise.
	 */
	public boolean removeThing(Thing thing)
	{
		// the removal flag
		boolean removed = false;
		
		// check if the locations set is not null
		if (this.things != null)
			// remove the location
			removed = this.things.remove(thing);
		
		// return the removal result
		return removed;
	}
}