package eu.linksmart.api.event.types;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.models.auth.In;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
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
    Map<String, EventBuilder> builders = new HashMap<>();
    /**
     * Some cases the some extra data of the event may be extracted from the topic/path of the event.
     * In this case, the topic/path can be provided so the underlying implementation extracts the needed data.
     *
     * @param topic used to construct some properties of the event envelope
     *
     * */
    @JsonIgnore
    void topicDataConstructor(String topic);
    /**
     * returns the time of the event as date. The time semantic could be anything (e.g. time when the event happens).
     *
     * @return time as date
     * */
    @JsonIgnore
    Date getDate();
    /**
     * returns date as string in an ISO 8601
     *
     * @return date as string
     * */
    @JsonIgnore
    default String getIsoTimestamp() {

        return DateTimeFormatter.ISO_INSTANT.format(getDate().toInstant());


    }
    /**
     * returns the id as IDType (the semantic depends on the implementation)
     *
     * @return id  as IDType
     * */
    @JsonIgnore
    IDType getId();
    /**
     * returns the id of the attribute as IDType (the semantic depends on the implementation)
     *
     * @return id of the attribute as IDType
     * */
    @JsonIgnore
    IDType getAttributeId();

    /**
     * returns the value/measurement
     *
     * @return value as ValueType
     * */
    @JsonIgnore
    ValueType getValue();
    /**
     * setts the time of the event as date. The time semantic could be anything (e.g. time when the event happens).
     *
     * @param time is the time as date
     * */
    @JsonIgnore
    void setDate(Date time);
    /**
     * setts the id (the semantic depends on the implementation)
     *
     * @param id is the id as IDType
     * */
    @JsonIgnore
    void  setId(IDType id);
    /**
     * setts the id of the attribute (the semantic depends on the implementation)
     *
     * @param id is the id of the attribute as IDType
     * */
    @JsonIgnore
    void setAttributeId(IDType id);
    /**
     * setts the value/measurement
     *
     * @param value is the value/measurement to be setted
     * */
    @JsonIgnore
    void setValue(ValueType value);
    /**
     * setts the value/measurement
     *
     * @param value is the value/measurement to be setted
     * */
    @JsonIgnore
    default void setUnsafeValue(Object value){
        setValue((ValueType) value);
    }
    /**
     * gets the associate topic of the envelope
     *
     * @return the associate topic of the envelope
     * */
    @JsonIgnore
    String getClassTopic();
    /**
     * setts the associate topic of the envelope
     *
     * @param  topic the associate topic of the envelope
     * */
    @JsonIgnore
    void setClassTopic(String topic);
    /**
     * gets the relative URL from which this event arrived
     *
     * @return the relative URL from which this instance arrived
     * */
    @JsonIgnore
    String getURL();
    /**
     * setts  the relative URL from which this event arrived
     *
     * @param  URL relative to the server endpoint where this event arrived
     * */
    @JsonIgnore
    void setURL(String URL);

    @JsonIgnore
    default Map<String, Object> getAdditionalData(){ return new HashMap<>();}

    @JsonIgnore
    default void setAdditionalData(Map<String, Object> additionalData){}
    @JsonIgnore
    default int getIntResult(){
       return getNumberResult().intValue();

    }
    @JsonIgnore
    default double getDoubleResult(){
        return getNumberResult().intValue();

    }
    @JsonIgnore
    default long getLongResult(){
        return getNumberResult().longValue();

    }
    @JsonIgnore
    default String getStringResult(){
        Object o = getValue();
        if(o==null)
            return null;
        return o.toString();

    }
    @JsonIgnore
    default List getLsitResult(){
        Object o = getValue();
        if(o==null || !( getValue() instanceof List ))
            return null;
        return (List)o;

    }
    @JsonIgnore
    default Object[] getArrayResult(){
        Object o = getValue();
        if(o==null || !( getValue() instanceof Object[] ))
            return null;
        return (Object[])o;

    }
    @JsonIgnore
    default Map getMapResult(){
        Object o = getValue();
        if(o==null || !( getValue() instanceof Map ))
            return null;
        return (Map)o;

    }
    @JsonIgnore
    default Number getNumberResult(){
        Object o = getValue();
        if(o==null)
            return Double.NaN;

        if (o.getClass() == int.class || o.getClass() == Integer.class || o.getClass() == BigDecimal.class ||  o.getClass() == BigInteger.class  || o.getClass() == short.class || o.getClass() == Short.class || o.getClass() == long.class || o.getClass() == Long.class || o.getClass() == float.class || o.getClass() == Float.class || o.getClass() == double.class || o.getClass() == Double.class || o.getClass() == Number.class || Number.class.isAssignableFrom(o.getClass())){
            return (Number) o;
        } else if (o.getClass() == boolean.class || o.getClass() == Boolean.class || Boolean.class.isAssignableFrom(o.getClass())){
            return ((Boolean)o)?1:0;
        } else if (o.getClass() == int.class || o.getClass() == Integer.class){
            return (Integer)o;
        } else if (String.class.isAssignableFrom(o.getClass())|| o.getClass() == Integer.class){
            try {
                Number number = Double.valueOf(o.toString());
            }catch (NumberFormatException e){
               // nothing
            }
        }
        return Double.NaN;
    }
}
