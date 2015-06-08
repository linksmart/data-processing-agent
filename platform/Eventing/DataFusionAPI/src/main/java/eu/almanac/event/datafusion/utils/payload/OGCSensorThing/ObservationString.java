package eu.almanac.event.datafusion.utils.payload.OGCSensorThing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;

/**
 * Created by Caravajal on 20.05.2015.
 */
public class ObservationString extends Observation {

    @Override
    public String getResultValue() {
        return resultValue;
    }

    @JsonPropertyDescription("The estimated value of an observedProperty from the observation. This will be intended as a Measure with value and unit.")
    @JsonProperty(value = "ResultValue")
    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
    }

    protected String resultValue;
}