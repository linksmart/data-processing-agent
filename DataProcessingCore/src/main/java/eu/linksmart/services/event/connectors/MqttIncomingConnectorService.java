package eu.linksmart.services.event.connectors;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.services.event.connectors.Observers.IncomingMqttObserver;
import eu.linksmart.services.event.intern.SharedSettings;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.broker.StaticBrokerService;
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
    protected transient Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected transient Configurator conf =  Configurator.getDefaultConfig();
    protected List<StaticBroker> brokers = new ArrayList<>();
    protected Map<String,IncomingMqttObserver> listeners = new Hashtable<>();


    protected MqttIncomingConnectorService() throws MalformedURLException, MqttException {

    }
    public synchronized void addListener(String alias, String topic, IncomingMqttObserver listener) throws InternalException{
        try {
            StaticBroker broker = new StaticBroker(alias, SharedSettings.getWill(), SharedSettings.getWillTopic());
            listener.setBrokerService(broker);
            broker.addListener(topic.replace("<id>", SharedSettings.getId()), listener);
            brokers.add(broker);
            listeners.put(topic,listener);
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw new InternalException(alias,"Broker", e.getMessage(),e);
        }


    }
    public synchronized void removeListener(String topic) throws InternalException{
        try {
            IncomingMqttObserver listener = listeners.get(topic);
            if(listener!=null) {
                StaticBroker broker = listeners.get(topic).getBrokerService();
                broker.removeListener(listener);
                brokers.remove(broker);
                listeners.remove(topic);
            }
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
        }


    }
    @Override
    public boolean isUp() {

        return brokers.stream().allMatch(StaticBroker::isConnected);
    }
}
