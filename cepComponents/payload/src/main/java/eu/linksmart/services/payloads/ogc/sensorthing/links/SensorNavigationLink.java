package eu.linksmart.services.payloads.ogc.sensorthing.links;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.linksmart.services.payloads.ogc.sensorthing.CommonControlInfo;

/**
 * Created by José Ángel Carvajal on 26.07.2017 a researcher of Fraunhofer FIT.
 */
public interface SensorNavigationLink extends CommonControlInfo {
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Sensor.
     *
     * @return  a string that represents the relative or absolute URL that retrieves content of the Sensor
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Sensor.")
    @JsonGetter(value = "Sensor@iot.navigationLink")
    default  String getSensorNavigationLink() {
        return getSelfLink(this.getClass().getSimpleName(),getId().toString(), "Sensor");
    }
    /**
     * navigationLink is the relative or absolute URL that retrieves content of the Sensor.
     *
     * @param  value a string that represents the relative or absolute URL that retrieves content of the Sensor
     */
    @JsonPropertyDescription("navigationLink is the relative or absolute URL that retrieves content of the Sensor.")
    @JsonSetter(value = "Sensor@iot.navigationLink")
    default  void setSensorNavigationLink(String value){}
}
