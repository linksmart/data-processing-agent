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
import org.apache.logging.log4j.LogManager;

import java.time.Period;
import java.util.*;
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
            EventBuilder eventBuilder = new OGCEventBuilder();
            EventBuilder.setAsDefaultBuilder(ObservationImpl.class, eventBuilder);
            EventBuilder.registerBuilder(Observation.class, eventBuilder);
        } catch (UntraceableException e) {
            e.printStackTrace();
        }
    }
    public static Observation factory(Object event, Object StreamID, Object sensorID, long time){
        try {
            return (Observation) EventBuilder.getBuilder(ObservationImpl.class).factory(event,StreamID,sensorID,time,null,new Hashtable<>());
        } catch (UntraceableException e) {
            LogManager.getLogger(ObservationImpl.class).error(e.getMessage(),e);
            return null;
        }
    }
    @JsonIgnore
    public static String classTopic = "LS/sensor/"+UUID.randomUUID().toString()+"/OGC/1.0/Datastreams/";



    @Override
    public void setUnsafeValue(Object value) {
        if(value != null && value instanceof Collection && ((Collection)value).size() == 1)
            setUnsafeValue(((Collection)value).iterator().next());
        else if(value != null && value instanceof Map && ((Map)value).size() == 1)
            setUnsafeValue(((Map)value).values().iterator().next());
        else if(value != null && value instanceof EventEnvelope )
            setValue(((EventEnvelope)value).getValue());
        else
            setValue(value);
    }

    @Override
    public String getClassTopic() {
        return classTopic;
    }

    @Override
    public void setClassTopic(String topic) {
        classTopic = topic;
    }

    @Override
    public Map<String, Object> getAdditionalData() {
        final Map<String,Object> additionalData = new HashMap<>();
        if(featureOfInterest!=null && featureOfInterest.getDescription()!=null)
            additionalData.put("featureOfInterest.description",featureOfInterest.getDescription());
        if(parameters== null)
            parameters = new ArrayList<>();

        parameters.forEach(e->additionalData.put(e.getKey(),e.getValue()));

        return additionalData;
    }

    @Override
    public void setAdditionalData(Map<String, Object> additionalData) {
        FeatureOfInterest fi = new FeatureOfInterestImpl();
        fi.setId(UUID.randomUUID());
        fi.setDescription((String) additionalData.getOrDefault("featureOfInterest.description", this.getResult()==null? null:this.getResult().getClass().getSimpleName()));
        if(fi.getDescription()!=null)
            this.setFeatureOfInterest(fi);

        additionalData.remove("featureOfInterest.description");
        if(!additionalData.isEmpty()) {
            if(parameters==null)
                parameters = new ArrayList<>();
            additionalData.forEach((k, v) -> parameters.add(Pair.of(k, v)));
        }
    }

    @JsonIgnore
    private List<Pair<String, Object>> parameters=null;
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
    public List<Pair<String, Object>> getParameters() {
        return this.parameters;
    }

    @Override
    public void setParameters(List<Pair<String, Object>> parameters) {
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

    @Override
    public String getURL() {
        if(parameters!=null) {
            Optional<Pair<String,Object>> optional = parameters.stream().filter(p->"url".equals(p.getKey().toLowerCase())).findFirst();
            if(optional.isPresent())
                return optional.get().getValue().toString();
        }
        return null;
    }
    @Override
    public void setURL(String url) {

        if(parameters==null)
             parameters = new ArrayList<>();

        parameters.add(Pair.of("url", url));
    }
}
