package eu.linksmart.services.event.ceml.handlers;

import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.api.event.types.EventEnvelope;
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
import sun.security.provider.SHA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public  class ListLearningHandler extends BaseListEventHandler {

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = LogManager.getLogger(ListLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
    final protected DataDescriptors descriptors;
    final private Publisher publisher;

    public ListLearningHandler(Statement statement) {
        super(statement);

        this.statement = (LearningStatement) statement;
        this.originalRequest =((LearningStatement)statement).getRequest();
        model = originalRequest.getModel();
        descriptors = originalRequest.getDescriptors();

        if((boolean)originalRequest.getSettings().getOrDefault(CEMLRequest.PUBLISH_INTERMEDIATE_STEPS,false))
            publisher = new DefaultMQTTPublisher(statement, SharedSettings.getWill(),SharedSettings.getWillTopic());
        else
            publisher=null;

    }

    @Override
    protected void processMessage(List input) {

        learn(flattList(input));

    }
    private List<Object> flattList(List input){
        List aux;
        if(input.iterator().hasNext()&&input.iterator().next() instanceof List) {
            aux= new ArrayList<>();
            ((List<List>)input).forEach(l->aux.addAll(flattList(l)));

        }else
            aux = input;

        return aux;
    }
    protected void learn(List input) {
        if(input!= null && publisher != null)
            try {
                publisher.publish(SharedSettings.getSerializer().serialize(input));
            } catch (IOException e) {
                loggerService.error(e.getMessage(),e);
            }

        // learning process with independent learning input and learning target/ground truth, and  prediction input and evaluation ground truth
        if(input!=null&&input.size()>=descriptors.size()+descriptors.getTargetSize()){
            try {
                synchronized (originalRequest) {

                    List auxInput;
                    if (input.get(0) instanceof EventEnvelope)
                        auxInput = (List) input.stream().map(m -> ((EventEnvelope) m).getValue()).collect(Collectors.toList());
                    else
                        auxInput = input;
                    List learningInput = auxInput.subList(0, descriptors.size());
                    model.learn(learningInput);

                    List groundTruth = auxInput.subList(descriptors.size(), descriptors.size() + descriptors.getTargetSize());
                    List predictionInput = auxInput.subList(descriptors.getTargetSize(), descriptors.getTargetSize() + descriptors.getInputSize());
                    Prediction prediction = model.predict(predictionInput);
                    model.getEvaluator().evaluate(prediction.getPrediction(), groundTruth);

                }
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }

        // learning process with same learning input as prediction input, and learning target/ground truth and evaluation ground truth
        }else if(input!=null&&input.size()>=descriptors.size()){
            try {
                synchronized (originalRequest) {
                    List auxInput;
                    if (input.get(0) instanceof EventEnvelope)
                        auxInput = (List) input.stream().map(m -> ((EventEnvelope) m).getValue()).collect(Collectors.toList());
                    else
                        auxInput = input;
                    // it's possible that there has been an error here.
                    List groundTruth = auxInput.subList(descriptors.getInputSize(), descriptors.getTargetSize() + descriptors.getInputSize());
                    List learningInput = auxInput.subList(0, descriptors.size());
                    List predictionInput = auxInput.subList(0, descriptors.getInputSize());

                    Prediction prediction = model.predict(predictionInput);

                    model.learn(learningInput);

                    model.getEvaluator().evaluate(prediction.getPrediction(), groundTruth);


                }
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }

        }
        if(input!=null&&input.size()>=descriptors.size()){
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


}
