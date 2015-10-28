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
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class Quarter
{
	// The quarter name
	private String name;
	
	// the quarter url
	private String url;
	
	// The quarter human-readable description
	private String description;
	
	// the city name
	private SmartCity city;
	
	// the district name
	private District district;
	
	// the set of waste production data, per year and genre
	private Set<WasteProduction> wasteProduction;
	
	// the quarter boundary
	private GeoBoundary boundary;
	
	// the waste bins located in the quarter
	public Set<WasteBin> bins;
	
	/**
	 * 
	 */
	public Quarter()
	{
		// initialize inner data structures
		this.wasteProduction = new HashSet<WasteProduction>();
		this.bins = new HashSet<WasteBin>();
	}
	
	public Quarter(String name)
	{
		// store the name
		this.setName(name);
		
		// initialize inner data structures
		this.wasteProduction = new HashSet<WasteProduction>();
		this.bins = new HashSet<WasteBin>();
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * @return the city
	 */
	public SmartCity getCity()
	{
		return city;
	}
	
	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(SmartCity city)
	{
		this.city = city;
	}
	
	
	
	/**
	 * @return the district
	 */
	public District getDistrict()
	{
		return district;
	}

	/**
	 * @param district the district to set
	 */
	public void setDistrict(District district)
	{
		this.district = district;
	}

	/**
	 * @return the wasteProduction
	 */
	public Set<WasteProduction> getWasteProduction()
	{
		return wasteProduction;
	}
	
	/**
	 * @param wasteProduction
	 *            the wasteProduction to set
	 */
	public void setWasteProduction(Set<WasteProduction> wasteProduction)
	{
		this.wasteProduction = wasteProduction;
	}
	
	public void addWasteProduction(WasteProduction wasteProduction)
	{
		this.wasteProduction.add(wasteProduction);
	}
	
	/**
	 * @return the boundary
	 */
	public GeoBoundary getBoundary()
	{
		return boundary;
	}
	
	/**
	 * @param boundary
	 *            the boundary to set
	 */
	public void setBoundary(GeoBoundary boundary)
	{
		this.boundary = boundary;
	}
	
	/**
	 * @return the bins
	 */
	public Set<WasteBin> getBins()
	{
		return bins;
	}
	
	/**
	 * @param bins
	 *            the bins to set
	 */
	public void setBins(Set<WasteBin> bins)
	{
		this.bins = bins;
	}
	
	public void addBin(WasteBin bin)
	{
		this.bins.add(bin);
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
}