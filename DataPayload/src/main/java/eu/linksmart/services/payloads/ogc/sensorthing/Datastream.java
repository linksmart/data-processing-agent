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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.GeoJsonObjectDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.GeoJsonObjectSerializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateSerializer;
import org.geojson.GeoJsonObject;
import org.geojson.Polygon;


import java.util.*;

/**
 * <strong>Definition:</strong> A datastream groups a collection of observations
 * that are related in some way. The one constraint is that the observations in
 * a datastream must measure the same observed property (i.e., one phenomenon).
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Datastream extends CommonControlInfoDescription
{
    @JsonPropertyDescription("The detailed description of the sensor or system. The content is open to accommodate changes to SensorML or to support other description languages.")
    @JsonProperty(value = "observationType")
    protected String observationType;
    @JsonProperty(value = "observationType")
    @JsonPropertyDescription("TBD")
    public String getObservationType() {
        return observationType;
    }
    @JsonProperty(value = "observationType")
    @JsonPropertyDescription("TBD.")
    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }



    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "unitOfMeasurement")
    @JsonDeserialize(as=HashMap.class)
    protected Map<String,Object> unitOfMeasurement;
    @JsonProperty(value = "unitOfMeasurement")
    @JsonPropertyDescription("TBD")
    public Map<String,Object>  getUnitOfMeasurement() {
        return unitOfMeasurement;
    }
    @JsonProperty(value = "unitOfMeasurement")
    @JsonPropertyDescription("TBD.")
    public void setUnitOfMeasurement(Map<String,Object>  unitOfMeasurement) { this.unitOfMeasurement = unitOfMeasurement; }


    /**
     * The time instant or period of when the Observation happens.
     Note: Many resource-constrained sensing devices do not have a clock.
     As a result, a client may omit phenonmenonTime when POST new Observations,
     even though phenonmenonTime is a mandatory property. When a SensorThings service
     receives a POST Observations without phenonmenonTime, the service SHALL
     assign the current server time to the value of the phenomenonTime.
     * */
    @JsonPropertyDescription("The time instant or period of when the Observation happens.")
    @JsonProperty(value = "phenomenonTime")
    @JsonDeserialize(using = IntervalDateDeserializer.class)
    @JsonSerialize(using = IntervalDateSerializer.class)
    protected Interval phenomenonTime;
    @JsonProperty(value = "phenomenonTime")
    @JsonPropertyDescription("TBD")
    public Interval getPhenomenonTime() {
        return phenomenonTime;
    }
    @JsonProperty(value = "phenomenonTime")
    @JsonPropertyDescription("TBD.")
    public void setPhenomenonTime(Interval phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "resultTime")
    @JsonDeserialize(using = IntervalDateDeserializer.class)
    @JsonSerialize(using = IntervalDateSerializer.class)
    protected Interval resultTime;
    @JsonProperty(value = "resultTime")
    @JsonPropertyDescription("TBD")
    public Interval getResultTime() {
        return resultTime;
    }
    @JsonProperty(value = "resultTime")
    @JsonPropertyDescription("TBD.")
    public void setResultTime(Interval resultTime) {
        this.resultTime = resultTime;
    }

    @JsonPropertyDescription("TBD.")
    @JsonProperty(value = "observedArea")
    //@JsonDeserialize(using = GeoJsonObjectDeserializer.class)
    //@JsonSerialize(using = GeoJsonObjectSerializer.class)
    //@JsonDeserialize(as=Polygon.class)
    // FIX: the only form to fit with the standard in the document is this or with a map
    protected Polygon observedArea;
    @JsonProperty(value = "observedArea")
    @JsonPropertyDescription("TBD")
    public Polygon  getObservedArea() {
        return observedArea;
    }
    @JsonProperty(value = "observedArea")
    @JsonPropertyDescription("TBD.")
    public void setObservedArea(Polygon  observedArea) { this.observedArea = observedArea; }


	/**
	 * Empty constructor, respects the bean instantiation pattern
	 */
	public Datastream()
	{
		// perform common initialization tasks
		this.initCommon();
	}



	/**
	 * Common initialization tasks
	 */
	protected void initCommon()
	{
		// initialize inner sets
	}


	/**
	 * Provides the {@link eu.almanac.ogc.sensorthing.api.datamodel.Thing} instance to which the datastream belongs.
	 *
	 * @return the thing
	 */
	/*public Thing getThing()
	{
		return thing;
	}*/

	/**
	 * Sets the {@link eu.almanac.ogc.sensorthing.api.datamodel.Thing} instance to which the datastream must belong.
	 *
	 * @param thing
	 *            the thing to set
	 */
//	public void setThing(Thing thing)
	{
		//this.thing = thing;
	}



}