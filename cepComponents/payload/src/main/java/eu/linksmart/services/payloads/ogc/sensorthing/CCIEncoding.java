package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 */
public interface CCIEncoding extends  CommonControlInfoDescription{
    @JsonPropertyDescription("TBD")
    @JsonGetter(value = "encodingType")
    String getEncodingType();

    @JsonPropertyDescription("TBD.")
    @JsonSetter(value = "encodingType")
    void setEncodingType(String encodingType);

}
