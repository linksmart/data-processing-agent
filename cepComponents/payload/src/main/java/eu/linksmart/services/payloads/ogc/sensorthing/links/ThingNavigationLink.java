package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface ThingNavigationLink extends CommonControlInfo {
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Thing.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Thing
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Thing.")
    @JsonGetter(value = "Thing@iot.navigationLink")
    default String getThingNavigationLink() {
        return getSelfLink(this.getClass().getSimpleName(),getId().toString(), "Thing");
    }
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Thing.
     *
     * @param  value a string that represents the relative or absolute URL that retrieves content of the Thing
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Thing.")
    @JsonSetter(value = "Thing@iot.navigationLink")
    default void setThingNavigationLink(String value){}
}
