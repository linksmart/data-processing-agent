package eu.linksmart.services.event.feeders;

import eu.linksmart.api.event.components.CEPEngine;
import eu.linksmart.services.event.handler.DefaultMQTTPublisher;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.api.event.components.Feeder;
import eu.linksmart.api.event.exceptions.StatementException;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.types.Topic;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.util.*;

/**
 * Created by José Ángel Carvajal on 05.09.2016 a researcher of Fraunhofer FIT.
 */
public class EventFeeder implements Feeder<EventEnvelope> {
    static protected EventFeeder me;
    static {
        me = new EventFeeder();
        Feeder.feeders.put(me.getClass().getCanonicalName(),me);
    }

    protected Configurator conf =  Configurator.getDefaultConfig();
    protected Map<Topic,Class> topicToClass= new Hashtable<Topic,Class>();
    protected Map<String,String> classToAlias= new Hashtable<String, String>();
    protected Logger loggerService = Utils.initLoggingConf(this.getClass());

    protected Deserializer deserializer = new DefaultDeserializer();
    private Map<String, Class> compiledTopicClass = new Hashtable<>(), aliasToClass =new Hashtable<>();

    protected EventFeeder() {
        try {
            LoadTypesIntoEngines();
        } catch (InstantiationException e) {
            loggerService.error(e.getMessage(),e);
        }

    }

    public static void feed(String topic, byte[] rawEvent) throws TraceableException, UntraceableException{
        me.addEvent(topic,rawEvent);
    }
    public static EventFeeder getFeeder(){
        return me;
    }
    public void feed(String rawEvent) {
        try {
            Map<String,String> rawMap = deserializer.parse(rawEvent,Map.class);
            for(Map.Entry<String,String> entry: rawMap.entrySet())
                me.feed(entry.getKey(), entry.getValue());
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
        }

    }
    public void feed(String topic, String unparsedEvent) throws TraceableException, UntraceableException{
        EventEnvelope eventEnvelope = parseEvent(topic, unparsedEvent);
        addEvent(eventEnvelope,topic);
    }
    public void feed(String topic, EventEnvelope eventEnvelope) throws TraceableException, UntraceableException{

        me.addEvent(eventEnvelope,topic);
    }
    public void feed( EventEnvelope event) throws TraceableException, UntraceableException{
        try {

            if(event!=null) {
                for (CEPEngine i : CEPEngine.instancedEngines.values())
                    i.addEvent(event, event.getClass());
            }else
                throw new UntraceableException("Error by feeding event: The event sent cannot be mapped to any loaded type");
        }catch(TraceableException|UntraceableException e) {
            loggerService.error(e.getMessage(), e);
            throw e;
        }catch(Exception e) {
            loggerService.error(e.getMessage(), e);
            throw new UnknownUntraceableException(e.getMessage(),e);
        }
    }
    protected void addEvent(String topic, byte[] rawEvent) throws TraceableException, UntraceableException{
        if(topic.contains(DefaultMQTTPublisher.defaultOutput(SharedSettings.getId()))) // if it is my topic the event should be ignore the message
            return;

        try {
            Object event=null;
            if(!compiledTopicClass.containsKey(topic)) {

                if(topicToClass.isEmpty())
                    event = deserializer.deserialize(rawEvent, Observation.class);
                else
                    for (Topic t : topicToClass.keySet()) {
                        if (t.equals(topic)) {
                            event = deserializer.deserialize(rawEvent, topicToClass.get(t));
                            compiledTopicClass.put(topic, topicToClass.get(t));
                            break;
                        }
                    }
            }else{
                event = deserializer.deserialize(rawEvent, compiledTopicClass.get(topic));
            }

            if(event instanceof EventEnvelope) {

                ((EventEnvelope)event).topicDataConstructor(topic);

                addEvent( (EventEnvelope) event,topic);
            }else
            if(event!=null)
                throw new StatementException(event.getClass().getCanonicalName(),"Event","Error while feeding the engine with events: Unknown event type, all events must implement the EventEnvelope class");
            else
                throw new StatementException(SharedSettings.getId(),"Agent","Error while feeding the engine with events: Unknown event type, all events must implement the EventEnvelope class");


        }catch(TraceableException|UntraceableException e) {
            loggerService.error(e.getMessage(), e);
            throw e;
        }catch(Exception e) {
            loggerService.error(e.getMessage(), e);
            throw new UnknownUntraceableException(e.getMessage(),e);
        }

    }
    protected EventEnvelope parseEvent(String topic, String rawEvent) throws TraceableException , UntraceableException{
        Object event = null;
        try {
            if (deserializer == null)
                deserializer = new DefaultDeserializer();
            if (!compiledTopicClass.containsKey(topic)) {
                if (topicToClass.isEmpty())
                    event = deserializer.parse(rawEvent, Observation.class);
                else
                    for (Topic t : topicToClass.keySet()) {
                        if (t.equals(topic)) {
                            event = deserializer.parse(rawEvent, topicToClass.get(t));
                            compiledTopicClass.put(topic, topicToClass.get(t));
                            break;
                        }
                    }
            } else {
                event = deserializer.parse(rawEvent, compiledTopicClass.get(topic));
            }
            if(!(event instanceof EventEnvelope))
                throw new StatementException(topic,"Event","Error while feeding the engine with events: Unknown event type, all events must implement the EventEnvelope class");

        }catch(TraceableException e) {
            loggerService.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            throw new UnknownUntraceableException(e.getMessage(), e);
        }
        return (EventEnvelope) event;
    }
    protected void addEvent(EventEnvelope event,String topic) throws TraceableException, UntraceableException{
        try {

            if(event!=null) {

                event.topicDataConstructor(topic);
                for (CEPEngine i : CEPEngine.instancedEngines.values())
                    i.addEvent(event, event.getClass());
            }else
                throw new UntraceableException("Error by feeding event: The event sent cannot be mapped to any loaded type");
        }catch(TraceableException|UntraceableException e) {
            loggerService.error(e.getMessage(), e);
            throw e;
        }catch(Exception e) {
            loggerService.error(e.getMessage(), e);
            throw new UnknownUntraceableException(e.getMessage(),e);
        }

    }
   protected void LoadTypesIntoEngines() throws  InstantiationException {

        Map<String,Pair<String,String>> aliasTopicClass= new HashMap<>();

        Arrays.asList(conf.getStringArray(Const.FeederPayloadAlias)).stream()
                .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                .forEach(alias -> aliasTopicClass.put(alias, new ImmutablePair<>(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), conf.getString(Const.FeederPayloadClass + "_" + alias))));

        if(aliasTopicClass.isEmpty())
            throw new InstantiationException(
                    "The configuration parameters of incoming events "
                            +Const.FeederPayloadAlias+" do not have proper topic or class configurations for properties: "
                            +Const.FeederPayloadClass+"_<alias> "+Const.EVENT_IN_TOPIC_CONF_PATH+"_<alias> "
            );

        CEPEngine.instancedEngines.values().stream().forEach(engine->aliasTopicClass.forEach((alias,topicClass)-> {
                    try {
                        Class aClass = Class.forName(topicClass.getRight());
                        topicToClass.put(new Topic(topicClass.getLeft()),aClass);
                        aliasToClass.put(alias, aClass);
                        classToAlias.put(aClass.getCanonicalName(),alias);

                    } catch (ClassNotFoundException e) {
                        loggerService.error(e.getMessage(), e);
                    }
                })

        );
    }
}
