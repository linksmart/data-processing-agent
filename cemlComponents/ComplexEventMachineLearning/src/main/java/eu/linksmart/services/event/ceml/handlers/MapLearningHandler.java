package eu.linksmart.services.event.ceml.handlers;

import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.impl.ExtractedElements;
import eu.linksmart.api.event.types.impl.SchemaNode;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public  class MapLearningHandler extends BaseMapEventHandler {

    private static final String RETRAIN_EVERY = "RetrainEvery";
    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = LogManager.getLogger(MapLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
    @Deprecated
    final protected DataDescriptors descriptors;

    // if property is different than 1, the learning handler is an batch retrainer
    final protected int retrainEvery;

   // private List<LearningInstance> instances;
   private List<List> targets, rawMaps, inputs;
    private List<Map> rawMapsLegacy;
    private List<Map> inputsLegacy;
    final private Publisher publisher;
    final protected SchemaNode schema;
    List<ExtractedElements> accInput = new ArrayList<>();


    public MapLearningHandler(Statement statement) throws TraceableException, UntraceableException {
        super(statement);

        this.statement = (LearningStatement) statement;
        this.originalRequest =((LearningStatement)statement).getRequest();
        model = originalRequest.getModel();
        descriptors = originalRequest.getDescriptors();
        schema = originalRequest.getDataSchema();

        if(model.getParameters().containsKey(RETRAIN_EVERY)) {
            this.retrainEvery = (int) model.getParameters().get(RETRAIN_EVERY);
            targets = new ArrayList<>();
            rawMaps= new ArrayList<>();
            inputs= new ArrayList<>();
        }else {
            retrainEvery = 1;
        }
        if((boolean)originalRequest.getSettings().getOrDefault(CEMLRequest.PUBLISH_INTERMEDIATE_STEPS,false))
            try {
                publisher = new DefaultMQTTPublisher(statement, SharedSettings.getWill(),SharedSettings.getWillTopic());
            } catch (TraceableException | UntraceableException e) {
                loggerService.error(e.getMessage(),e);
                if(statement.isEssential())
                    System.exit(-1);
                throw e;
            }
        else
            publisher=null;
    }

    @Override
    protected void processMessage(Map[] events) {
        if(events.length > 1)
            loggerService.warn("Learning events arriving as Map[] they are process individually");

        if(events!=null)
            for (Map m: events)
                processMessage(m);
    }

    @Override
    protected void processLeavingMessage(Map[] events) {
        if(events.length > 1)
            loggerService.warn("Learning events arriving remove streams, they are handle as inserting streams ");

        if(events!=null)
            processMessage(events);
    }
    @Deprecated
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

    @Deprecated
    private void iterativeTrainingLegacy(Map rawMap, Map  withoutTarget, List measuredTargets) throws TraceableException, UntraceableException {
        // crate a prediction for evaluation proposes
        Prediction prediction = model.predict(withoutTarget);

        // learn the current learning instance
        model.learn(rawMap);

        evaluate(prediction,measuredTargets);
    }
    private void evaluate(Prediction prediction, List target){
        // evaluating the current learning instance
        model.getEvaluator().evaluate(prediction.getPrediction() instanceof List?(List)prediction.getPrediction(): Collections.singletonList(prediction.getPrediction()), target);
    }
    protected void processMessage(Map eventMap) {
        if(eventMap!= null && publisher != null)
            try {
                publisher.publish(SharedSettings.getSerializer().serialize(eventMap));
            } catch (IOException e) {
                loggerService.error(e.getMessage(),e);
            }

            if(schema==null) {
                 processMessageLegacy(eventMap);
                 return;
            }
            if(eventMap.size() == 1 && schema.size() !=1  &&  !schema.getProperties().containsKey(eventMap.keySet().iterator().next()) )
                eventMap = (Map) eventMap.values().iterator().next();


        ExtractedElements elements = schema.collect(eventMap);
        if(elements!=null){

            if((boolean)originalRequest.getSettings().getOrDefault(CEMLRequest.PUBLISH_INTERMEDIATE_STEPS,false))
                originalRequest.report(eventMap);

            try {
                synchronized (originalRequest) {
                    try {
                        if (retrainEvery == 1) { // iterative
                            Prediction prediction = model.predict(elements.getInputsList());
                            model.learn(elements.getInputsList(), elements.getTargetsList());
                            evaluate(prediction, elements.getTargetsList());

                        } else if (rawMaps.size() < retrainEvery) {
                            targets.add(elements.getTargetsList());
                            rawMaps.add(elements);
                            inputs.add(elements.getInputsList());
                            if (rawMaps.size() == retrainEvery)
                                retrain();

                        }
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(),e);
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
    @Deprecated
    protected void processMessageLegacy(Map eventMap) {

        if(eventMap!=null){

            if((boolean)originalRequest.getSettings().getOrDefault(CEMLRequest.PUBLISH_INTERMEDIATE_STEPS,false))
                originalRequest.report(eventMap);

            try {
                Map  withoutTarget= new HashMap<>();
                List measuredTargets = new ArrayList<>();
                synchronized (originalRequest) {
                    // check if all elements to learn are there
                    elementsAssurance(eventMap,withoutTarget,measuredTargets);

                    if(retrainEvery==1)
                        iterativeTrainingLegacy(eventMap,withoutTarget,measuredTargets);
                    else if (rawMaps.size()<retrainEvery) {
                        rawMapsLegacy.add(eventMap);
                        inputsLegacy.add(withoutTarget);
                        targets.add(measuredTargets);
                        if (rawMaps.size() == retrainEvery)
                            retrain();
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
        List<Prediction> predictions = model.batchPredict(inputs);

        model.batchLearn(rawMaps);

        for (int i = 0; i < predictions.size(); i++)
            evaluate(predictions.get(i), targets.get(i));


        targets = new ArrayList<>();
        rawMaps = new ArrayList<>();
        inputs = new ArrayList<>();


    }
}
