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
public class SmartCity
{
	
	private String name;
	private String url;
	private int geoNameId;
	
	private GeoPoint location;
	private GeoBoundary boundary;
	/**
	 * 
	 * @element-type Quarter
	 */
	private Set<Quarter> quarters;
	private Set<District> districts;
	private Set<WasteProduction> cityWasteProduction;
	
	/**
	 * @param name
	 */
	public SmartCity(String name)
	{
		this.name = name;
		
		// initialize inner data structures
		this.quarters = new HashSet<Quarter>();
		this.districts = new HashSet<District>();
		this.cityWasteProduction = new HashSet<WasteProduction>();
	}
	
	/**
	 * 
	 */
	public SmartCity()
	{
		// initialize inner data structures
		this.quarters = new HashSet<Quarter>();
		this.districts = new HashSet<District>();
		this.cityWasteProduction = new HashSet<WasteProduction>();
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
	 * @return the location
	 */
	public GeoPoint getLocation()
	{
		return location;
	}
	
	/**
	 * @param location
	 *            the location to set
	 */
	public void setLocation(GeoPoint location)
	{
		this.location = location;
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
	 * @return the quarters
	 */
	public Set<Quarter> getQuarters()
	{
		return quarters;
	}
	
	/**
	 * @param quarters
	 *            the quarters to set
	 */
	public void setQuarters(Set<Quarter> quarters)
	{
		this.quarters = quarters;
	}
	
	/**
	 * @return the districts
	 */
	public Set<District> getDistricts()
	{
		return districts;
	}
	
	/**
	 * @param districts
	 *            the districts to set
	 */
	public void setDistricts(Set<District> districts)
	{
		this.districts = districts;
	}
	
	/**
	 * @return the cityWasteProduction
	 */
	public Set<WasteProduction> getCityWasteProduction()
	{
		return cityWasteProduction;
	}
	
	/**
	 * @param cityWasteProduction
	 *            the cityWasteProduction to set
	 */
	public void setCityWasteProduction(Set<WasteProduction> cityWasteProduction)
	{
		this.cityWasteProduction = cityWasteProduction;
	}
	
	/**
	 * @return the geoNameId
	 */
	public int getGeoNameId()
	{
		return geoNameId;
	}
	
	/**
	 * @param geoNameId
	 *            the geoNameId to set
	 */
	public void setGeoNameId(int geoNameId)
	{
		this.geoNameId = geoNameId;
	}
	
	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}
	
	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
}