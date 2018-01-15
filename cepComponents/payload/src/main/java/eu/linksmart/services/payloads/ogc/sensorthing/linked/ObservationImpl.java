package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoImpl;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Period;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 *  Copyright [2013] [Fraunhofer-Gesellschaft]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
/**
 * Implementation of {@link Observation} interface
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = Observation.class)
public class ObservationImpl extends CommonControlInfoImpl implements Observation, EventEnvelope<Object,Object> {
    static {
        try {
            EventBuilder.setAsDefaultBuilder(Observation.class, new OGCEventBuilder());
        } catch (UntraceableException e) {
            e.printStackTrace();
        }
    }

    public static String defaultTopic = null;

    @Override
    public String getClassTopic() {
        return defaultTopic;
    }

    @Override
    public void setClassTopic(String topic) {
        defaultTopic = topic;
    }

    @JsonIgnore
    private List<Pair<String, String>> parameters=null;
    @JsonIgnore
    protected FeatureOfInterest featureOfInterest;
    @JsonIgnore
    protected Datastream datastream = null;
    @JsonIgnore
    protected Date phenomenonTime;
    @JsonIgnore
    protected Date resultTime;
    @JsonIgnore
    protected Object result;
    @JsonIgnore
    protected Period validTime;
    @JsonIgnore
    protected String topic;

    /*
    * First the implementation of the Observation Interface.
    * Getters and setters of the intrinsic properties.
   */
    @Override
    public Date getPhenomenonTime() {
        return phenomenonTime;
    }
    @Override
    public void setPhenomenonTime(Date phenomenonTime) {
        this.phenomenonTime = phenomenonTime;
    }


    @Override
    public Date getResultTime() {
        return resultTime;
    }
    @Override
    public void setResultTime(Date resultTime) {
        this.resultTime = resultTime;
    }

    @Override
    public Object getResult() {
        return result;
    }
    @Override
    public void setResult(Object result) {
        this.result = result;
    }


    @Override
    public Period getValidTime() {
        return validTime;
    }
    @Override
    public void setValidTime(Period validTime) {
        this.validTime = validTime;
    }

    @Override
    public List<Pair<String, String>> getParameters() {
        return this.parameters;
    }

    @Override
    public void setParameters(List<Pair<String, String>> parameters) {
        this.parameters=parameters;
    }

    // Getters and setters of the relational properties.
    @Override
    public FeatureOfInterest getFeatureOfInterest() {
        return featureOfInterest;
    }
    @Override
    public void setFeatureOfInterest(FeatureOfInterest featureOfInterest) {
        if(featureOfInterest!=null) {
            this.featureOfInterest = featureOfInterest;
            this.featureOfInterest.addObservations(this);
        }
    }

    @Override
    public Datastream getDatastream() {
        return datastream;
    }
    @Override
    public void setDatastream(Datastream datastream) {
        this.datastream = datastream;
        this.datastream.addObservation(this);
    }



}
