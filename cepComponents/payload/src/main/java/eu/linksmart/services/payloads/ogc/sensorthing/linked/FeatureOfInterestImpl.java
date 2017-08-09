package eu.linksmart.services.payloads.ogc.sensorthing.linked;

import com.fasterxml.jackson.annotation.*;
import eu.linksmart.services.payloads.ogc.sensorthing.FeatureOfInterest;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CCIEncodingImpl;

import java.util.ArrayList;
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
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 * Implementation of {@link FeatureOfInterest} interface
 * An Observation results in a value being assigned to a phenomenon.
 * The phenomenon is a property of a feature, the latter being the FeatureOfInterest of the Observation [OGC and ISO 19156:2011].
 * In the context of the Internet of Things, many Observations’ FeatureOfInterest can be the Location of the Thing.
 * For example, the FeatureOfInterest of a wifi-connect thermostat can be the Location of the thermostat (i.e., the living room where the thermostat is located in).
 * In the case of remote sensing, the FeatureOfInterest can be the geographical area or volume that is being sensed.
 *
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "@iot.id", scope = FeatureOfInterest.class)
public class FeatureOfInterestImpl extends CCIEncodingImpl implements FeatureOfInterest {

    /// An Observation observes on one-and-only-one FeatureOfInterest. One FeatureOfInterest could be observed by zero-to-many Observations.
    @JsonIgnore
    protected List<Observation> observations;

    @Override
    public List<Observation> getObservations() {
        return observations;
    }


    @Override
    public void setObservations(List<Observation> observations) {
        if(observations!= null)
            observations.forEach(o->o.setFeatureOfInterest(this));
        if (this.observations == null ) {
            this.observations=observations;
            return;
        }
        this.observations.addAll(observations);
    }

    @Override
    public void addObservations(Observation observation) {
        if(observations==null)
            observations= new ArrayList<>();
        this.observations.add(observation);
    }




    @JsonIgnore
    protected Object feature;



    @Override
    public Object getFeature() {
        return feature;
    }
    @Override
    public void setFeature(Object name) {
        this.feature = name;
    }


}
