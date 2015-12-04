package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

/**
 * Created by angel on 4/12/15.
 */
public interface EvaluationAlgorithm {
    double getTarget();
    void setTarget(double target);
    double calculate();
    double getResult();
    void setComparisonMethod(ComparisonMethod method);
    boolean isReady();


    EvaluationAlgorithmExtended getExtended();

    enum ComparisonMethod{
        Equal, More, MoreEqual, Less, LessEqual
    }
}
