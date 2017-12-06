package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface ThingsNavigationLink extends CommonControlInfo {
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Things.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Things
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Things.")
    @JsonGetter(value = "Things@iot.navigationLink")
    default String getThingsNavigationLink() {
        return getSelfLink(this.getClass().getSimpleName(),getId().toString(), "Things");
    }
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Things.
     *
     * @param  value a string that represents the relative or absolute URL that retrieves content of the Things
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Things.")
    @JsonSetter(value = "Things@iot.navigationLink")
    default void setThingsNavigationLink(String value){}
}
