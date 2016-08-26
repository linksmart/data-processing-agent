package eu.linksmart.ceml.evaluation.metrics.base;

import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.evaluation.metrics.MetricDefinition;
import eu.linksmart.api.event.ceml.evaluation.metrics.ModelEvaluationAlgorithmExtended;
import eu.linksmart.gc.utils.function.Utils;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import com.rits.cloning.Cloner;

/**
 * Created by angel on 4/12/15.
 */
public abstract class EvaluationMetricBase<T extends Object> implements EvaluationMetric<T> {

    protected ComparisonMethod method= ComparisonMethod.More;
    protected static Logger loggerService = Utils.initLoggingConf(EvaluationMetricBase.class);

    protected String name;
    protected T target ;
    public T currentValue;
    protected Cloner cloner = new Cloner();

    static public EvaluationMetric instanceEvaluationAlgorithm(String canonicalName, String method, Object target)  {

        try {
            Class clazz = Class.forName(canonicalName);

            Constructor constructor = null;


            ComparisonMethod methodParameter = ComparisonMethod.More;
            if("equal".equals(method.trim().toLowerCase())){
                methodParameter =ComparisonMethod.Equal;

            } else if(method.trim().toLowerCase().contains("smaller")|| method.trim().toLowerCase().contains("less")){
                if(method.trim().toLowerCase().contains("equal")){
                    methodParameter =ComparisonMethod.LessEqual;
                }else
                    methodParameter =ComparisonMethod.Less;
            } else if(method.trim().toLowerCase().contains("bigger")|| method.trim().toLowerCase().contains("more")){
                if(method.trim().toLowerCase().contains("equal")){
                    methodParameter =ComparisonMethod.MoreEqual;
                }
            }

            if(target instanceof Double)
                constructor = clazz.getConstructor(ComparisonMethod.class, double.class);
            else if(target instanceof Double[])
                constructor = clazz.getConstructor(ComparisonMethod.class, Double[].class);

            return  (EvaluationMetric) constructor.newInstance(methodParameter,target);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        return null;
    }
    public EvaluationMetricBase(){
        name = this.getClass().getSimpleName();
    }
    public EvaluationMetricBase(ComparisonMethod method, T target){

        // TODO: broke, the target copy the reference of the object insted of making a new reference,
        // I solved this by creating the reference outside the constructor
        name = this.getClass().getSimpleName();
        this.method = method;
        this.target=cloner.deepClone(target);

    }

    @Override
    public void setComparisonMethod(ComparisonMethod method) {
        this.method =method;
    }


    @Override
    public T getTarget() {
        return target;
    }

    @Override
    public void setTarget(T target) {

        this.target =cloner.deepClone(target);
    }

    @Override
    public T getResult() {
        return currentValue;
    }
    @Override
    public void reBuild(MetricDefinition<T> evaluationAlgorithm) {

      //  if (target instanceof Object[])
       //     target = cloner.deepClone((T) evaluationAlgorithm.getThresholds());
       // else
            target = cloner.deepClone( evaluationAlgorithm.getThreshold());
    }

    public ModelEvaluationAlgorithmExtended getExtended(){
        if(this instanceof ModelEvaluationAlgorithmExtended)
            return  (ModelEvaluationAlgorithmExtended)this;
        return null;

    }
    @Override
    public String report() {
        if( target instanceof Object[]) {
            Object[] targets = (Object[])target,current = (Object[])currentValue;
            String s= this.getClass().getSimpleName() + ": [";
            for (int i = 0; i<targets.length; i++) {
                s += " Class: " + String.valueOf(i)+"  Value: " + current[i].toString() + " Target: " + Arrays.toString(targets);
                if(i+1<targets.length)
                    s+=", ";
            }

            s+="]";
            return s;
        }
        loggerService.debug("(D) Algorithm ID "+String.valueOf(System.identityHashCode(this))+" CurrentValue ID "+String.valueOf(System.identityHashCode(currentValue)) +" Target ID " + String.valueOf(System.identityHashCode(target)));
        return "\n (R)"+this.getClass().getSimpleName() + "> current: " + currentValue.toString() + " Target : " + target.toString() ;
    }
    @Override
    public double getNormalizedResult(){
        if(((Double)getResult()/((Double)getTarget()))>1.0)
            return 1.0;
        return ((Double)getResult()/((Double)getTarget()));
    }

    @Override
    public boolean isControlMetric() {
        return false;
    }


}
