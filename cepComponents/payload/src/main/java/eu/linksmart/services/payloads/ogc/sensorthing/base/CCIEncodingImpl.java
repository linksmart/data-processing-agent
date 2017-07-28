package eu.linksmart.services.payloads.ogc.sensorthing.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private String encodingType;
    @Override
    public String getEncodingType() {
        return encodingType;
    }
    @Override
    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }
}
