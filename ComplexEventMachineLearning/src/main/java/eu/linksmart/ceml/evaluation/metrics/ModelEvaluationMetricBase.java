package eu.linksmart.ceml.evaluation.metrics;

import eu.linksmart.api.event.ceml.evaluation.metrics.ModelEvaluationMetric;

/**
 * Created by José Ángel Carvajal on 23.12.2015 a researcher of Fraunhofer FIT.
 */
public abstract class ModelEvaluationMetricBase extends EvaluationMetricBase<Double> implements ModelEvaluationMetric<Double> {




    public ModelEvaluationMetricBase(ComparisonMethod method, Double target){
        super(method, target);
        currentValue = 0.0;

    }

    @Override
    public abstract Double calculate() ;





    @Override
    public boolean isReady() {
        switch (method){

            case Equal:
                return currentValue.compareTo(target) == 0;
            case More:
                return currentValue.compareTo(target) > 0;
            case MoreEqual:
                return currentValue.compareTo(target) >= 0;
            case Less:
                return currentValue.compareTo(target) < 0;
            case LessEqual:
                return currentValue.compareTo(target) <= 0;
        }
        return false;
    }

    @Override
    public void reset() {
        currentValue = 0.0;
    }

    @Override
    public ModelEvaluationMetricBase build(){
        // TODO aot-generated
        return this;
    }

}
