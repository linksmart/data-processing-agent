package eu.linksmart.services.payloads.ogc.sensorthing.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfoDescription;

/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
public abstract class CommonControlInfoDescriptionImpl extends CommonControlInfoImpl implements CommonControlInfoDescription {

    /**
     * This is the description of the thing entity. The content is open to
     * accommodate changes to SensorML and to support other description
     * languages.
     **/
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonProperty(value = "description")
    protected String description;

    public CommonControlInfoDescriptionImpl(String description) {
        this.description = description;
    }
    public CommonControlInfoDescriptionImpl() {
        this.description = null;
    }
    @Override
    public String toString(){
        if(id!=null)
            return "ID: "+id+"; Description: "+description;
        return CommonControlInfoImpl.class.getCanonicalName();
    }

    /**
     * Returns the description of this Thing instance, as a {@link String}
     *
     * @return the description
     */
    @Override
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
    @Override
    @JsonPropertyDescription("This is the description of the thing entity. The content is open to accommodate changes to SensorML and to support other description languages.")
    @JsonProperty(value = "description")
    public void setDescription(String description)
    {
        this.description = description;
    }

}
