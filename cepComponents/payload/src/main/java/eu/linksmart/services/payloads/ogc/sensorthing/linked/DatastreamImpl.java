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
package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescriptionImpl;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.IntervalDateSerializer;
import org.geojson.Polygon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 * Implementation of {@link Datastream} interface
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = Datastream.class)
public class DatastreamImpl extends CommonControlInfoDescriptionImpl implements Datastream {
    @JsonIgnore
    protected String observationType;
    @JsonIgnore
	protected List<Observation> observations;
    @JsonIgnore
    protected ObservedProperty observedProperty;
    @JsonIgnore
    protected Sensor sensor;
    @JsonIgnore
    protected Thing thing;
    @JsonIgnore
    protected Map<String,Object> unitOfMeasurement;
    @JsonIgnore
    protected Interval phenomenonTime;
    @JsonIgnore
    protected Interval resultTime;
    @JsonIgnore
    public Interval getResultTime() {
        return resultTime;
    }
    @JsonIgnore
    protected Polygon observedArea;

    public DatastreamImpl()
    {
        // perform common initialization tasks
        this.initCommon();
    }

    @Override
    public String getObservationType() {
        return observationType;
    }
    @Override

    public void setObservationType(String observationType) {
        this.observationType = observationType;
    }


    @Override
    public Map<String,Object>  getUnitOfMeasurement() {
        return unitOfMeasurement;
    }
    @Override
    public void setUnitOfMeasurement(Map<String, Object> unitOfMeasurement) { this.unitOfMeasurement = unitOfMeasurement; }


    @Override
    public Interval getPhenomenonTime() {
        return phenomenonTime;
    }
    @Override
    public void setPhenomenonTime(Interval phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }


    @Override
    public void setResultTime(Interval resultTime) {
        this.resultTime = resultTime;
    }

    @Override
    public Polygon  getObservedArea() {
        return observedArea;
    }
    @Override
    public void setObservedArea(Polygon observedArea) { this.observedArea = observedArea; }


    @Override
    public List<Observation> getObservations() {
        return observations;
    }
    @Override
    public void SetObservations(List<Observation> observation) {
        if(observations== null)
            observations=new ArrayList<>();
        observations = (observation);
    }
    @Override
    public void addObservation(Observation observation) {
        // check not null
        if (observation != null){
            if ((this.observations == null))
                this.observations = new ArrayList<>();

            if(!this.observations.contains(observation))
                // add the observation
                this.observations.add(observation);

        }
    }


    @Override
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
    public Sensor getSensor() {
        return sensor;
    }
    @Override
    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
        if(this.sensor.getDatastreams() == null) {
            this.sensor.datastreams = new ArrayList<>();
            this.sensor.datastreams.add(this);
        } else if (!this.sensor.datastreams.contains(this)) {
            this.sensor.datastreams.add(this);
        }


    }


    @Override
    public Thing getThing() {
        return thing;
    }

    @Override
    public void setThing(Thing thing) {
        this.thing = thing;
        if (this.thing.datastreams == null)
            this.thing.datastreams = new ArrayList<>();

        if (!this.thing.datastreams.contains(this))
            this.thing.datastreams.add(this);
    }


    @Override
    public ObservedProperty getObservedProperty()
    {
        return observedProperty;
    }


    @Override
    public void setObservedProperty(ObservedProperty observedProperty)
    {
        this.observedProperty = observedProperty;
    }




    @Override
    public String getObservationsNavigationLink() {
        return "Datastream("+id+")/Observations";
    }
    @Override
    public void setObservationsNavigationLink(String value) {}
    @Override
    public String getObservedPropertNavigationLink() {
        return "Datastream("+id+")/ObservedProperty";
    }
    @Override
    public void setObservedPropertyNavigationLink(String value) {   }
    @Override
    public String getSensorNavigationLink() {
        return "Datastream("+id+")/Sensor";
    }
    @Override
    public void setSensorNavigationLink(String value) {   }
    @Override
    public String getThingNavigationLink() {
        return "Datastream("+id+")/Thing";
    }
    //@JsonPropertyDescription("TBD.")
    @Override
    public void setThingNavigationLink(String value) {   }



    protected void initCommon()
    {
        // initialize inner sets
    }


}