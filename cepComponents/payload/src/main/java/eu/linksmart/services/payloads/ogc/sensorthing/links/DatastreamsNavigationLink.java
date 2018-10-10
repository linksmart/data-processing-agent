package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

public interface DatastreamsNavigationLink extends CommonControlInfo {
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Datastreams.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Datastreams
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Datastreams.")
    @JsonGetter(value = "Datastreams@iot.navigationLink")
    default String getDatastreamsNavigationLink() {
        return getSelfLink(this.getClass().getSimpleName(),getId().toString(), "Datastreams");
    }
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Datastreams.
     *
     * @param  str a string that represents the relative or absolute URL that retrieves content of the Datastreams
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Datastreams.")
    @JsonSetter(value = "Datastreams@iot.navigationLink")
    default void setDatastreamsNavigationLink(String str) {}
}
