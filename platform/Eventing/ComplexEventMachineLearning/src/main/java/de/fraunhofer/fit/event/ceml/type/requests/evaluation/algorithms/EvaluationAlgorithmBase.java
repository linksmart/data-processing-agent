package de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms;

import com.google.gson.*;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.TargetRequest;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.gson.GsonSerializable;
import eu.linksmart.gc.utils.logging.LoggerService;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * Created by angel on 4/12/15.
 */
public abstract class EvaluationAlgorithmBase implements EvaluationAlgorithm {
    protected double target =-1;
    protected double currentValue= 0;
    protected ComparisonMethod method= ComparisonMethod.More;
    protected static LoggerService loggerService = Utils.initDefaultLoggerService(EvaluationAlgorithmBase.class);

    protected String name;


    static public EvaluationAlgorithm instanceEvaluationAlgorithm(String canonicalName, String method, double target)  {

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

            constructor = clazz.getConstructor(ComparisonMethod.class, double.class);


            return  (EvaluationAlgorithm) constructor.newInstance(methodParameter,target);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        return null;
    }
    public EvaluationAlgorithmBase(){
        name = this.getClass().getSimpleName();
    }
    public EvaluationAlgorithmBase(ComparisonMethod method, double target){
        name = this.getClass().getSimpleName();
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


    public EvaluationAlgorithmExtended getExtended(){
        if(this instanceof EvaluationAlgorithmExtended)
            return  (EvaluationAlgorithmExtended)this;
        return null;

    }

    @Override
    public void reBuild(TargetRequest evaluationAlgorithm) {

        target = evaluationAlgorithm.getThreshold();
    }

}
