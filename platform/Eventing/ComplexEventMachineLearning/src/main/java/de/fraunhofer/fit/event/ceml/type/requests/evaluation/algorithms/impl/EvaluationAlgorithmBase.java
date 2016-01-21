package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.ModelEvaluationAlgorithmExtended;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.lang.reflect.Constructor;

/**
 * Created by angel on 4/12/15.
 */
public abstract class EvaluationAlgorithmBase<T extends Object> implements EvaluationAlgorithm<T> {

    protected ComparisonMethod method= ComparisonMethod.More;
    protected static LoggerService loggerService = Utils.initDefaultLoggerService(EvaluationAlgorithmBase.class);

    protected String name;
    protected T target ;
    protected T currentValue;

    static public EvaluationAlgorithm instanceEvaluationAlgorithm(String canonicalName, String method, Object target)  {

        try {
            Class clazz = Class.forName(canonicalName);

            Constructor constructor = null;


            ComparisonMethod methodParameter = ComparisonMethod.More;
            if(method.trim().toLowerCase().equals("equal")){
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

            return  (EvaluationAlgorithm) constructor.newInstance(methodParameter,target);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        return null;
    }
    public EvaluationAlgorithmBase(){
        name = this.getClass().getSimpleName();
    }
    public EvaluationAlgorithmBase(ComparisonMethod method, T target){
        name = this.getClass().getSimpleName();

        this.target=target;

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

        this.target =target;
    }

    @Override
    public T getResult() {
        return currentValue;
    }
    @Override
    public void reBuild(TargetRequest evaluationAlgorithm) {

        if (target instanceof Object[])
            target = (T) evaluationAlgorithm.getThresholds();
        else
            target = (T) evaluationAlgorithm.getThreshold();
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
                s += " Class: " + String.valueOf(i)+"  Value: " + current[i].toString() + " Target: " + targets.toString();
                if(i+1<targets.length)
                    s+=", ";
            }

            s+="]";
            return s;
        }
        return this.getClass().getSimpleName() + ": " + currentValue.toString() + " Target: " + target.toString();
    }


}