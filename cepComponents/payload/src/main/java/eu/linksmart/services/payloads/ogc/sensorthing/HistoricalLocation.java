package eu.linksmart.services.payloads.ogc.sensorthing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.services.payloads.ogc.sensorthing.base.CommonControlInfoImpl;


import java.util.Date;

/**
 * Created by José Ángel Carvajal on 01.04.2016 a researcher of Fraunhofer FIT.
 */
public class HistoricalLocation extends CommonControlInfoImpl {


    /**
     * The time point/period of the location. Shall be rendered as ISO8601 time
     * point/period string
     **/
    @JsonPropertyDescription("The time point/period of the location. Shall be rendered as ISO8601 time point/period string")
    @JsonProperty(value = "time")
    protected Date time;


    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


}
