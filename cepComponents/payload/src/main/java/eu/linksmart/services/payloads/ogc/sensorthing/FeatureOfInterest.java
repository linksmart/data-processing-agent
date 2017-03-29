package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.*;

import eu.linksmart.services.payloads.ogc.sensorthing.base.CCIEncoding;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
//@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="objectID")
public class FeatureOfInterest extends CCIEncoding {





    @JsonPropertyDescription("The detailed description of the sensor or system. The content is open to accommodate changes to SensorML or to support other description languages.")
    @JsonProperty(value = "feature")
    protected Object feature;



    @JsonProperty(value = "feature")
    @JsonPropertyDescription("TBD")
    public Object getFeature() {
        return feature;
    }
    @JsonProperty(value = "feature")
    @JsonPropertyDescription("TBD.")
    public void setFeature(Object name) {
        this.feature = name;
    }






}
