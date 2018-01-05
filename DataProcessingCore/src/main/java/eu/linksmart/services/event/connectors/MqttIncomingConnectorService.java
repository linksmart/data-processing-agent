package eu.linksmart.services.event.connectors;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.services.event.connectors.mqtt.IncomingMqttObserver;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.AgentUtils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
    protected transient Logger loggerService = AgentUtils.initLoggingConf(this.getClass());
    protected transient Configurator conf =  Configurator.getDefaultConfig();

    // protected List<StaticBroker> brokers = new ArrayList<>();
    protected Map<String,Map<String, IncomingMqttObserver>> listeners = new Hashtable<>();


    protected MqttIncomingConnectorService() throws MalformedURLException, MqttException {

    }
    public synchronized void addListener(String alias, String topic, IncomingMqttObserver listener) throws InternalException{
        try {
            StaticBroker broker = new StaticBroker(alias, SharedSettings.getWill(), SharedSettings.getWillTopic());
            listener.setBrokerService(broker);
            broker.addListener(AgentUtils.topicReplace(topic), listener);

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
