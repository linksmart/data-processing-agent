package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;
import eu.linksmart.services.payloads.ogc.sensorthing.Datastream;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface DatastreamNavigationLink extends CommonControlInfo {
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Datastream.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Datastream
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Datastream.")
    @JsonGetter(value = "Datastream@iot.navigationLink")
    default String getDatastreamNavigationLink() {
        return getSelfLink(this.getClass().getSimpleName(),getId().toString(), "Datastream");
    }
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Datastream.
     *
     * @param  str a string that represents the relative or absolute URL that retrieves content of the Datastream
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Datastream.")
    @JsonSetter(value = "Datastream@iot.navigationLink")
    default void setDatastreamNavigationLink(String str) {}
}
