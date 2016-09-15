package eu.linksmart.api.event.types;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
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
 * Interface hides the underlying event envelope implementation.
 * The envelope is the basic information that any IoT event must contain.
 *
 * The IDType is the id and the id of the attribute. The semantics depends on the implementation.
 *      Any type given must be able to be transform into a string (e.g. int, double, string, UIID)
 * The ValueType is the type of the actual payload of the event.
 *
 * All interfaces an function signatures defined in the IoT agent name space (eu.linksmart.event.*) should use
 * this interface and not any underlying implementation.
 *
 * @author Jose Angel Carvajal Soto
 * @since  1.0.0
 *
 * */
public interface EventEnvelope<IDType, ValueType> extends JsonSerializable {
    /**
     * Some cases the some extra data of the event may be extracted from the topic/path of the event.
     * In this case, the topic/path can be provided so the underlying implementation extracts the needed data.
     *
     * */
    @JsonIgnore
    public void topicDataConstructor(String topic);
    /**
     * returns the time of the event as date. The time semantic could be anything (e.g. time when the event happens).
     *
     * @return time as date
     * */
    @JsonIgnore
    public Date getDate();
    /**
     * returns date as string in an ISO 8601
     *
     * @return date as string
     * */
    @JsonIgnore
    public String getIsoTimestamp();
    /**
     * returns the id as IDType (the semantic depends on the implementation)
     *
     * @return id  as IDType
     * */
    @JsonIgnore
    public IDType getId();
    /**
     * returns the id of the attribute as IDType (the semantic depends on the implementation)
     *
     * @return id of the attribute as IDType
     * */
    @JsonIgnore
    public IDType getAttributeId();

    /**
     * returns the value/measurement
     *
     * @return value as ValueType
     * */
    @JsonIgnore
    public ValueType getValue();
    /**
     * setts the time of the event as date. The time semantic could be anything (e.g. time when the event happens).
     *
     * @param time is the time as date
     * */
    @JsonIgnore
    public void setDate(Date time);
    /**
     * setts the id (the semantic depends on the implementation)
     *
     * @param id is the id as IDType
     * */
    @JsonIgnore
    public void  setId(IDType id);
    /**
     * setts the id of the attribute (the semantic depends on the implementation)
     *
     * @param id is the id of the attribute as IDType
     * */
    @JsonIgnore
    public void setAttributeId(IDType id);
    /**
     * setts the value/measurement
     *
     * @param value is the value/measurement to be setted
     * */
    @JsonIgnore
    public void setValue(ValueType value);
}
