package eu.linksmart.services.event.handler;


import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.components.ComplexEventPropagationHandler;
import eu.linksmart.api.event.components.Enveloper;
import eu.linksmart.api.event.components.Publisher;
import eu.linksmart.services.utils.mqtt.broker.BrokerConfiguration;
import eu.linksmart.services.utils.serialization.Serializer;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.services.utils.configuration.Configurator;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by José Ángel Carvajal on 06.10.2014 a researcher of Fraunhofer FIT.
 */
    public class ComplexEventHandler extends BaseMapEventHandler implements ComplexEventPropagationHandler {

    protected Publisher publisher;
    protected Serializer serializer;
    protected EventBuilder builder;
    static final private String POSTFIX_ID = "$topic";

    private Configurator conf =  Configurator.getDefaultConfig();

    public ComplexEventHandler(Statement query) throws TraceableException, UntraceableException {
        super(query);

        this.query=query;
        try {
            if(query.getResultType()!=null && EventBuilder.getBuilder(query.getResultType()) != null ) {
                builder = EventBuilder.getBuilder(query.getResultType());
                query.setLastOutput(builder.factory(SharedSettings.getId(), query.getId(), Double.NaN, new Date(), new HashMap<>()));
            }else
                builder = null;
            serializer = SharedSettings.getSerializer();
            if(!query.isRESTOutput()) {
                publisher = new DefaultMQTTPublisher(query, SharedSettings.getWill(), SharedSettings.getWillTopic());
                loggerService.info("The Agent(ID:" + SharedSettings.getId() + ") generating events for statement ID "+query.getId()+" in the broker " + query.getScope(0) + "  URL: " + publisher.getScopes().stream().map(s->BrokerConfiguration.loadConfigurations().get(s).getURL()).collect(Collectors.joining(",")));

            }else {

                publisher = new HTTPPublisher(query);
                loggerService.info("The Agent(ID:" + SharedSettings.getId() + ") generating events for statement ID "+query.getId()+" in the broker " + query.getScope(0) + "  URL: " + publisher.getScopes().stream().map(HTTPPublisher.knownInstances::get).collect(Collectors.joining(",")));

            }
        }catch (TraceableException e){
            loggerService.error(e.getMessage(),e);
            throw e;
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw new UnknownException(query.getId(),"Internal Error",e.getMessage(),e);
        }

       loggerService.info("The Agent(ID:"+ SharedSettings.getId()+") generating event in the topic(s): " + publisher.getOutputs().stream().collect(Collectors.joining(",")));
    }



    protected void processMessage(Map[] eventMap){
        processMap(eventMap);
    }

    @Override
    protected void processLeavingMessage(Map[] eventMap) {
        processMap(eventMap);
    }

    protected void processMap(Map[] eventMap) {
        if (eventMap != null) {
            if (eventMap.length == 1)
                processSingleMap(eventMap[0]);
            else {
                try {
                    query.setLastOutput(
                            builder.factory(
                                    SharedSettings.getId(),
                                    query.getId(),
                                    eventMap,
                                    new Date(),
                                    new HashMap<>()
                            )
                    );
                } catch (UntraceableException e) {

                    loggerService.error(e.getMessage(), e);
                }

                try {
                    publisher.publish(serializer.serialize(query.getLastOutput()));
                } catch (Exception e) {
                    loggerService.error(e.getMessage(), e);
                }
            }
        }


    }
    protected void processSingleMap(Map eventMap){
        if(builder!=null) {
            if (eventMap.size() == 1) {
                try {
                    query.setLastOutput(builder.factory(
                            SharedSettings.getId(),
                            query.getId(),
                            eventMap.values().toArray()[0],
                            (new Date()).getTime(),
                            new HashMap<>()
                    ));
                } catch (UntraceableException e) {
                    loggerService.error(e.getMessage(), e);
                }
                // if the eventMap is only one then is sent as one event
                try {
                    publisher.publish(serializer.serialize(query.getLastOutput()));

                } catch (Exception eEntity) {
                    loggerService.error(eEntity.getMessage(), eEntity);
                }
            } else {
                Object tmpDate = eventMap.getOrDefault("time", eventMap.getOrDefault("Time", eventMap.getOrDefault("date", eventMap.getOrDefault("Date", new Date()))));
                Date date = ((tmpDate instanceof Date) ? (Date) tmpDate : ((tmpDate instanceof Long) ? new Date((Long) tmpDate) : new Date()));
                try {
                    query.setLastOutput(
                            builder.factory(
                                    SharedSettings.getId(),
                                    query.getId(),
                                    eventMap,
                                    date.getTime(),
                                    new HashMap<>()
                            )
                    );
                } catch (UntraceableException e) {

                    loggerService.error(e.getMessage(), e);
                }

                // if the eventMap has several events in it
                if (conf.getBoolean(Const.AGGREGATE_EVENTS_CONF)) {
                    // if the aggregation option is on; the whole map is send as it is
                    try {
                        publisher.publish(serializer.serialize(query.getLastOutput()));
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(), e);
                    }
                } else {
                    try {
                        query.setLastOutput(
                                builder.factory(
                                        SharedSettings.getId(),
                                        query.getId(),
                                        eventMap,
                                        date.getTime(),
                                        new HashMap<>()
                                )
                        );
                    } catch (UntraceableException e) {

                        loggerService.error(e.getMessage(), e);
                    }
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
        }else {

            query.setLastOutput(eventMap);

            // if the eventMap is only one then is sent as one event
            try {
                publisher.publish(serializer.serialize(query.getLastOutput()));

            } catch (Exception eEntity) {
                loggerService.error(eEntity.getMessage(), eEntity);
            }
        }
    }

    @Override
    public  synchronized void destroy(){

        publisher.close();
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
