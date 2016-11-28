package eu.linksmart.services.event.connectors;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.api.event.exceptions.InternalException;
import eu.linksmart.services.event.connectors.Observers.EventMqttObserver;
import eu.linksmart.services.event.connectors.Observers.IncomingMqttObserver;
import eu.linksmart.services.event.connectors.Observers.StatementMqttObserver;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.Broker;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.broker.StaticBrokerService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public class MqttIncomingConnectorService implements IncomingConnector {

    static protected MqttIncomingConnectorService me = null;

    /*
    todo if you read this and still is not being use remove it
    static public MqttIncomingConnectorService getReference() throws MalformedURLException, MqttException {
        if(me == null)
            me= new MqttIncomingConnectorService();
        return me;
    }*/
    static public MqttIncomingConnectorService getReference(String will, String willTopic) throws MalformedURLException, MqttException {
        if(me == null)
            me= new MqttIncomingConnectorService(will, willTopic);
        return me;
    }
    protected transient Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected transient Configurator conf =  Configurator.getDefaultConfig();
    protected List<StaticBroker> brokers = new ArrayList<>();
    final protected String will, willTopic;

    protected MqttIncomingConnectorService(String will, String willTopic) throws MalformedURLException, MqttException {
        this.will =will;
        this.willTopic =willTopic;
    }
    public void addAddListener(String alias, String topic, IncomingMqttObserver listener) throws InternalException{
        try {
            StaticBroker broker = new StaticBroker(alias, will,willTopic);
            listener.setBrokerService(broker);
            broker.addListener(topic, listener);
            brokers.add(broker);
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw new InternalException(alias,"Broker", e.getMessage(),e);
        }


    }
    @Override
    public boolean isUp() {

        return brokers.stream().allMatch(StaticBroker::isConnected);
    }
}
