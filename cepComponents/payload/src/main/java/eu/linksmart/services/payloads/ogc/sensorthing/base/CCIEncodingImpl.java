package eu.linksmart.services.payloads.ogc.sensorthing.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.CCIEncoding;


/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
public abstract class CCIEncodingImpl extends CommonControlInfoDescriptionImpl implements CCIEncoding {


    public CCIEncodingImpl(String description, String encodingType) {
        super(description);
        this.encodingType = encodingType;
    }

    public CCIEncodingImpl(String encodingType) {
        this.encodingType = encodingType;
    }

    public CCIEncodingImpl() {
        this.encodingType = null;
    }

    @JsonPropertyDescription("The detailed description of the sensor or system. The content is open to accommodate changes to SensorML or to support other description languages.")
    @JsonProperty(value = "encodingType")
    private String encodingType;
    @Override
    @JsonProperty(value = "encodingType")
    @JsonPropertyDescription("TBD")
    public String getEncodingType() {
        return encodingType;
    }
    @Override
    @JsonProperty(value = "encodingType")
    @JsonPropertyDescription("TBD.")
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }
}
