package de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl;

/**
 * Created by angel on 4/12/15.
 */
public class TargetRequest {
    public double getThreshold() {
        return threshold;
    }

    public String getName() {
        return name;
    }

    private double threshold = 0;
    private String name = "";
    private String method = "more";

    public String getMethod() {
        return method;
    }

}
