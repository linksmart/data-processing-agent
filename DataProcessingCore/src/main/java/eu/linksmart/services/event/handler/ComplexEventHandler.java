package eu.linksmart.services.event.handler;


import eu.linksmart.api.event.types.impl.StatementInstance;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.DynamicConst;
import eu.linksmart.api.event.components.ComplexEventPropagationHandler;
import eu.linksmart.api.event.components.Enveloper;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.services.utils.mqtt.broker.BrokerConfiguration;
import eu.linksmart.services.utils.serialization.Serializer;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.configuration.Configurator;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by José Ángel Carvajal on 06.10.2014 a researcher of Fraunhofer FIT.
 */
    public class ComplexEventHandler extends BaseMapEventHandler implements ComplexEventPropagationHandler {

    protected Publisher publisher;
    protected Enveloper enveloper;
    protected Serializer serializer;


    private Configurator conf =  Configurator.getDefaultConfig();

    public ComplexEventHandler(Statement query) throws StatementException, Exception {
        super(query);

        this.query=query;
        try {
            serializer = new DefaultSerializer();
            enveloper = new DefaultEnveloper();
            if(!query.isRESTOutput()) {
                publisher = new DefaultMQTTPublisher(query,DynamicConst.getId());
                loggerService.info("The Agent(ID:" + DynamicConst.getId() + ") generating events for statement ID "+query.getID()+" in the broker " + query.getScope(0) + "  URL: " + (new BrokerConfiguration(query.getID()).getURL()));
                loggerService.info("The Agent(ID:"+DynamicConst.getId()+") generating event in the topic(s): " + publisher.getOutputs().stream().collect(Collectors.joining(",")));
            }else {
                publisher = new HTTPPublisher(query);
            }
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }



    }


    protected void processMessage(Map eventMap){

            if (eventMap.size() == 1) {
                // if the eventMap is only one then is sent as one event
                try {
                    publisher.publish(
                            serializer.serialize(
                                    enveloper.pack(
                                            eventMap.get(eventMap.keySet().toArray()[0]),
                                            new Date(), DynamicConst.getId(),
                                            query.getID(),
                                            eventMap.keySet().toArray()[0].toString()
                                    )
                            )
                    );

                } catch (Exception eEntity) {
                    loggerService.error(eEntity.getMessage(), eEntity);
                }
            }else {
                // if the eventMap has several events in it
                if(conf.getBoolean(Const.AGGREGATE_EVENTS_CONF)) {
                    // if the aggregation option is on; the whole map is send as it is
                    try {
                        publisher.publish(
                                serializer.serialize(
                                        enveloper.pack(
                                                eventMap,
                                                new Date(),
                                                DynamicConst.getId(),
                                                query.getID(),
                                                "Map"
                                        )
                                )
                        );
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(), e);
                    }
                }else {
                    // if the aggregation option is off; each value of the map is send as an independent event
                    eventMap.keySet().forEach(key -> {
                                try {
                                    publisher.publish(
                                            serializer.serialize(
                                                    enveloper.pack(
                                                            eventMap,
                                                            new Date(),
                                                            DynamicConst.getId(),
                                                            query.getID(),
                                                            key.toString()
                                                    )
                                            )
                                    );
                                } catch (Exception ex) {
                                    loggerService.error(ex.getMessage(), ex);
                                }
                            }
                    );
                }
            }




    }

    @Override
    public  synchronized void destroy(){

        publisher.close();
        enveloper.close();
        serializer.close();
    }
    @Override
    public Enveloper getEnveloper() {
        return enveloper;
    }

    @Override
    public void setEnveloper(Enveloper enveloper) {
        this.enveloper = enveloper;
    }

    @Override
    public Publisher getPublisher() {
        return publisher;
    }

    @Override
    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }
}
