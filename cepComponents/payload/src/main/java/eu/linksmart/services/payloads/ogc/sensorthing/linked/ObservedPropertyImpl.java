package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;
import eu.linksmart.services.payloads.ogc.sensorthing.ObservedProperty;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescriptionImpl;

import java.util.List;

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
 * Implementation of {@link ObservedProperty} interface
 * @author Jose Angel Carvajal Soto
 * @since  1.5.0
 *
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = ObservedProperty.class)
public class ObservedPropertyImpl extends CommonControlInfoDescriptionImpl implements ObservedProperty {


    @JsonIgnore
    private List<Datastream> datastreams;
    @JsonIgnore
    private String definition;

    @Override
    public List<Datastream> getDatastreams() {
        return datastreams;
    }
    @Override
    public void setDatastreams(List<Datastream> datastreams) {
        if(datastreams!=null) {
            datastreams.forEach(d->d.setObservedProperty(this));
            this.datastreams = datastreams;
        }

    }

    @Override
    public String getDefinition()
    {
        return definition;
    }

    @Override
    public void setDefinition(String uri)
    {
        this.definition = uri;
    }

}
