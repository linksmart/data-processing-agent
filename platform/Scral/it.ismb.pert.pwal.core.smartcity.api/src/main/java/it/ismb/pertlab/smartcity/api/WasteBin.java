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

/**
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public abstract class WasteBin
{
	
	// the human readable name of the bin
	protected String name;
	
	// the human readable description of the bin
	protected String description;
	
	// the uniform resource locator of the bin
	protected String url;
	
	// the address
	protected String address;
	
	protected District district;
	protected Quarter quarter;
	protected GeoPoint location;
	protected Garbage garbageType;
	
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
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * @param description
	 *            the description to set
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
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	
	/**
	 * @return the quarter
	 */
	public Quarter getQuarter()
	{
		return quarter;
	}
	
	/**
	 * @param quarter
	 *            the quarter to set
	 */
	public void setQuarter(Quarter quarter)
	{
		this.quarter = quarter;
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
	 * @return the garbageType
	 */
	public Garbage getGarbageType()
	{
		return garbageType;
	}
	
	/**
	 * @param garbageType
	 *            the garbageType to set
	 */
	public void setGarbageType(Garbage garbageType)
	{
		this.garbageType = garbageType;
	}
	
	/**
	 * @return the address
	 */
	public String getAddress()
	{
		return address;
	}
	
	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		WasteBin clone = null;
		try
		{
			clone = this.getClass().newInstance();
			
			//deep copy complex instance variables
			clone.setAddress(this.address);
			clone.setDescription(this.description);
			clone.setGarbageType(this.garbageType);
			clone.setName(this.name);
			clone.setQuarter(this.quarter);
			clone.setUrl(this.url);
			clone.setLocation(new GeoPoint(this.location.getLatitude(), this.location.getLongitude(), this.location.getElevation()));
			
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//return the cloned bin
		return clone;
	}
}