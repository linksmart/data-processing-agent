package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoDescription;


/**
 * Created by José Ángel Carvajal on 04.04.2016 a researcher of Fraunhofer FIT.
 */
public class ObservedProperty extends CommonControlInfoDescription {

    @JsonProperty(value = "name")
    protected String name;

    /**
     * The URI of the observedProperty/phenomenon.
     */
    @JsonProperty(value = "definition")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    private String definition;

    /**
     * Provides the URN(URI) of the observed property or phenomenon modeled by
     * this instance.
     *
     * @return the uri The phenomenon URI as a {@link String}
     */
    @JsonProperty(value = "URI")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    public String getDefinition()
    {
        return definition;
    }

    /**
     * Sets the URN(URI) of the observed property or phenomenon modeled by this
     * instance.
     *
     * @param uri
     *            the uri to set.
     */
    @JsonProperty(value = "URI")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    public void setDefinition(String uri)
    {
        this.definition = uri;
    }
    @JsonProperty(value = "name")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    public String getName() {
        return name;
    }
    @JsonProperty(value = "name")
    @JsonPropertyDescription("The URI of the observedProperty/phenomenon.")
    public void setName(String name) {
        this.name = name;
    }



}
