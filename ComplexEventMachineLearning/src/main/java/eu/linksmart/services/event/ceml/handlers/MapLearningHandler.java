package eu.linksmart.services.event.ceml.handlers;

import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.api.event.ceml.CEMLRequest;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.event.ceml.intern.Const;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public  class MapLearningHandler extends BaseMapEventHandler {

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = Utils.initLoggingConf(MapLearningHandler.class);
    final protected LearningStatement statement;
    final protected CEMLRequest originalRequest;
    final protected Model model;
    final protected DataDescriptors descriptors;

    public MapLearningHandler(Statement statement) {
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
                Map  withoutTarget= new HashMap<>();
                List measuredTargets = new ArrayList<>();
                synchronized (originalRequest) {
                    for (DataDescriptor descriptor : descriptors)
                        if (descriptor.isTarget()) {
                            if (descriptor.getNativeType().isAssignableFrom(eventMap.get(descriptor.getName()).getClass()))
                                measuredTargets.add(eventMap.get(descriptor.getName()));
                            else
                                loggerService.error("Type mismatch between the the expected output and received one");
                        } else if (descriptor.getNativeType().isAssignableFrom(eventMap.get(descriptor.getName()).getClass()))
                            withoutTarget.put(descriptor.getName(), eventMap.get(descriptor.getName()));
                        else
                            loggerService.error("Type mismatch between the the expected input and received one");


                    Prediction prediction = model.predict(withoutTarget);
                    model.learn(eventMap);

                    model.getEvaluator().evaluate(prediction, measuredTargets);
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
