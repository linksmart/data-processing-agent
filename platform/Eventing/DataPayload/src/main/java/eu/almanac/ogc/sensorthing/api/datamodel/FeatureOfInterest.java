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
import org.geojson.GeoJsonObject;

import java.util.HashSet;
import java.util.Set;

/**
 * <strong>Definition:</strong> Features or feature collections that represent
 * the identifiable object(s) on which the sensor systems are making
 * observations. In the case of an in-situ sensor or observations being
 * attributes of the thing, the feature of interest could be the thing itself.
 * For remote sensors, this may be the geographical area or volume that is being
 * sensed.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class FeatureOfInterest extends OGCSensorThingsAPIDataModelEntry
{
	/**
	 * This is the description of the feature of interest entity. The content is
	 * open to accommodate changes to SensorML and to support other description
	 * languages. In the case of the thing itself being the feature of interest,
	 * this description property is inherited from the thing entity.
	 */
	@JsonPropertyDescription("This is the description of the feature of interest entity. The content is open to accommodate changes to SensorML and to support other description languages. In the case of the thing itself being the feature of interest, this description property is inherited from the thing entity.")
	@JsonProperty(value = "Description")
	private String description;
	
	/**
	 * The absolute geographical position of the feature of interest. This is
	 * generally the GeoJSON geometry object. In the case of the thing itself
	 * being the feature of interest, this geometry property is inherited from
	 * the thing entity by interpolating the geometries in the location
	 * entities.
	 */
	@JsonPropertyDescription("The absolute geographical position of the feature of interest. This is generally the GeoJSON geometry object. In the case of the thing itself being the feature of interest, this geometry property is inherited from the thing entity by interpolating the geometries in the location entities.")
	@JsonProperty(value = "Geometry")
	private GeoJsonObject geometry;
	/* {see=http://blog.opendatalab.de/hack/2013/07/16/geojson-jackson/} */
	
	/**
	 * An observation observes on one and only one feature of interest. One
	 * feature of interest could be observed by zero-to-many observations.
	 */
	private Set<Observation> observations;
	
	/**
	 * Empty constructor, respects the bean implementation pattern.
	 */
	public FeatureOfInterest()
	{
		// perform common initialization tasks
		this.initCommon();
	}
	
	/**
	 * Builds a new instance of Feature of Interest with the given description
	 * and geometry (as GeoJSON)
	 * 
	 * @param description
	 *            the description of the feature of interest entity. The content
	 *            is open to accommodate changes to SensorML and to support
	 *            other description languages. In the case of the thing itself
	 *            being the feature of interest, this description property is
	 *            inherited from the thing entity.
	 * 
	 * @param geometry
	 *            The absolute geographical position of the feature of interest.
	 *            This is generally the GeoJSON geometry object. In the case of
	 *            the thing itself being the feature of interest, this geometry
	 *            property is inherited from the thing entity by interpolating
	 *            the geometries in the location entities.
	 */
	public FeatureOfInterest(String description, GeoJsonObject geometry)
	{
		// store the feature of interest description
		this.description = description;
		
		// store the geometry of the represented feature of interest
		this.geometry = geometry;
	}
	
	/**
	 * Common initialization tasks, prepares inner datastructures.
	 */
	private void initCommon()
	{
		// initialize the inner data structures
		this.observations = new HashSet<Observation>();
	}
	
	/**
	 * Provides he description of the feature of interest entity. The content is
	 * open to accommodate changes to SensorML and to support other description
	 * languages. In the case of the thing itself being the feature of interest,
	 * this description property is inherited from the thing entity.
	 * 
	 * @return the description as a {@link String}
	 */
	@JsonPropertyDescription("This is the description of the feature of interest entity. The content is open to accommodate changes to SensorML and to support other description languages. In the case of the thing itself being the feature of interest, this description property is inherited from the thing entity.")
	@JsonProperty(value = "Description")
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Sets he description of the feature of interest entity. The content is
	 * open to accommodate changes to SensorML and to support other description
	 * languages. In the case of the thing itself being the feature of interest,
	 * this description property is inherited from the thing entity.
	 * 
	 * @param description
	 *            the description to set.
	 */
	@JsonPropertyDescription("This is the description of the feature of interest entity. The content is open to accommodate changes to SensorML and to support other description languages. In the case of the thing itself being the feature of interest, this description property is inherited from the thing entity.")
	@JsonProperty(value = "Description")
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Provides the absolute geographical position of the feature of interest.
	 * This is generally the GeoJSON geometry object.
	 * 
	 * @return the geometry as a {@link GeoJsonObject} instance.
	 */
	@JsonPropertyDescription("The absolute geographical position of the feature of interest. This is generally the GeoJSON geometry object. In the case of the thing itself being the feature of interest, this geometry property is inherited from the thing entity by interpolating the geometries in the location entities.")
	@JsonProperty(value = "Geometry")
	public GeoJsonObject getGeometry()
	{
		return geometry;
	}
	
	/**
	 * Sets he absolute geographical position of the feature of interest. This
	 * is generally the GeoJSON geometry object.
	 * 
	 * @param geometry
	 *            the geometry to set, as a {@link GeoJsonObject} instance.
	 */
	@JsonPropertyDescription("The absolute geographical position of the feature of interest. This is generally the GeoJSON geometry object. In the case of the thing itself being the feature of interest, this geometry property is inherited from the thing entity by interpolating the geometries in the location entities.")
	@JsonProperty(value = "Geometry")
	public void setGeometry(GeoJsonObject geometry)
	{
		this.geometry = geometry;
	}
	
	/**
	 * Provides the list of observations about this {@link FeatureOfInterest}
	 * instance. The returned set is Live reference to the internal data
	 * structure which is not Thread-safe. Synchronization and concurrent
	 * modification issues might arise in multi-threaded environments.
	 * 
	 * @return the observations
	 */
	public Set<Observation> getObservations()
	{
		return observations;
	}
	
	/**
	 * Sets the list of Observations about this {@link FeatureOfInterest}.
	 * Removes any list previously existing.
	 * 
	 * @param observations
	 *            the observations to set
	 */
	public void setObservations(Set<Observation> observations)
	{
		this.observations = observations;
	}
	
	/**
	 * Adds a single {@link Observation} to the set of observations about this
	 * {@link FeatureOfInterest}.
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
	 * Removes a single {@link Observation} from the set of observations about
	 * this {@link FeatureOfInterest}.
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
    @Override
    public String getImplementationOf() {
        return EventType.class.getSimpleName();
    }
}