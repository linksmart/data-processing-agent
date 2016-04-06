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
package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CCIEncoding;
import org.geojson.GeoJsonObject;

import java.util.Set;

/**
 * <strong>Definition:</strong> A location is an absolute geographical position
 * at a specific time point.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Location extends CCIEncoding
{




	/**
	 * The absolute geographical position of the location. This is generally a
	 * GeoJSON location object.
	 */
	@JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON location object")
	@JsonProperty(value = "location")
	protected GeoJsonObject location;
	/* {see=http://blog.opendatalab.de/hack/2013/07/16/geojson-jackson/} */

	/**
	 * Empty constructor, respects the bean instantiation pattern.
	 */
	public Location()
	{
        this("",null);
	}

	/**
	 * Builds a new location object, referred to a specific time instant
	 * (expressed as a {@link java.util.Date} instance) and having the given location (as
	 * a {@link org.geojson.GeoJsonObject}).
	 *
	 * @param location
	 *            The location of the location identified by this instance.
	 */
	public Location(GeoJsonObject location)
	{
		this("",location);
	}
    /**
     * Builds a new location object, referred to a specific time instant
     * (expressed as a {@link java.util.Date} instance) and having the given location (as
     * a {@link org.geojson.GeoJsonObject}).
     *
     * @param location
     *            The location of the location identified by this instance.
     */
    public Location(String description,GeoJsonObject location)
    {
        this.description = description;
        this.location = location;
    }
	/**
	 * Provides the absolute geographical position of the location described by
	 * this Location instance. Generally a GeoJSON location object
	 *
	 * @return the location The location location as a {@link org.geojson.GeoJsonObject}
	 */
	@JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON location object")
	@JsonProperty(value = "location")
	public GeoJsonObject getLocation()
	{
		return location;
	}

	/**
	 * Sets the absolute geographical position of the location described by this
	 * Location instance. Generally a GeoJSON location object.
	 *
	 * @param location
	 *            The location location as a {@link org.geojson.GeoJsonObject}
	 */
	@JsonPropertyDescription("The absolute geographical position of the location. This is generally a GeoJSON location object")
	@JsonProperty(value = "location")
	public void setLocation(GeoJsonObject location)
	{
		this.location = location;
	}

    @Override
    public String toString(){
        if(location!=null)
            return "ID: "+id+"; Description: "+description+"; "+"Location: "+location.getBbox().toString();
        return null;
    }
    @Override
    public int hashCode(){
        return location.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        return obj == this || obj instanceof Location && ((Location) obj).location.getBbox().equals(location.getBbox());
    }

}