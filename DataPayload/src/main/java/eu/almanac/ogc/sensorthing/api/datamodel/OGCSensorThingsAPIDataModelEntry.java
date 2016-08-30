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

/**
 * The superclass of all data models entries, defines common fields and provides
 * corresponding accessors (getter and setter).
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public abstract class OGCSensorThingsAPIDataModelEntry
{
	// the unique identifier associated to any data model entry
	protected String id;
	
	/**
	 * Empty constructor, respects the bean pattern instantiation
	 */
	public OGCSensorThingsAPIDataModelEntry()
	{
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Provides back the ID of the specific model entry instance, as a String
	 * 
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 * Sets the ID of the specific model entry instance, as a String
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
}
