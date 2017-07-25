package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 */
public interface CCIEncoding extends  CommonControlInfoDescription{
    @JsonProperty(value = "encodingType")
    @JsonPropertyDescription("TBD")
    String getEncodingType();

    @JsonProperty(value = "encodingType")
    @JsonPropertyDescription("TBD.")
    void setEncodingType(String encodingType);
}
