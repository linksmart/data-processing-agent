package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface FeatureOfInterestNavigationLink extends CommonControlInfo{
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the FeatureOfInterest.
     *
     * @param  str a string that represents the relative or absolute URL that retrieves content of the FeatureOfInterest
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the FeatureOfInterest.")
    @JsonSetter(value = "FeatureOfInterest@iot.navigationLink")
    default void setFeatureOfInterestNavigationLink(String str){}
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the FeatureOfInterest.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the FeatureOfInterest
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the FeatureOfInterest.")
    @JsonGetter(value = "FeatureOfInterest@iot.navigationLink")
    default String getFeatureOfInterestNavigationLink(){
        return getSelfLink(this.getClass().getSimpleName(),getId()!=null?getId().toString():null, "FeatureOfInterest");}
}
