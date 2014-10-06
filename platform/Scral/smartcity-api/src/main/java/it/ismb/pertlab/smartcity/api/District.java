/*
 * SmartCityAPI - core
 * 
 * Copyright (c) 2014 Dario Bonino
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
package it.ismb.pertlab.smartcity.api;

import java.util.HashSet;
import java.util.Set;

/**
 * A class representing an administrative district of a given city (or
 * municipality). It is defined as the union of a well-known set of quarters.
 * The district is modeled representing its name, the city to which belongs, the
 * set of quarters composing it and its geographical boundaries as a polygon of
 * longitude,latitude coordinates.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class District
{
	
	// The district name
	private String name;
	
	// the district url
	private String url;
	
	// The district human-readable description
	private String description;
	
	// the district boundary
	private GeoBoundary boundary;
	
	// the district name
	private SmartCity city;
	
	// the set of waste production data, per year and genre
	private Set<WasteProduction> wasteProduction;
	
	// the quarters composing the district
	private Set<Quarter> quarters;
	
	/**
	 * Create an initially empty district instance
	 */
	public District()
	{
		// initialize inner data structures
		this.quarters = new HashSet<Quarter>();
		this.wasteProduction = new HashSet<WasteProduction>();
	}
	
	/**
	 * Create a district instance named after the given district name.
	 * 
	 * @param name
	 *            , the district name as a {@link String}
	 */
	public District(String name)
	{
		this.name = name;
		
		// initialize inner data structures
		this.quarters = new HashSet<Quarter>();
		this.wasteProduction = new HashSet<WasteProduction>();
	}
	
	/**
	 * Provides the district name.
	 * 
	 * @return the name, the district name as a {@link String}
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Sets the district name
	 * 
	 * @param name
	 *            the name of the district to be set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Provides the district Uniform Resource Locator (URL)
	 * 
	 * @return the url, as a {@link String}
	 */
	public String getUrl()
	{
		return url;
	}
	
	/**
	 * Sets the district Uniform Resource Locator (URL)
	 * 
	 * @param url
	 *            the {@link String} instance to set as district URL
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	/**
	 * Gets the district long description
	 * 
	 * @return the long description of the district, as a {@link String}
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * Sets the district log description
	 * 
	 * @param description
	 *            the long description of the district as a {@link String}
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	/**
	 * Gets the {@link SmartCity} instance to which this district belongs.
	 * 
	 * @return the smart city to which the district belongs.
	 */
	public SmartCity getCity()
	{
		return city;
	}
	
	/**
	 * Sets the {@link SmartCity} instance containing this district.
	 * 
	 * @param city
	 *            the city to which this district belongs.
	 */
	public void setCity(SmartCity city)
	{
		this.city = city;
	}
	
	/**
	 * Provides the set of waste production data ({@link Set}<
	 * {@link WasteProduction}>) associated to this district.
	 * 
	 * @return the set of wasteProduction data of this district instance.
	 */
	public Set<WasteProduction> getWasteProduction()
	{
		return wasteProduction;
	}
	
	/**
	 * Sets/Replaces the set of waste production data associated to this
	 * district.
	 * 
	 * @param wasteProduction
	 *            the wasteProduction data to associate to this district.
	 */
	public void setWasteProduction(Set<WasteProduction> wasteProduction)
	{
		this.wasteProduction = wasteProduction;
	}
	
	/**
	 * Add a {@link WasteProduction} instance to this district.
	 * 
	 * @param wasteProduction
	 *            , the waste production data to associate to this district.
	 */
	public void addWasteProduction(WasteProduction wasteProduction)
	{
		this.wasteProduction.add(wasteProduction);
	}
	
	/**
	 * Provides the set of {@link Quarter}s belonging to this district.
	 * 
	 * @return the set of quarters associated to this district.
	 */
	public Set<Quarter> getQuarters()
	{
		return quarters;
	}
	
	/**
	 * Sets the set of {@link Quarter}s belonging to this district.
	 * 
	 * @param quarters
	 *            the quarters belonging to this district.
	 */
	public void setQuarters(Set<Quarter> quarters)
	{
		this.quarters = quarters;
	}
	
	/**
	 * Adds a {@link Quarter} instance to this district.
	 * 
	 * @param quarter
	 *             the quarter instance to add.
	 */
	public void addQuarter(Quarter quarter)
	{
		this.quarters.add(quarter);
	}
	
	/**
	 * Gets the geographical boundary of this district
	 * @return the boundary, as an instance of {@link GeoBoundary}
	 */
	public GeoBoundary getBoundary()
	{
		return boundary;
	}
	
	/**
	 * Sets the geographical boundary of this district
	 * @param boundary
	 *            the {@link GeoBoundary} instance to associate to this district
	 */
	public void setBoundary(GeoBoundary boundary)
	{
		this.boundary = boundary;
	}
	
}
