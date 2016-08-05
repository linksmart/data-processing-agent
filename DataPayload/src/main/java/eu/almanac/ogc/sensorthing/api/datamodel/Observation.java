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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.datafusion.EventType;
import eu.linksmart.gc.utils.function.Utils;

import java.util.Date;

/**
 * <strong>Definition:</strong> An act of observing a property or phenomenon on
 * a feature of interest, with the goal of producing an estimate of the value of
 * the property. A specialized event whose result is a data value. [OGC
 * 07-022r1].
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
public class Observation extends OGCSensorThingsAPIDataModelEntry implements EventType<String,String,Object>
{
	/**
	 * The time point/period of when the observation happens. To be rendered as
	 * ISO8601 time point/period string.
	 */
	@JsonPropertyDescription("The time point/period of when the observation happens. To be rendered as ISO8601 time point/period string.")
	@JsonProperty(value = "Time")
	protected Date phenomenonTime;

	/**
	 * The estimated value of an observedProperty from the observation. This
	 * will be intended as a Measure with value and unit.
	 */
	@JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
	@JsonProperty(value = "ResultValue")
	protected Object resultValue;

	/**
	 * The data type of the ResultValue. Service should by default set the
	 * ResultType as "Measure" unless users specify a different ResultType when
	 * creating an observation.
	 */
	@JsonPropertyDescription("The data type of the ResultValue. Service should by default set the ResultType as \"Measure\" unless users specify a different ResultType when creating an observation.")
	@JsonProperty(value = "ResultType")
	protected Object resultType;

	/**
	 * A datastream can have zero-to-many observations. One observation must
	 * occur in one and only one datastream.
	 */
	@JsonProperty(value = "Datastream")
	protected Datastream datastream;

	/**
	 * An observation observes on one and only one feature of interest. One
	 * feature of interest could be observed by one to many observations.
	 *
	 */
	@JsonProperty(value = "FeatureOfInterest")
	protected FeatureOfInterest featureOfInterest;

	/**
	 * An observation is performed by one and only one sensor. One sensor could
	 * produce zero to many observations.
	 */
	@JsonProperty(value = "Sensor")
	protected Sensor sensor;

	/**
	 * Empty constructor, implements the bean instantiation pattern
	 */
	public Observation()
	{
		// intentionaly left empty as no internal data structure must be
		// initialized
	}

	/**
	 * Returns The time point/period of when the observation happens. In string
	 * representations this is typically rendered as ISO8601 time point/period
	 * string.
	 *
	 * @return the phenomenonTime as a {@link Date} object
	 */
	@JsonPropertyDescription("The time point/period of when the observation happens. To be rendered as ISO8601 time point/period string.")
	@JsonProperty(value = "Time")
	public Date getPhenomenonTime()
	{
		return phenomenonTime;
	}

	/**
	 * Sets time point/period of when the observation happens.
	 *
	 * @param phenomenonTime
	 *            the phenomenonTime to set, as a {@link Date} instance.
	 */
	@JsonPropertyDescription("The time point/period of when the observation happens. To be rendered as ISO8601 time point/period string.")
	@JsonProperty(value = "Time")
	public void setPhenomenonTime(Date phenomenonTime)
	{
		this.phenomenonTime = phenomenonTime;
	}

	/**
	 * Provides the estimated value of an observedProperty from the observation.
	 * This is intended as a Measure with value and unit (e.g.,
	 * {@link javax.measure.}).
	 *
	 * @return the resultValue The value as an Object (typically a
	 *         {@link javax.measure.DecimalMeasure})
	 */
	@JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
	@JsonProperty(value = "ResultValue")
	public Object getResultValue()
	{
		return resultValue;
	}

	/**
	 * Sets the estimated value of an observedProperty from the observation.
	 * This is intended as a Measure with value and unit (e.g.,
	 * {@link javax.measure.DecimalMeasure}).
	 *
	 * @param resultValue
	 *            the resultValue to set, typically a
	 *            {@link javax.measure.DecimalMeasure}
	 */
	@JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
	@JsonProperty(value = "ResultValue")
	public void setResultValue(Object resultValue)
	{
		this.resultValue = resultValue;
	}

	/**
	 * Provides the data type of the ResultValue. Service should by default set
	 * the ResultType as \"Measure\" unless users specify a different ResultType
	 * when creating an observation.
	 *
	 * @return the resultType The type of the observation as an Object.
	 */
	@JsonPropertyDescription("The data type of the ResultValue. Service should by default set the ResultType as \"Measure\" unless users specify a different ResultType when creating an observation.")
	@JsonProperty(value = "ResultType")
	public Object getResultType()
	{
		return resultType;
	}

	/**
	 * Sets the data type of the ResultValue.Typical adoption is:
	 *
	 * <pre>
	 * // the temperature value as a measure
	 * DecimalMeasure&lt;Temperature&gt; temperature = DecimalMeasure.valueOf(binMetric.getTemperature() + &quot; &quot;
	 * 		+ SI.CELSIUS.toString());
	 *
	 * // the corresponding observation
	 * Observation obs = new Observation();
	 *
	 * // set the observation type
	 * obs.setResultType(temperature.getClass().getSimpleName());
	 *
	 * // can also be done as follows
	 * obs.setResultType(temperature.getClass()) //but would not be easy to Jsonify
	 * </pre>
	 *
	 * @param resultType
	 *            the resultType to set
	 */
	@JsonPropertyDescription("The data type of the ResultValue. Service should by default set the ResultType as \"Measure\" unless users specify a different ResultType when creating an observation.")
	@JsonProperty(value = "ResultType")
	public void setResultType(Object resultType)
	{
		this.resultType = resultType;
	}

	/**
	 * Provides the {@link Datastream} instance to which this observation
	 * belongs.
	 *
	 * @return the datastream including this observation.
	 */
	@JsonProperty(value = "Datastream")
	public Datastream getDatastream()
	{
		return datastream;
	}

	/**
	 * Sets the {@link Datastream} instance to which this observation belongs.
	 *
	 * @param datastream
	 *            the datastream to which the observation must be associated.
	 */
	@JsonProperty(value = "Datastream")
	public void setDatastream(Datastream datastream)
	{
		this.datastream = datastream;
	}

	/**
	 * Provides the {@link FeatureOfInterest} about "described" by this
	 * observation.
	 *
	 * @return the featureOfInterest described by this observation.
	 */
	@JsonProperty(value = "FeatureOfInterest")
	public FeatureOfInterest getFeatureOfInterest()
	{
		return featureOfInterest;
	}

	/**
	 * Sets the {@link FeatureOfInterest} about "described" by this observation.
	 *
	 * @param featureOfInterest
	 *            the featureOfInterest to which this observation refers.
	 */
	@JsonProperty(value = "FeatureOfInterest")
	public void setFeatureOfInterest(FeatureOfInterest featureOfInterest)
	{
		this.featureOfInterest = featureOfInterest;
	}

	/**
	 * Provides the {@link Sensor} instance generating this observation
	 * @return the sensor
	 */
	@JsonProperty(value = "Sensor")
	public Sensor getSensor()
	{
		return sensor;
	}

	/**
	 * Sets the {@link Sensor} instance generating this observation
	 * @param sensor
	 *            the sensor to set
	 */
	@JsonProperty(value = "Sensor")
	public void setSensor(Sensor sensor)
	{
		this.sensor = sensor;
	}

	@Override
	public void topicDataConstructor(String topic) {
		String [] aux = topic.split("/");
		setId( aux[aux.length-2]);

	}

    @Override
    @JsonIgnore
    public Date getDate() {
        return phenomenonTime;
    }

    @Override
    @JsonIgnore
    public String getIsoTimestamp() {
        return Utils.getIsoTimestamp(phenomenonTime);
    }

    @Override
    @JsonIgnore
    public String getAttributeId() {
        return datastream.id;
    }

    @Override
    @JsonIgnore
    public Object getValue() {
        return resultValue;
    }

    @Override
    @JsonIgnore
    public void setDate(Date value) {
        phenomenonTime =value;

    }

    @Override
    @JsonIgnore
    public void setAttributeId(String value) {

        if (datastream==null)
            datastream = new Datastream();
        datastream.setId(value);

    }

    @Override
    public void setValue(Object value) {
        resultValue =value;

    }
    public static Observation factory(Object event, String resultType, String StreamID, String sensorID) {
       return factory(event,resultType,sensorID,sensorID,(new Date()).getTime());

    }
    public static Observation factory(Object event, String resultType, String StreamID, String sensorID, long time) {
        Sensor sen = new Sensor();
        sen.setId(sensorID);
        sen.setObservations(null);

        Datastream ds = new Datastream();
        ds.setObservations(null);
        ds.setId(StreamID);

        Observation ob = new Observation();
        ob.setDatastream(ds);
        ob.setSensor(sen);
        ob.setPhenomenonTime(new Date());
        ob.setResultType(resultType);
        ob.setResultValue(event);
        ob.setFeatureOfInterest(null);
        ob.setDate(new Date(time));

        return ob;
    }


}