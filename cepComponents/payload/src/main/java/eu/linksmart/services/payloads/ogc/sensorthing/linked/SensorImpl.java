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
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.Sensor;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CCIEncodingImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * <strong>Definition:</strong> A sensor is an instrument that can observe a
 * property or phenomenon with the goal of producing an estimate of the value of
 * the property. In some cases, the sensor in this data model can also be seen
 * as the procedure (method, algorithm, or instrument) defined in OGC 07-022r1.
 * 
 * @author <a href="mailto:bonino@ismb.it">Dario Bonino</a>
 *
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id" , scope = Sensor.class)
public class SensorImpl extends CCIEncodingImpl implements Sensor {


    @Override
    public List<Datastream> getDatastreams() {
        return datastreams;
    }
    @Override
    public void setDatastreams(List<Datastream> datastreams) {
        if(datastreams!=null) {
            datastreams.forEach(d->d.setSensor(this));
            this.datastreams = datastreams;
        }

    }

    @JsonIgnore
    List<Datastream> datastreams;


    /**
     * The detailed description of the sensor or system. The content is open to
     * accommodate changes to SensorML or to support other description
     * languages.
     */
    @JsonIgnore
    protected Object metadata;




    /**
     * Empty class constructor, respects the bean instantiation pattern
     * @param description human readable description of this sensor
     * @param metadata any extra data added to this sensor
     */
    public SensorImpl(String description, String metadata) {
        super(description);
        this.metadata = metadata;
    }

    /**
     * Empty class constructor, respects the bean instantiation pattern
     * @param description human readable description of this sensor
     * @param metadata any extra data added to this sensor
     * @param encoding how the data send by this sensor is encoded
     */
    public SensorImpl(String description, String encoding, String metadata) {
        super(description, encoding);
        this.metadata = metadata;
    }
    /**
     * Builds a new Sensor instance described by the given metadata.
     *
     * @param metadata
     *            The detailed description of the sensor or system. The content
     *            is open to accommodate changes to SensorML or to support other
     *            description languages.
     */
    public SensorImpl(String metadata) {
        this.metadata = metadata;
    }
    public SensorImpl(){
        this.metadata=null;
    }

    /**
     * Provides back the metadata describing this {@link Sensor} instance.
     *
     * @return The metadata as a {@link String}. It contains the detailed
     *         description of the sensor or system. The content is open to
     *         accommodate changes to SensorML or to support other description
     *         languages.
     *
     *
     */
    @Override
    public Object getMetadata()
    {
        return metadata;
    }

    /**
     * Sets the metadata describing this {@link Sensor} instance.
     *
     * @param metadata
     *            the metadata to set, it contains the detailed description of
     *            the sensor or system. The content is open to accommodate
     *            changes to SensorML or to support other description languages.
     */
    @Override
    public void setMetadata(Object metadata)
    {
        this.metadata = metadata;
    }

    @Override
    public void addDatastream(Datastream datastream) {
        if(datastreams== null)
            datastreams = new ArrayList<>();
        if(!datastreams.contains(datastream)){
            datastreams.add(datastream);
        }
    }


}