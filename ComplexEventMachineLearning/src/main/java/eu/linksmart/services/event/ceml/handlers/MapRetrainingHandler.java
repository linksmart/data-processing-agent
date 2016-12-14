package eu.linksmart.services.event.ceml.handlers;

import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.ClassesDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 14.12.2016 a researcher of Fraunhofer FIT.
 */
public class MapRetrainingHandler  extends BaseMapEventHandler {

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = Utils.initLoggingConf(MapLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
    final protected DataDescriptors descriptors;

    public MapRetrainingHandler(Statement statement) {
        super(statement);

        this.statement = (LearningStatement) statement;
        this.originalRequest =((LearningStatement)statement).getRequest();
        model = originalRequest.getModel();
        descriptors = originalRequest.getDescriptors();

    }


    @Override
    protected void processMessage(Map eventMap) {
        if(eventMap!=null){
            try {
                List  withoutTarget= new ArrayList<>();
                List measuredTargets = new ArrayList<>();
                synchronized (originalRequest) {
                    for (DataDescriptor descriptor : descriptors)
                        if (descriptor.isTarget()) {
                            if (descriptor.isAssignable(eventMap.get(descriptor.getName()).getClass())) {
                                if(model.isClassifier())
                                    if (eventMap.get(descriptor.getName()) instanceof Number)
                                        measuredTargets.add(eventMap.get(descriptor.getName()));
                                    else if(descriptor instanceof ClassesDescriptor) {
                                        ClassesDescriptor aux = (ClassesDescriptor)descriptor;
                                        measuredTargets.add(aux.getIndexClass(eventMap.get(aux.getName())));
                                    }
                                    else
                                        measuredTargets.add(eventMap.get(descriptor.getName()));
                            }else
                                loggerService.error("Type mismatch between the the expected output and received one");
                        } else if (descriptor.isAssignable(eventMap.get(descriptor.getName()).getClass()))
                            withoutTarget.add(eventMap.get(descriptor.getName()));
                        else
                            loggerService.error("Type mismatch between the the expected input and received one");


                    originalRequest.setLastPrediction(model.predict(withoutTarget));
                    model.learn(eventMap);
                    if(measuredTargets.size()==1)
                        model.getEvaluator().evaluate(originalRequest.getLastPrediction().getPrediction(), measuredTargets.get(0));
                    else
                        model.getEvaluator().evaluate(originalRequest.getLastPrediction().getPrediction(), measuredTargets);

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
}
