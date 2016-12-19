package eu.linksmart.services.event.ceml.handlers;

import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.12.2016 a researcher of Fraunhofer FIT.
 */
public class LearningInstance {
    protected Map inputInstance;
    protected Map rawValues;
    protected List target;
    protected List groundTruth;

    public LearningInstance(Map inputInstance, Map rawValues, List target, List groundTruth) {
        this.inputInstance = inputInstance;
        this.rawValues = rawValues;
        this.target = target;
        this.groundTruth = groundTruth;
    }

    public Map getInputInstance() {
        return inputInstance;
    }

    public void setInputInstance(Map inputInstance) {
        this.inputInstance = inputInstance;
    }

    public Map getRawValues() {
        return rawValues;
    }

    public void setRawValues(Map rawValues) {
        this.rawValues = rawValues;
    }

    public List getTarget() {
        return target;
    }

    public void setTarget(List target) {
        this.target = target;
    }

    public List getGroundTruth() {
        return groundTruth;
    }

    public void setGroundTruth(List groundTruth) {
        this.groundTruth = groundTruth;
    }
}
