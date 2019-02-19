package eu.linksmart.services.event.connectors;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.services.event.connectors.mqtt.EventMqttObserver;
import eu.linksmart.services.event.connectors.mqtt.IncomingMqttObserver;
import eu.linksmart.services.event.connectors.mqtt.StatementMqttObserver;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import org.apache.logging.log4j.LogManager;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public class MqttIncomingConnectorService extends IncomingSyncConnector implements IncomingConnector {

    static protected MqttIncomingConnectorService me = null;

       static public MqttIncomingConnectorService getReference() throws MalformedURLException, MqttException {
        if(me == null)
            me= new MqttIncomingConnectorService();
        return me;
    }
    protected static transient Logger loggerService = LogManager.getLogger(MqttIncomingConnectorService.class);
    protected static transient Configurator conf =  Configurator.getDefaultConfig();

    private static void addEventConnection(String alias, List<String> brokers){
        brokers.forEach(broker->{
            try {
                getReference().addListener(broker, conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias), new EventMqttObserver(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + alias)));
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
            }
        });
    }
    public static void addEventConnection( List<String> brokers){
        Arrays.asList(conf.getStringArray(Const.FeederPayloadAlias)).stream()
                .filter(i -> conf.containsKeyAnywhere(Const.EVENT_IN_TOPIC_CONF_PATH + "_" + i) && conf.containsKeyAnywhere(Const.FeederPayloadClass + "_" + i))
                .forEach(alias -> brokers.forEach(broker->addEventConnection(alias,brokers)));
    }
    static {
        if(conf.getBoolean(Const.START_MQTT_STATEMENT_API)) {
            try {
                getReference().addListener(conf.getString(Const.STATEMENT_INOUT_BROKER_CONF_PATH),conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)+"#", new StatementMqttObserver(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH)+"#"));
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                loggerService.error("Programmatically exiting!");
                System.exit(-1);
            }
        }

        try {

            addEventConnection(Arrays.asList(conf.getStringArray(Const.EVENTS_IN_BROKER_CONF_PATH)));
        }catch (Exception e){
            if(!conf.getBoolean("Test")) {
                loggerService.error(e.getMessage(), e);
                loggerService.error("Programmatically exiting!");
                System.exit(-1);
            }
        }
    }
    protected Map<String,Map<String, IncomingMqttObserver>> listeners = new Hashtable<>();


    protected MqttIncomingConnectorService() throws MalformedURLException, MqttException {

    }
    public synchronized void addListener(String alias, String topic, IncomingMqttObserver listener) throws InternalException{
        try {
            StaticBroker broker = new StaticBroker(alias, SharedSettings.getWill(), SharedSettings.getWillTopic());
            listener.setBrokerService(broker);
            broker.addListener(AgentUtils.topicReplace(topic, ""), listener);

            loggerService.info("The Agent(ID:" + SharedSettings.getId() + ") with incoming events broker alias: " + alias + "  URL: " +broker.getConfiguration().getURL()+topic);
            listeners.putIfAbsent(alias,new Hashtable<>());
            listeners.get(alias).put(topic,listener);
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw new InternalException(alias,"Broker", e.getMessage(),e);
        }


    }
    public synchronized void removeListener(String alias) throws InternalException{
        try {
            List<Exception> exceptions = new ArrayList<>();
            listeners.get(alias).values().forEach(listener->{
                try {
                    listener.getBrokerService().removeListener(listener);
                   // listener.getBrokerService().removeListener(listener.getTopics().get(0),listener);
                    if(!listener.getBrokerService().hasListeners()) {
                        listener.getBrokerService().destroy();
                        listeners.remove(alias);
                    }
                } catch (Exception e) {
                    loggerService.error(e.getMessage(),e);
                    exceptions.add(e);
                    return;
                }
            });

            if(!exceptions.isEmpty())
                throw new InternalException(SharedSettings.getId(),this.getClass().getCanonicalName(),exceptions.get(0).getMessage(),exceptions.get(0));

        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw new InternalException(SharedSettings.getId(),this.getClass().getCanonicalName(),e.getMessage(),e);
        }


    }
    public Map<String, Map<String, IncomingMqttObserver>> getListeners() {
        return listeners;
    }

    @Override
    public boolean isUp() {
        //todo
        return true;
    }
}
