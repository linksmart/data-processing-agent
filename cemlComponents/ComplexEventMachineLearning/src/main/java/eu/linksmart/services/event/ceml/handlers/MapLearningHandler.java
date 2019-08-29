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

    static protected Configurator conf = Configurator.getDefaultConfig();
    static protected Logger loggerService = LogManager.getLogger(MapLearningHandler.class);



    final private Publisher publisher;
    List<ExtractedElements> accInput = new ArrayList<>();


    public MapLearningHandler(Statement statement) throws TraceableException, UntraceableException {
        super(statement);

       // descriptors = model.getDescriptors();


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

    protected void processMessage(Map eventMap) {
        if(eventMap!= null && publisher != null)
            try {
                publisher.publish(SharedSettings.getSerializer().serialize(eventMap));
            } catch (IOException e) {
                loggerService.error(e.getMessage(),e);
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

                        } else {
                            singleBatchIteration(elements);
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


}
