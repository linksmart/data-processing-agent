package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sun.xml.internal.ws.config.metro.dev.FeatureReader;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateDeserializer;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize.DateSerializer;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.*;
import eu.linksmart.services.payloads.ogc.sensorthing.links.DatastreamNavigationLink;
import eu.linksmart.services.payloads.ogc.sensorthing.links.FeatureOfInterestNavigationLink;
import eu.linksmart.services.utils.function.Utils;
import org.apache.commons.lang3.tuple.Pair;

import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
 *
 * In OGC SensorThing 1.0:
 *      An Observation is the act of measuring or otherwise determining the value of a property [OGC 10-004r3 and ISO 19156:2011]
 *
 *  @see   <a href="http://docs.opengeospatial.org/is/15-078r6/15-078r6.html#31" </a>
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 *
 */

@JsonDeserialize(as = ObservationImpl.class)
@JsonSerialize(as = ObservationImpl.class)
public interface Observation extends EventEnvelope, CommonControlInfo, FeatureOfInterestNavigationLink,DatastreamNavigationLink {

    /**
     * Gets the phenomenon time as a Date.
     * The phenomenon time is the time instant or period of when the Observation happens.
     *
     * @return the phenomenon time as Date.
     *
     * */
    @JsonPropertyDescription("The time instant or period of when the Observation happens." )
    @JsonSerialize(using = DateSerializer.class)
    @JsonGetter(value = "phenomenonTime")
    Date getPhenomenonTime();
    /**
     * Sets the phenomenon time with the given Date.
     * The phenomenon time is the time instant or period of when the Observation happens.
     *
     * @param  phenomenonTime sets the phenomenon time.
     *
     * */
    @JsonPropertyDescription("The time instant or period of when the Observation happens." )
    @JsonDeserialize(using =DateDeserializer.class)
    @JsonSetter(value = "phenomenonTime")
    void setPhenomenonTime(Date phenomenonTime);
    /**
     * Gets the result time as a Date.
     * The time of the Observation's result was generated.
     * Often the result time is the same as the phenomenon time.
     *
     * @return the result time as Date.
     *
     * */
    @JsonPropertyDescription("The time of the Observation's result was generated.")
    @JsonSerialize(using = DateSerializer.class)
    @JsonGetter(value = "resultTime")
    Date getResultTime();
    /**
     * Sets the result time with the given Data.
     * The time of the Observation's result was generated.
     * Often the result time is the same as the phenomenon time.
     *
     * @param  resultTime sets the result time.
     *
     * */
    @JsonPropertyDescription("The time of the Observation's result was generated.")
    @JsonDeserialize(using =DateDeserializer.class)
    @JsonSetter(value = "resultTime")
    void setResultTime(Date resultTime);
    /**
     * Gets the result as a Object.
     * The estimated value of an ObservedProperty from the Observation.
     *
     * @return the result time as Object.
     *
     * */
    @JsonPropertyDescription("The estimated value of an ObservedProperty from the Observation.")
    @JsonGetter(value = "result")
    Object getResult();
    /**
     * Sets the result time with the given value.
     * The estimated value of an ObservedProperty from the Observation.
     *
     * @param  result sets to the given value.
     *
     * */
    @JsonPropertyDescription("The estimated value of an ObservedProperty from the Observation.")
    @JsonSetter(value = "result")
    void setResult(Object result);
    /**
     * Gets the validTime as a Period.
     * The time period during which the result may be used.
     *
     * @return the validTime time as Object.
     *
     * */
    @JsonPropertyDescription("The time period during which the result may be used.")
    @JsonGetter(value = "validTime")
    Period getValidTime();
    /**
     * Sets the validTime time with the given Period.
     * The time period during which the result may be used.
     *
     * @param  validTime sets to the given Period.
     *
     * */
    @JsonPropertyDescription("The time period during which the result may be used.")
    @JsonSetter(value = "validTime")
    void setValidTime(Period validTime);
    /**
     * Gets the parameters as a Array of objects.
     * Key-value pairs showing the environmental conditions during measurement.
     *
     * @return the parameters time as Array of objects.
     *
     * */
    @JsonPropertyDescription("The time period during which the result may be used.")
    @JsonGetter(value = "parameters")
    List<Pair<String,String>> getParameters();
    /**
     * Sets the parameters time with the given Array of objects.
     * Key-value pairs showing the environmental conditions during measurement.
     *
     * @param  parameters sets to the given array of objects.
     *
     * */
    @JsonPropertyDescription("The time period during which the result may be used.")
    @JsonSetter(value = "parameters")
    void setParameters(List<Pair<String,String>> parameters);
    /**
     * Gets the related FeatureOfInterest of this observation.
     *
     * {@link FeatureOfInterest} An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations
     *
     * @return FeatureOfInterest of this observation
     *
     * */
    @JsonGetter("featureOfInterest")
    @JsonPropertyDescription("An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.")
    FeatureOfInterest getFeatureOfInterest();
    /**
     * Sets the related FeatureOfInterest of this observation.
     *
     * {@link FeatureOfInterest} An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations
     *
     * @param  featureOfInterest to be set in this observation
     *
     * */
    @JsonSetter("featureOfInterest")
    @JsonPropertyDescription("An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.")
    void setFeatureOfInterest(FeatureOfInterest featureOfInterest);
    /**
     * Gets the related Datastream of this observation.
     *
     * {@link Datastream} A Datastream can have zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.
     *
     * @return Datastream of this observation
     *
     * */
    @JsonGetter("datastream")
    @JsonPropertyDescription("A Datastream can have zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.")
    Datastream getDatastream();
    /**
     * Sets the related Datastream of this observation.
     *
     * {@link Datastream} A Datastream can have zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.
     *
     * @param  datastream to be set in this observation
     *
     * */
    @JsonSetter("datastream")
    @JsonPropertyDescription("A Datastream can have zero-to-many Observations. One Observation SHALL occur in one-and-only-one Datastream.")
    void setDatastream(Datastream datastream);
    static Observation factory(Object event, String resultType, String StreamID, String sensorID, String name) {
        return factory(event,resultType,sensorID,sensorID,(new Date()).getTime(), name);

    }
    static Observation factory(Object event, String resultType, String StreamID, String sensorID, long time, String name) {
        // Construct Sensor and Thing with the the Agent id.
        Sensor sen = new SensorImpl();
        sen.setId(sensorID);

        Thing th = new ThingImpl();
        th.setId(sensorID);

        // construct the a Datastream with the Statement Id
        Datastream ds = new DatastreamImpl();
        ds.setId(StreamID);
        ds.setSensor(sen);

        // add related objects
        ds.setSensor(sen);
        ds.setThing(th);

        // construct feature of interest with Id made by the hash of the name of the statement
        FeatureOfInterest fi = new FeatureOfInterestImpl();
        fi.setId(Utils.hashIt(name));
        fi.setDescription(resultType);



        // construct Observation with random ID
        Observation ob = new ObservationImpl();
        ob.setId(UUID.randomUUID());
        ob.setDatastream(ds);
        ob.setPhenomenonTime(new Date());
        ob.setFeatureOfInterest(fi);
        ob.setDatastream(ds);
        ob.setResult(event);
        ob.setFeatureOfInterest(null);
        ob.setDate(new Date(time));
        // add related objects
        fi.addObservations(ob);

        ArrayList<Observation> obs = (new ArrayList<>());
        obs.add(ob);
        ds.setObservations(obs);

        return ob;
    }



    /*
    * The property resultQuality is missing. The property is unlikely to be used. Therefore, it has not been implemented.
    * */
}
