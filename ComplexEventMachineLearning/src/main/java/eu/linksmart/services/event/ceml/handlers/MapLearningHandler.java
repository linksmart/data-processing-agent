package eu.linksmart.services.event.ceml.handlers;

import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;
import weka.core.pmml.jaxbbindings.TrainingInstances;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public  class MapLearningHandler extends BaseMapEventHandler {

    private static final String RETRAIN_EVERY = "RetrainEvery";
    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = Utils.initLoggingConf(MapLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
    final protected DataDescriptors descriptors;

    // if property is different than 1, the learning handler is an batch retrainer
    final protected int retrainEvery;

   // private List<LearningInstance> instances;
   private List<List> targets;
    private List<Map> rawMaps;
    private List<Map> inputs;



    public MapLearningHandler(Statement statement) {
        super(statement);

        this.statement = (LearningStatement) statement;
        this.originalRequest =((LearningStatement)statement).getRequest();
        model = originalRequest.getModel();
        descriptors = originalRequest.getDescriptors();

        if(model.getParameters().containsKey(RETRAIN_EVERY)) {
            this.retrainEvery = (int) model.getParameters().get(RETRAIN_EVERY);
            targets = new ArrayList<>();
            rawMaps= new ArrayList<>();
            inputs= new ArrayList<>();
        }else {
            retrainEvery = 1;
        }

    }
    private void elementsAssurance(Map rawMap, Map  withoutTarget, List measuredTargets){
        for (DataDescriptor descriptor : descriptors)
            if (descriptor.isTarget()) {
                if (descriptor.isAssignable(rawMap.get(descriptor.getName()).getClass())) {
                    if(model.isClassifier())
                        if (rawMap.get(descriptor.getName()) instanceof Number)
                            measuredTargets.add(rawMap.get(descriptor.getName()));
                        else if(descriptor instanceof ClassesDescriptor) {
                            ClassesDescriptor aux = (ClassesDescriptor)descriptor;
                            try {
                                measuredTargets.add(aux.getIndexClass(rawMap.get(aux.getName())));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            measuredTargets.add(rawMap.get(descriptor.getName()));
                }else
                    loggerService.error("Type mismatch between the the expected output and received one");
            } else if (descriptor.isAssignable(rawMap.get(descriptor.getName()).getClass()))
                withoutTarget.put(descriptor.getName(), rawMap.get(descriptor.getName()));
            else
                loggerService.error("Type mismatch between the the expected input and received one");
    }
    private void iterativeTraining(Map rawMap, Map  withoutTarget, List measuredTargets) throws TraceableException, UntraceableException {
        // crate a prediction for evaluation proposes
        Prediction prediction = model.predict(withoutTarget);

        // learn the current learning instance
        model.learn(rawMap);

        evaluate(prediction,measuredTargets);
    }

    private void evaluate(Prediction prediction, List target){
        // evaluating the current learning instance
        if (target.size() == 1)
            model.getEvaluator().evaluate(prediction.getPrediction(), target.get(0));
        else
            model.getEvaluator().evaluate(prediction.getPrediction(), target);
    }
    @Override
    protected void processMessage(Map eventMap) {
        if(eventMap!=null){
            try {
                Map  withoutTarget= new HashMap<>();
                List measuredTargets = new ArrayList<>();
                synchronized (originalRequest) {
                    // check if all elements to learn are there
                    elementsAssurance(eventMap,withoutTarget,measuredTargets);

                    if(retrainEvery==1)
                        iterativeTraining(eventMap,withoutTarget,measuredTargets);
                    else if (rawMaps.size()<retrainEvery) {
                        rawMaps.add(eventMap);
                        inputs.add(withoutTarget);
                        targets.add(measuredTargets);
                    }else if (rawMaps.size() == retrainEvery){
                        retrain();
                        targets = new ArrayList<>();
                        rawMaps= new ArrayList<>();
                        inputs= new ArrayList<>();
                    }

                }
                if( model.getEvaluator().isDeployable())
                    originalRequest.deploy();
                else
                    originalRequest.undeploy();

            } catch (Exception e) {
               loggerService.error(e.getMessage(),e);
            }
            originalRequest.report();
        }
    }

    private void retrain() throws TraceableException, UntraceableException {
        List<Prediction> predictions =  model.batchPredict(inputs);

        model.learn(rawMaps);

        for(int i=0; i< predictions.size();i++)
            evaluate(predictions.get(i),targets.get(i));

    }
}
