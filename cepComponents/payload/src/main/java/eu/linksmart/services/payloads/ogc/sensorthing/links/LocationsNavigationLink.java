package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface LocationsNavigationLink extends CommonControlInfo{
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Locations.
     *
     * @param  str a string that represents the relative or absolute URL that retrieves content of the Locations
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Locations.")
    @JsonSetter(value = "Locations@iot.navigationLink")
    default void setLocationsNavigationLink(String str){}
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the FeatureOfInterest.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Locations
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Locations.")
    @JsonSetter(value = "Locations@iot.navigationLink")
    default String getLocationsNavigationLink(){return NavigationLink.getSelfLink(this.getClass().getSimpleName(),getId().toString(), "Locations");}
}
