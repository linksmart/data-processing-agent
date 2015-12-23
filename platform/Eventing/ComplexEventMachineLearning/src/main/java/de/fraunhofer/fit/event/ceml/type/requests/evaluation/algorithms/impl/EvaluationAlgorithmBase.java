package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.impl;

import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.ModelEvaluationAlgorithmExtended;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;

import java.lang.reflect.Constructor;

/**
 * Created by angel on 4/12/15.
 */
public abstract class EvaluationAlgorithmBase implements EvaluationAlgorithm {

    protected ComparisonMethod method= ComparisonMethod.More;
    protected static LoggerService loggerService = Utils.initDefaultLoggerService(EvaluationAlgorithmBase.class);

    protected String name;


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
    public EvaluationAlgorithmBase(ComparisonMethod method){
        name = this.getClass().getSimpleName();

    }

    @Override
    public void setComparisonMethod(ComparisonMethod method) {
        this.method =method;
    }




    public ModelEvaluationAlgorithmExtended getExtended(){
        if(this instanceof ModelEvaluationAlgorithmExtended)
            return  (ModelEvaluationAlgorithmExtended)this;
        return null;

    }


}
