package eu.linksmart.services.event.ceml.evaluation.evaluators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.metrics.Samples;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.EvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by devasya on 7/20/2016.
 * This class is takes care of evaluating with the help of different EvaluationMetrics.
 */
@JsonDeserialize(as = EvaluatorBaseDeserializer.class)
public abstract class GenericEvaluator<T> extends EvaluatorBase<T> {
    @JsonIgnore
    protected static final transient Logger loggerService = LogManager.getLogger(WindowEvaluator.class);


    protected Map<String,EvaluationMetricBase<Double>> evaluationAlgorithms = new HashMap<>();

    protected List<TargetRequest> targets;


    EvaluationMetricBase<Double> samples;
    public GenericEvaluator( List<TargetRequest> targets) {
        this.targets=targets;

    }
    public GenericEvaluator() {
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
            if(targets!=null) {
                for (TargetRequest target : targets) {
                    String algorithm;

                    algorithm = this.getClass().getCanonicalName() + "$" + target.getName();

                    evaluationAlgorithms.put(
                            target.getName(),
                            (EvaluationMetricBase<Double>) instanceEvaluationAlgorithm(algorithm, target.getThreshold(), EvaluationMetric.ComparisonMethod.valueOf(target.getMethod().substring(0,1).toUpperCase()+target.getMethod().substring(1).toLowerCase()))
                    );


                }
            }
            return this;

        } catch (Exception e) {
            throw new UnknownUntraceableException(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() throws Exception {
        // nothing
    }

    private String getCanonicalName(String name){
        if(name.contains("$"))
            return name;
        else
            return this.getClass().getCanonicalName() + "$" + name;
    }

    public <M extends Number> EvaluationMetric<T> instanceEvaluationAlgorithm(String name, M target, EvaluationMetric.ComparisonMethod method)  {

        try {
            Class<EvaluationMetric<T>> clazz = (Class<EvaluationMetric<T>>) Class.forName(getCanonicalName(name));

            Constructor<EvaluationMetric<T>> constructor = null;



            try {
                constructor = clazz.getConstructor(/*because is a child class of this*/this.getClass(), target.getClass());

            }catch (NoSuchMethodException e){
                constructor = clazz.getConstructor(/*because is a child class of this*/this.getClass(), /*Try to extract the primitive type*/ (Class)target.getClass().getField("TYPE").get(null));

            }


            EvaluationMetric<T> metric = constructor.newInstance(this,target);
            metric.setComparisonMethod(method);

            return metric;
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        return null;
    }
    public <M extends Number> EvaluationMetric<T> instanceEvaluationAlgorithm(String name)  {

        try {
            Class<EvaluationMetric<T>> clazz = (Class<EvaluationMetric<T>>) Class.forName(getCanonicalName(name));

            Constructor<EvaluationMetric<T>> constructor = null;


            try {
                constructor = clazz.getConstructor(/*because is a child class of this*/this.getClass());

            }catch (NoSuchMethodException e){
                constructor = clazz.getConstructor(/*because is a child class of this*/this.getClass());

            }

            return  constructor.newInstance(this);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }
        return null;
    }

}
