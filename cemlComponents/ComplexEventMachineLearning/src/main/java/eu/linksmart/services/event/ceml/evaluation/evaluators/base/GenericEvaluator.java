package eu.linksmart.services.event.ceml.evaluation.evaluators.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.ceml.evaluation.evaluators.WindowEvaluator;
import eu.linksmart.services.event.ceml.evaluation.metrics.Samples;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.EvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.services.utils.function.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devasya on 7/20/2016.
 * This class is takes care of evaluating with the help of different EvaluationMetrics.
 */
public abstract class GenericEvaluator<T> extends EvaluatorBase<T> {
    @JsonIgnore
    protected static Logger loggerService = LogManager.getLogger(WindowEvaluator.class);

    protected Map<String,EvaluationMetricBase<Double>> evaluationAlgorithms = new HashMap<>();

    protected List<TargetRequest> targets;

    EvaluationMetricBase<Double> samples;
    public GenericEvaluator( List<TargetRequest> targets) {
        this.targets=targets;

    }




    @Override
    public boolean isDeployable(){

        for(EvaluationMetric algorithm: evaluationAlgorithms.values())
            if(!algorithm.isReady())
                return false;
        return true;


    }

    @Override
    public void reBuild(Evaluator<T> evaluator) {
        if(evaluator instanceof DoubleTumbleWindowEvaluator){
            DoubleTumbleWindowEvaluator aux = (DoubleTumbleWindowEvaluator)evaluator;
            for(TargetRequest algorithm: aux.getTargets()) {
                evaluationAlgorithms.get(algorithm.getName()).reBuild(algorithm);
            }

        }

    }
    @Override
    public Map<String, EvaluationMetric< Number>> getEvaluationAlgorithms(){
        return (Map)evaluationAlgorithms;
    }
    @Override
    public String report(){
        String report = "";
        for(EvaluationMetric algorithm: evaluationAlgorithms.values()){
            report += algorithm.report()+" || ";
        }
        return report;
    }


    @Override
    public Evaluator<T> build() throws TraceableException,UntraceableException {
        try {

            samples = evaluationAlgorithms.get(Samples.class.getSimpleName());
            for(TargetRequest target:targets){
                String algorithm;

                    algorithm = this.getClass().getCanonicalName()+"$"+target.getName();

                evaluationAlgorithms.put(
                        target.getName(),
                        (EvaluationMetricBase<Double>) instanceEvaluationAlgorithm(algorithm,target.getMethod(),target.getThreshold())
                );


            }
        return this;

        }catch (Exception e){
            throw new UnknownUntraceableException(e.getMessage(),e);
        }
    }

    @Override
    public void destroy() throws Exception {
        // nothing
    }


    public <M extends Number> EvaluationMetric<T> instanceEvaluationAlgorithm(String canonicalName, String method, M target)  {

        try {
            Class<EvaluationMetric<T>> clazz = (Class<EvaluationMetric<T>>) Class.forName(canonicalName);

            Constructor<EvaluationMetric<T>> constructor = null;


            EvaluationMetric.ComparisonMethod methodParameter = null;
            if("equal".equalsIgnoreCase(method.trim())){
                methodParameter = EvaluationMetric.ComparisonMethod.Equal;

            } else if(method.trim().toLowerCase().contains("smaller")|| method.trim().toLowerCase().contains("less")){
                if(method.trim().toLowerCase().contains("equal")){
                    methodParameter = EvaluationMetric.ComparisonMethod.LessEqual;
                }else
                    methodParameter = EvaluationMetric.ComparisonMethod.Less;
            } else if(method.trim().toLowerCase().contains("bigger")|| method.trim().toLowerCase().contains("more")){
                if(method.trim().toLowerCase().contains("equal")){
                    methodParameter = EvaluationMetric.ComparisonMethod.MoreEqual;
                }
            }



            try {
                constructor = clazz.getConstructor(/*because is a child class of this*/this.getClass(),EvaluationMetric.ComparisonMethod.class,  target.getClass());

            }catch (NoSuchMethodException e){
                constructor = clazz.getConstructor(/*because is a child class of this*/this.getClass(),EvaluationMetric.ComparisonMethod.class, /*Try to extract the primitive type*/ (Class)target.getClass().getField("TYPE").get(null));

            }


            return  constructor.newInstance(this,methodParameter,target);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        return null;
    }

}
