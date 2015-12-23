package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.ModelEvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class ModelEvaluationAlgorithmBase extends EvaluationAlgorithmBase implements ModelEvaluationAlgorithm {
    protected double target =-1;
    protected double currentValue= 0;


    public ModelEvaluationAlgorithmBase(ComparisonMethod method, double target){
        super(method);
        this.target=target;

    }
    @Override
    public double getTarget() {
        return target;
    }

    @Override
    public void setTarget(double target) {

        this.target =target;
    }

    @Override
    public abstract double calculate() ;



    @Override
    public double getResult() {
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
                return currentValue== target;
            case More:
                return currentValue >target;
            case MoreEqual:
                return currentValue >=target;
            case Less:
                return currentValue < target;
            case LessEqual:
                return currentValue <=target;
        }
        return false;
    }



    @Override
    public void reBuild(TargetRequest evaluationAlgorithm) {

        target = evaluationAlgorithm.getThreshold();
    }

}
