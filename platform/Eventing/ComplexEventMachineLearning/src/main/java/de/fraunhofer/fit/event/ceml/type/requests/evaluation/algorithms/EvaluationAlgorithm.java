package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;

import java.io.Serializable;

/**
 * Created by angel on 4/12/15.
 */
public interface EvaluationAlgorithm  extends Serializable {
    double getTarget();
    void setTarget(double target);
    double calculate();
    double getResult();
    void setComparisonMethod(ComparisonMethod method);
    void reBuild(TargetRequest evaluationAlgorithm);
    boolean isReady();


    EvaluationAlgorithmExtended getExtended();

    enum ComparisonMethod{
        Equal, More, MoreEqual, Less, LessEqual
    }
}
