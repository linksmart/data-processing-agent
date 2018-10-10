package eu.linksmart.api.event.ceml.evaluation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.ceml.evaluation.metrics.MetricDefinition;

import java.util.Map;

/**
 * Created by angel on 4/12/15.
 */
public class TargetRequest implements MetricDefinition {
    public TargetRequest(){    }
    public TargetRequest(double threshold, String name, String method) {
        this.threshold = threshold;
        this.name = name;
        this.method = method;
    }

    public Double getThreshold() {
        return threshold;
    }

    public String getName() {
        return name;
    }

    @JsonPropertyDescription("Value to be consider by the selected method as threshold for the current evaluation metrics to be achieved")
    @JsonProperty(value = "threshold")
    private double threshold = 0;

    public Double[] getThresholds() {
        return thresholds;
    }

    @JsonPropertyDescription("Value to be consider by the selected method as threshold for the current evaluation metrics to be achieved")
    @JsonProperty(value = "thresholds")
    private Double[] thresholds = null;
    @JsonPropertyDescription("Name of the metrics to use")
    @JsonProperty(value = "name")
    private String name = "";
    @JsonPropertyDescription("Evaluation methodology to compare with threshold: more, less, more or equal, less or equal, equal")
    @JsonProperty(value = "method")
    private String method = "default";


    public String getMethod() {
        return method;
    }

}
