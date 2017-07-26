package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface ObservationsNavigationLink extends CommonControlInfo {
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Observations.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Observations
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Observations.")
    @JsonGetter(value = "Observations@iot.navigationLink")
    default String getObservationsNavigationLink(){ return NavigationLink.getSelfLink(this.getClass().getSimpleName(),getId().toString(), "Observations");}
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Observations.
     *
     * @param  value a string that represents the relative or absolute URL that retrieves content of the Observations
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Observations.")
    @JsonSetter(value = "Observations@iot.navigationLink")
    default void setObservationsNavigationLink(String value){}
}
