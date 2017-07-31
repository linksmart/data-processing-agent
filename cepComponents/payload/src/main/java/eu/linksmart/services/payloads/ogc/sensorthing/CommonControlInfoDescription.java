package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 */
public interface CommonControlInfoDescription extends CommonControlInfo {


    /**
     * Returns the description of this Thing instance, as a {@link String}
     *
     * @return the description
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonGetter(value = "description")
    String getDescription();

    /**
     * Sets the description of this Thing instance.
     *
     * @param description
     *            the description to set as a {@link String}
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonGetter(value = "description")
    void setDescription(String description);

    /**
     * Sets the common name of the object
     *
     * @param name
     *            the common name to set as a {@link String}
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonSetter(value = "name")
    void setName(String name);
    /**
     * Gets the common name of the object
     *
     * @return  the common name to set as a {@link String}
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonGetter(value = "name")
    String getName();
}
