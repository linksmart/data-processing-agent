package eu.linksmart.services.event.handler;


import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.components.ComplexEventPropagationHandler;
import eu.linksmart.api.event.components.Enveloper;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.services.utils.mqtt.broker.BrokerConfiguration;
import eu.linksmart.services.utils.serialization.Serializer;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.types.Statement;
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

    public ComplexEventHandler(Statement query) throws Exception {
        super(query);

        this.query=query;
        try {
            enveloper = new DefaultEnveloper();
            serializer = SharedSettings.getSerializer();
            if(!query.isRESTOutput()) {
                publisher = new DefaultMQTTPublisher(query, SharedSettings.getWill(), SharedSettings.getWillTopic());
                loggerService.info("The Agent(ID:" + SharedSettings.getId() + ") generating events for statement ID "+query.getId()+" in the broker " + query.getScope(0) + "  URL: " + publisher.getScopes().stream().map(s->BrokerConfiguration.loadConfigurations().get(s).getURL()).collect(Collectors.joining(",")));
                loggerService.info("The Agent(ID:"+ SharedSettings.getId()+") generating event in the topic(s): " + publisher.getOutputs().stream().collect(Collectors.joining(",")));
            }else {

                publisher = new HTTPPublisher(query);
            }
            query.setLastOutput(enveloper.pack(Double.NaN,new Date(), SharedSettings.getId(),query.getId(),"BootstrapMessage",query.getName()));
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }



    }

    protected void processMessage(Map eventMap){

            if (eventMap.size() == 1) {
                query.setLastOutput(enveloper.pack(
                        eventMap.get(eventMap.keySet().toArray()[0]),
                        new Date(), SharedSettings.getId(),
                        query.getId(),
                        eventMap.keySet().toArray()[0].toString(),
                        query.getName()
                ));
                // if the eventMap is only one then is sent as one event
                try {
                    publisher.publish(serializer.serialize( query.getLastOutput()));

                } catch (Exception eEntity) {
                    loggerService.error(eEntity.getMessage(), eEntity);
                }
            }else {
                query.setLastOutput(
                        enveloper.pack(
                                eventMap,
                                new Date(),
                                SharedSettings.getId(),
                                query.getId(),
                                "Map",
                                query.getName()
                        )
                );

                // if the eventMap has several events in it
                if(conf.getBoolean(Const.AGGREGATE_EVENTS_CONF)) {
                    // if the aggregation option is on; the whole map is send as it is
                    try {
                        publisher.publish(serializer.serialize(query.getLastOutput()));
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(), e);
                    }
                }else {
                    query.setLastOutput(
                            enveloper.pack(
                                    eventMap,
                                    new Date(),
                                    SharedSettings.getId(),
                                    query.getId(),
                                    "Map",
                                    query.getName()
                            )
                    );
                    // if the aggregation option is off; each value of the map is send as an independent event
                    eventMap.keySet().forEach(key -> {
                                try {
                                    publisher.publish(serializer.serialize(query.getLastOutput()));
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
