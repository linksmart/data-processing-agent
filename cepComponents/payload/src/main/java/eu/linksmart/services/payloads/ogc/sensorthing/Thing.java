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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescription;


import java.util.HashMap;
import java.util.Map;

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
 * @author <a href="mailto:carvajal@fit.fhg.de">Angel Carvajal</a>
 *
 */
public class Thing extends CommonControlInfoDescription
{



	/**
	 * The empty class constructor, implements the bean instantiation pattern.
	 */
	public Thing()
	{
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

        super(description);

	}

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "properties")
    @JsonDeserialize(as=HashMap.class)
    protected Map<String,Object> properties;
    @JsonProperty(value = "properties")
    @JsonPropertyDescription("TBD")
    public Map<String,Object>  getProperties() {
        return properties;
    }
    @JsonProperty(value = "properties")
    @JsonPropertyDescription("TBD.")
    public void setProperties(Map<String,Object>  properties) { this.properties = properties; }

    public void addProperty(String key,Object  property) {
        if(this.properties==null)
            this.properties= new HashMap<>();
        this.properties.put(key,property);
    }






}