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
public class GeoPoint
{
	
	private double latitude;
	
	private double longitude;
	
	public double elevation;
	
	/**
	 * @param latitude
	 * @param longitude
	 */
	public GeoPoint(double latitude, double longitude)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = 0.0;
	}
	
	/**
	 * @param latitude
	 * @param longitude
	 * @param elevation
	 */
	public GeoPoint(double latitude, double longitude, double elevation)
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.elevation = elevation;
	}
	
	public GeoPoint(String longLatString, boolean longlat)
	{
		String actualLongLat = longLatString.trim();
		String coordinates[] = actualLongLat.split(" ");
		
		if(coordinates.length >=2)
		{
			if(longlat)
			{
				this.longitude = Double.valueOf(coordinates[0]);
				this.latitude = Double.valueOf(coordinates[1]);
			}
			else
			{
				this.longitude = Double.valueOf(coordinates[1]);
				this.latitude = Double.valueOf(coordinates[0]);
			}
			
			if(coordinates.length == 3)
				this.elevation = Double.parseDouble(coordinates[2]);
		}
	}
	
	/**
	 * @return the latitude
	 */
	public double getLatitude()
	{
		return latitude;
	}
	
	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	
	/**
	 * @return the longitude
	 */
	public double getLongitude()
	{
		return longitude;
	}
	
	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	
	/**
	 * @return the elevation
	 */
	public double getElevation()
	{
		return elevation;
	}
	
	/**
	 * @param elevation
	 *            the elevation to set
	 */
	public void setElevation(double elevation)
	{
		this.elevation = elevation;
	}
	
	public String getAsWKT()
	{
		return "Point(("+this.longitude+" "+this.longitude+"))";
	}
	
}