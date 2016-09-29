package eu.linksmart.services.event.feeder;

import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.components.CEPEngine;
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
import org.slf4j.Logger;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 05.09.2016 a researcher of Fraunhofer FIT.
 */
public class EventFeeder implements Feeder {
    static protected EventFeeder me;
    static {
        me = new EventFeeder();
    }

    protected Configurator conf =  Configurator.getDefaultConfig();
    protected Map<Topic,Class> topicToClass= new Hashtable<Topic,Class>();
    protected Map<String,String> classToAlias= new Hashtable<String, String>();
    protected Logger loggerService = Utils.initLoggingConf(this.getClass());

    protected Deserializer deserializer = new DefaultDeserializer();
    private Map<String, Class> compiledTopicClass = new Hashtable<>();

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
    public static void feed(String topic, EventEnvelope event) throws TraceableException, UntraceableException{
        me.addEvent(topic,event);
    }

    public static void feed(String topic, String unparsedEvent) throws TraceableException, UntraceableException{
        me.addEvent(topic,me.parseEvent(topic,unparsedEvent));
    }
    protected void addEvent(String topic, byte[] rawEvent) throws TraceableException, UntraceableException{
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

            if(event instanceof EventEnvelope)
                addEvent(topic,(EventEnvelope)event);
            else
                throw new StatementException(event.getClass().getCanonicalName(),"Event","Error while feeding the engine with events: Unknown event type, all events must implement the EventEnvelope class");
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
                throw new StatementException(event.getClass().getCanonicalName(),"Event","Error while feeding the engine with events: Unknown event type, all events must implement the EventEnvelope class");

        }catch(TraceableException e) {
            loggerService.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            throw new UnknownUntraceableException(e.getMessage(), e);
        }
        return (EventEnvelope) event;
    }
    protected void addEvent(String topic,EventEnvelope event) throws TraceableException, UntraceableException{
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
        List topics =conf.getList(Const.FeederPayloadTopic);
        List classes =conf.getList(Const.FeederPayloadClass);
        List aliases =conf.getList(Const.FeederPayloadAlias);

        if(classes.size()!=aliases.size()&&aliases.size()!=topics.size())
            throw new InstantiationException(
                    "The configuration parameters of "
                            +Const.FeederPayloadAlias+" "
                            +Const.FeederPayloadClass+" "
                            +Const.FeederPayloadTopic+" do not match"
            );
        for (CEPEngine dfw: CEPEngine.instancedEngines.values()) {
            for(int i=0; i<classes.size();i++) {
                try {
                    Class aClass = Class.forName(classes.get(i).toString());
                    topicToClass.put(new Topic(topics.get(i).toString()),aClass);
                    classToAlias.put(aClass.getCanonicalName(),aliases.get(i).toString());
                    dfw.addEventType(aliases.get(i).toString(), aClass );
                } catch (ClassNotFoundException e) {
                    loggerService.error(e.getMessage(), e);
                }
            }

        }
    }
}
