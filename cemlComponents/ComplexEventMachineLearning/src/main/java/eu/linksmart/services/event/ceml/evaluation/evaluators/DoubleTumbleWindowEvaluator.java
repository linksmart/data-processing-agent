package eu.linksmart.services.event.ceml.evaluation.evaluators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;

import eu.linksmart.services.event.ceml.evaluation.metrics.base.EvaluationMetricBase;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TumbleEvaluator;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.evaluation.metrics.ModelEvaluationMetric;
import eu.linksmart.services.event.ceml.evaluation.metrics.InitialSamples;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.annotate.JsonDeserialize;


import java.util.List;
import java.util.Map;

/**
 * Created by angel on 1/12/15.
 */
@JsonDeserialize(as = DoubleTumbleWindowEvaluatorDeserializer.class)
public class DoubleTumbleWindowEvaluator extends EvaluatorBase<Number> implements TumbleEvaluator<Number>  {

    public DoubleTumbleWindowEvaluator() {
    }
    @JsonIgnore
    protected static transient Logger loggerService = LogManager.getLogger(DoubleTumbleWindowEvaluator.class);
    @JsonProperty("windowEvaluators")
    protected WindowEvaluator[] windowEvaluators = new WindowEvaluator[2];

    protected int learning = 0, learnt =0;

    protected ModelEvaluationMetric initialSamples;

    private List classes;
    protected long[][] initialSamplesMatrix;


    public List<String> getClasses() {
        return classes;
    }

    @JsonIgnore
    private   long[][] initialConfusionMatrix = null;

    public void setClasses(List classes) {
        this.classes = classes;
    }
    public DoubleTumbleWindowEvaluator(List<TargetRequest> targets) {
        this.targets =targets;


    }
    public void setInitialConfusionMatrix(long[][] initialConfusionMatrix) {
        this.initialConfusionMatrix = initialConfusionMatrix;
    }


    @Override
    public double evaluate(List<Number> predicted, List<Number> actual){
        // The evaluation only works when the classes are mutually exclusive
        if(predicted.size()!=actual.size() && actual.size()!=1)
            throw new UnsupportedOperationException("The evaluation only supports mutually exclusive classes.");

        if(initialSamples.isReady()) {
           double re= windowEvaluators[learning].evaluate( predicted, actual);
            if(learnt!=learning)
                windowEvaluators[learnt].evaluate( predicted, actual);

            trySliding();

            return re;


        }
        initialSamples.calculate();
        return 0.0;


    }


    public synchronized boolean isDeployable(){


        return windowEvaluators[learnt].isDeployable();


    }



    public synchronized boolean trySliding() {

        loggerService.info("Tumble Window evaluator is trying to sliding...");
        if(readyToSlide()) {

            if(learning==learnt){
                learning=(learning+1)%2;
            }else{
                learning=(learning+1)%2;
                windowEvaluators[learnt].reset();
                learnt=(learnt+1)%2;
            }
            loggerService.info("Sliding finished.");
            return true;

        }

        loggerService.info("Sliding not done.");
        return false;

    }

    @Override
    public synchronized boolean readyToSlide() {
        return windowEvaluators[learning].readyToSlide();

    }


    @SuppressWarnings("unchecked")
    //@Override
    public DoubleTumbleWindowEvaluator build() throws UntraceableException, TraceableException {
        boolean isSlideAfter = false;
        for(int i=0; i <targets.size();i++) {
            if (targets.get(i) != null)
                if (targets.get(i).getName().equals(InitialSamples.class.getSimpleName())) {
                    initialSamples = (ModelEvaluationMetric) EvaluationMetricBase.instanceEvaluationAlgorithm(
                            InitialSamples.class.getCanonicalName(),
                            targets.get(i).getMethod(),
                            targets.get(i).getThreshold()
                    );
                    targets.remove(i);

                }
            if (targets.get(i).getName().equals(WindowEvaluator.SlideAfter.class.getSimpleName()))
                isSlideAfter = true;
        }


        if (!isSlideAfter)
            throw  new UntraceableException("For creating sliding evaluator the SlideAfter must be defined");

        if(initialSamples== null)
            initialSamples = new InitialSamples(-1);

        windowEvaluators[0] = new WindowEvaluator(classes, targets);
        windowEvaluators[1] = new WindowEvaluator(classes,targets);

        windowEvaluators[0].setInitialConfusionMatrix(initialConfusionMatrix);
        windowEvaluators[0].setInitialSamplesMatrix(initialSamplesMatrix);

        windowEvaluators[0].build();
        windowEvaluators[1].build();

        return this;
    }
    @Override
    public void reBuild(Evaluator evaluator) {
        if(evaluator instanceof DoubleTumbleWindowEvaluator){

            windowEvaluators[0].reBuild(evaluator);
            windowEvaluators[1].reBuild(evaluator);
        }

    }
    @JsonIgnore
    @Override
    public Map<String, EvaluationMetric<Number>> getEvaluationAlgorithms() {
        return windowEvaluators[learnt].getEvaluationAlgorithms();
    }

    @Override
    public String report() {
        if(learning == learnt)
            return  "Learning window report > "+windowEvaluators[learning].report();

        return  "Learning window report > "+windowEvaluators[learning].report()+" | "+
                "Learnt window report > "+windowEvaluators[learnt].report();
    }



    @Override
    public void destroy() throws Exception {
        // nothing
    }

    public void setInitialSamplesMatrix(long[][] initialSamplesMatrix) {
        this.initialSamplesMatrix = initialSamplesMatrix;
    }
}