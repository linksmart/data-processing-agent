package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by José Ángel Carvajal on 25.07.2017 a researcher of Fraunhofer FIT.
 */
public interface CommonControlInfo {
    /**
     *  selfLink is the absolute URL of an entity that is unique among all other entities.
     *  @return the @iot.selfLink value
     *  */
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
    @JsonGetter(value = "@iot.selfLink")
    String getSelfLink();

    /**
     *  selfLink is the absolute URL of an entity that is unique among all other entities.
     *  @param selfLink the use to extract the information for the internal @iot.selfLink
     * */
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
    @JsonSetter(value = "@iot.selfLink")
    void setSelfLink(String selfLink);

    /**
     * Provides back the ID of the specific model entry instance, as a String
     *
     * @return the id
     */
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
    @JsonGetter(value = "@iot.id")
    Object getId();

    /**
     * Sets the ID of the specific model entry instance, as a String
     *
     * @param id
     *            the id to set
     */
    @JsonPropertyDescription("id is the system-generated identifier of an entity.")
    @JsonSetter(value = "@iot.id")
    void setId(Object id);
}