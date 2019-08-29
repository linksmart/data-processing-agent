package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface HistoricalLocationsNavigationLink extends CommonControlInfo{
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the HistoricalLocations.
     *
     * @param  str a string that represents the relative or absolute URL that retrieves content of the HistoricalLocations
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the FeatureOfInterest.")
    @JsonSetter(value = "HistoricalLocations@iot.navigationLink")
    default void setHistoricalLocationsNavigationLink(String str){}
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the FeatureOfInterest.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the HistoricalLocations
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the HistoricalLocations.")
    @JsonSetter(value = "HistoricalLocations@iot.navigationLink")
    default String getHistoricalLocationsNavigationLink(){return getSelfLink(this.getClass().getSimpleName(),getId().toString(), "HistoricalLocations");}
}
