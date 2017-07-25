package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 */
public interface CommonControlInfoDescription extends CommonControlInfo {
    @Override
    String toString();

    /**
     * Returns the description of this Thing instance, as a {@link String}
     *
     * @return the description
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonProperty(value = "description")
    String getDescription();

    /**
     * Sets the description of this Thing instance.
     *
     * @param description
     *            the description to set as a {@link String}
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonProperty(value = "description")
    void setDescription(String description);
}
