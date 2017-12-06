package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface ObservedPropertyNavigationLink extends CommonControlInfo {

    /**
     * navigationLink is the relative or absolute URL that retrieves content of the ObservedProperty.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the ObservedProperty
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the ObservedProperty.")
    @JsonGetter(value = "ObservedProperty@iot.navigationLink")
    default String getObservedPropertNavigationLink(){ return getSelfLink(this.getClass().getSimpleName(),getId().toString(), "ObservedProperty");}
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the ObservedProperty.
     *
     * @param  value a string that represents the relative or absolute URL that retrieves content of the ObservedProperty
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the ObservedProperty.")
    @JsonSetter(value = "ObservedProperty@iot.navigationLink")
    default void setObservedPropertyNavigationLink(String value){}
}
