package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;

import java.io.Serializable;

/**
 * Created by angel on 4/12/15.
 */
public interface EvaluationAlgorithm<T>  extends Serializable {

    void setComparisonMethod(ComparisonMethod method);
    void reBuild(TargetRequest evaluationAlgorithm);
    boolean isReady();
    T getTarget();
    void setTarget(T target);
    T getResult();
    ModelEvaluationAlgorithmExtended getExtended();
    String report();

    enum ComparisonMethod{
        Equal, More, MoreEqual, Less, LessEqual
    }
}
