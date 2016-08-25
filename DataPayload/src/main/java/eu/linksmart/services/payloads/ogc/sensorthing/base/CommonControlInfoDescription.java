package eu.linksmart.services.payloads.ogc.sensorthing.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
public abstract class CommonControlInfoDescription extends CommonControlInfo {

    /**
     * This is the description of the thing entity. The content is open to
     * accommodate changes to SensorML and to support other description
     * languages.
     **/
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonProperty(value = "description")
    protected String description;

    public CommonControlInfoDescription(String description) {
        this.description = description;
    }
    public CommonControlInfoDescription() {
        this.description = null;
    }
    @Override
    public String toString(){
        if(id!=null)
            return "ID: "+id+"; Description: "+description;
        return CommonControlInfo.class.getCanonicalName();
    }

    /**
     * Returns the description of this Thing instance, as a {@link String}
     *
     * @return the description
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonProperty(value = "description")
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description of this Thing instance.
     *
     * @param description
     *            the description to set as a {@link String}
     */
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonProperty(value = "description")
    public void setDescription(String description)
    {
        this.description = description;
    }

}
