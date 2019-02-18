package eu.linksmart.services.event.handler;


import eu.linksmart.api.event.exceptions.*;
import eu.linksmart.api.event.types.EventBuilder;
import eu.linksmart.services.event.handler.base.BaseMapEventHandler;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.api.event.components.ComplexEventPropagationHandler;
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
                query.setLastOutput(builder.factory(SharedSettings.getId(), query.getId(),Double.NaN, new Date(),null, new HashMap<>()));
            }else {
                builder = null;
                query.setLastOutput(Double.NaN);
            }
            serializer = SharedSettings.getSerializer();
            if(!query.isRESTOutput()) {
                publisher = new DefaultMQTTPublisher(query, SharedSettings.getWill(), SharedSettings.getWillTopic());
                loggerService.info("The Agent(ID:" + SharedSettings.getId() + ") generating events for statement ID "+query.getId()+" in the broker " + query.getScope(0) + "  URL: " + publisher.getScopes().stream().map(s->BrokerConfiguration.loadConfiguration(s).getURL()).collect(Collectors.joining(",")));

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
            else if(eventMap.length>1&&( eventMap[0].containsKey("k") && eventMap[0].get("k") != null || eventMap[0].containsKey("key") && eventMap[0].get("key") != null) && (eventMap[0].containsKey("v") && eventMap[0].get("v") != null|| eventMap[0].containsKey("values") && eventMap[0].get("values") != null)){
                final Map<Object,Collection> aux = new HashMap();
                Arrays.stream(eventMap).forEach(i->applyMap(i,aux));
                processSingleMap(aux);
            }else {
                packLastOutput( SharedSettings.getId(),query.getId(),eventMap,(new Date()).getTime(),null,new HashMap<>());
                publishLastOutput();
            }
        }


    }
    private void applyMap(Map source,Map<Object,Collection> target){ // repack the map using the k/key as keys and the v/values as values
        target.putIfAbsent(source.getOrDefault("k",source.get("key")), new ArrayList());
        if(source.getOrDefault("v",source.get("values")) instanceof Collection)
            target.get(source.getOrDefault("k",source.get("key"))).addAll(  (Collection) source.getOrDefault("v",source.get("values")) );
        else if(source.getOrDefault("v",source.get("values")) instanceof Object[])
            target.get(source.getOrDefault("k",source.get("key"))).addAll(  Arrays.asList((Object[]) source.getOrDefault("v",source.get("values"))) );
        else if(source.getOrDefault("v",source.get("values")) instanceof Map && (((Map) source.getOrDefault("v",source.get("values"))).size() == 1))
            target.get(source.getOrDefault("k",source.get("key"))).addAll(  ((Map) source.getOrDefault("v",source.get("values"))).values() );
        else
            target.get(source.getOrDefault("k",source.get("key"))).add(source.getOrDefault("v",source.get("values")));
    }
    protected void processSingleMap(Map eventMap){
        if(builder!=null) {
            if (eventMap.size() == 1 && eventMap.values().toArray()[0]!=null) { // has only one element

                packLastOutput( SharedSettings.getId(),query.getId(),eventMap.values().toArray()[0],(new Date()).getTime(),null,new HashMap<>());
                // if the eventMap is only one then is sent as one event
                publishLastOutput();

            }  else if (eventMap.size() == 2 &&( eventMap.containsKey("k") && eventMap.get("k")!= null || eventMap.containsKey("key") && eventMap.get("key")!= null) && (eventMap.containsKey("v") && eventMap.get("v")!= null || eventMap.containsKey("values") && eventMap.get("values")!= null)) {
                // re-creates the map mapping the given keys and values
                final Map aux = new HashMap();
                applyMap(eventMap,aux);
                processSingleMap(aux);
            }else if (!eventMap.isEmpty()){ // if there is objects to work with
                Object tmpDate = eventMap.getOrDefault("time", eventMap.getOrDefault("Time", eventMap.getOrDefault("date", eventMap.getOrDefault("Date", null))));
                Object tmpId = eventMap.getOrDefault("id", eventMap.getOrDefault("ID", eventMap.getOrDefault("Id", eventMap.getOrDefault("@iot.id", eventMap.getOrDefault("datastream.id", null)))));

                if((tmpDate!= null && tmpId!= null && eventMap.size()==3)|| ((tmpDate!= null || tmpId!= null) && eventMap.size()==2)) { // id the Id or time of the Datastream is defined
                    Date date = (tmpDate == null) ? new Date() : ((tmpDate instanceof Date) ? (Date) tmpDate : ((tmpDate instanceof Long) ? new Date((Long) tmpDate) : new Date()));
                    if (eventMap.size() == 3) { // time and id are given
                        eventMap.remove(tmpDate);
                        eventMap.remove(tmpId);
                        packLastOutput( SharedSettings.getId(),tmpId,eventMap.values().toArray()[0],date.getTime(),null,new HashMap<>());
                    } else if (tmpDate != null) { // time is given
                        eventMap.remove(tmpDate);
                        eventMap.remove(tmpId);
                        packLastOutput( SharedSettings.getId(),tmpId,eventMap.values().toArray()[0],date.getTime(),null,new HashMap<>());
                    } else { // if Id is given
                        eventMap.remove(tmpId);
                        packLastOutput( SharedSettings.getId(),tmpId,eventMap.values().toArray()[0],date.getTime(),null,new HashMap<>());
                    }
                }else { // if none are given
                    Date date = (tmpDate == null) ? new Date() : ((tmpDate instanceof Date) ? (Date) tmpDate : ((tmpDate instanceof Long) ? new Date((Long) tmpDate) : new Date()));

                    packLastOutput(SharedSettings.getId(), query.getId(), eventMap, date.getTime(), null, new HashMap<>());
                }

                // if the eventMap has several events in it
                if (conf.getBoolean(Const.AGGREGATE_EVENTS_CONF)) {
                    // if the aggregation option is on; the whole map is send as it is
                    publishLastOutput();
                } else {

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
        }else { // if there no event builder set

            query.setLastOutput(eventMap);

            // if the eventMap is only one then is sent as one event
           publishLastOutput();
        }
    }

    private void packLastOutput(Object thingId, Object streamID, Object value, long date, String url, Map<String, Object> map){
        try {
            query.setLastOutput(
                    builder.factory(thingId,streamID,value,date,url,map)
            );
        } catch (UntraceableException e) {

            loggerService.error(e.getMessage(), e);
        }
    }
    private void publishLastOutput(){
        try {
            publisher.publish(serializer.serialize(query.getLastOutput()));
        } catch (Exception eEntity) {
            loggerService.error(eEntity.getMessage(), eEntity);
        }
    }
    @Override
    public  synchronized void destroy(){

        publisher.close();
    }


}
