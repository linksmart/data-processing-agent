package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.ModelEvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class ModelEvaluationAlgorithmBase extends EvaluationAlgorithmBase<Double> implements ModelEvaluationAlgorithm {
    protected Double target =-1.0;
    protected Double currentValue= 0.0;


    public ModelEvaluationAlgorithmBase(ComparisonMethod method, double target){
        super(method);
        this.target=target;

    }
    @Override
    public Double getTarget() {
        return target;
    }

    @Override
    public void setTarget(Double target) {

        this.target =target;
    }

    @Override
    public abstract Double calculate() ;



    @Override
    public Double getResult() {
        return currentValue;
    }

    @Override
    public void setComparisonMethod(ComparisonMethod method) {
        this.method =method;
    }

    @Override
    public boolean isReady() {
        switch (method){

            case Equal:
                return currentValue.compareTo(target) == 0;
            case More:
                return currentValue.compareTo(target) < 0;
            case MoreEqual:
                return currentValue.compareTo(target) <= 0;
            case Less:
                return currentValue.compareTo(target) > 0;
            case LessEqual:
                return currentValue.compareTo(target) >= 0;
        }
        return false;
    }



    @Override
    public void reBuild(TargetRequest evaluationAlgorithm) {

        target = evaluationAlgorithm.getThreshold();
    }

}
