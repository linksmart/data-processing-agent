package eu.linksmart.services.event.ceml.handlers;

import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.impl.ExtractedElements;
import eu.linksmart.api.event.types.impl.SchemaNode;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
import eu.linksmart.services.event.handler.base.BaseListEventHandler;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.utils.configuration.Configurator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public  class   ListLearningHandler extends BaseListEventHandler {

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = LogManager.getLogger(ListLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
//    @Deprecated
//    final protected DataDescriptors descriptors;
    final protected SchemaNode schema;
    final private Publisher publisher;

    public ListLearningHandler(Statement statement) throws TraceableException, UntraceableException {
        super(statement);

        this.statement = (LearningStatement) statement;
        this.originalRequest =((LearningStatement)statement).getRequest();
        model = originalRequest.getModel();

        schema = model.getDataSchema();

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
    protected void processLeavingMessage(List<Object> events) {
        loggerService.warn("Getting leaving messages that are not being processed");
    }

    @Override
    protected void processMessage(List input) {
        verbose(input);

        ExtractedElements elements = schema.collect(input);
        if(elements==null) {
            loggerService.error("Incoming data from learning statement  id " + statement.getId() + " do not match which data schema!");
            return;
        }

        // learning process with independent learning input and learning target/ground truth, and  prediction input and evaluation ground truth
        if(input!=null&&elements.size()>=schema.size() && input.size()>schema.size()){
            try {
                synchronized (originalRequest) {
                    Prediction prediction = model.predict(elements.getInputsList());
                    model.learn(elements.getInputsList(),elements.getTargetsList());
                    List auxInput;

                    if (input.get(0) instanceof EventEnvelope)
                        auxInput = (List) input.stream().map(m -> ((EventEnvelope) m).getValue()).collect(Collectors.toList());
                    else
                        auxInput = input;

                    List groundTruth = auxInput.subList(schema.size(), schema.size() + schema.getTargetSize());

                    model.getEvaluator().evaluate(prediction.getPrediction() instanceof List?(List)prediction.getPrediction(): Collections.singletonList(prediction.getPrediction()), groundTruth);

                }
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }

            // learning process with same learning input as prediction input, and learning target/ground truth and evaluation ground truth
        }else if(input!=null&&input.size()>=schema.size()){
            try {
                synchronized (originalRequest) {
                    Prediction prediction = model.predict(elements.getInputsList());
                    model.learn(elements.getInputsList(),elements.getTargetsList());
                    model.getEvaluator().evaluate(prediction.getPrediction() instanceof List?(List)prediction.getPrediction(): Collections.singletonList(prediction.getPrediction()), elements.getTargetsList());


                }
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }

        }
        if(input!=null&&input.size()>=schema.size())
            asses();

    }


    protected void verbose(Object input){
        if(input!= null && publisher != null)
            try {
                publisher.publish(SharedSettings.getSerializer().serialize(input));
            } catch (IOException e) {
                loggerService.error(e.getMessage(),e);
            }
    }
    protected void asses(){
        try {

            if(model.getEvaluator().isDeployable())
                originalRequest.deploy();
            else
                originalRequest.undeploy();

        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }

        originalRequest.report();
    }

}
