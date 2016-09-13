package eu.linksmart.services.event.connectors;

import eu.linksmart.api.event.components.IncomingConnector;
import eu.linksmart.services.event.connectors.Observers.EventMqttObserver;
import eu.linksmart.services.event.connectors.Observers.StatementMqttObserver;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.net.MalformedURLException;

/**
 * Created by José Ángel Carvajal on 01.09.2016 a researcher of Fraunhofer FIT.
 */
public class MqttIncomingConnectorService implements IncomingConnector {

    static protected IncomingConnector me = null;

    static public IncomingConnector getReference() throws MalformedURLException, MqttException {
        if(me == null)
            me= new MqttIncomingConnectorService();
        return me;
    }

    protected Logger loggerService = Utils.initLoggingConf(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();
    protected StaticBroker statementBroker= null, eventBroker = null;

    protected static final Object lockDown = new Object();

    protected MqttIncomingConnectorService() throws MalformedURLException, MqttException {
        initStatementObserver();
        initEventObserver();
    }

    void initStatementObserver() throws MalformedURLException, MqttException {
        statementBroker = new StaticBroker(conf.getString(Const.STATEMENT_INOUT_BROKER_CONF_PATH),conf.getString(Const.STATEMENT_INOUT_BROKER_PORT_CONF_PATH));
        statementBroker.addListener(conf.getString(Const.STATEMENT_INOUT_BASE_TOPIC_CONF_PATH), new StatementMqttObserver(statementBroker));
    }
    void initEventObserver() throws MalformedURLException, MqttException {
        eventBroker = new StaticBroker(conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH),conf.getString(Const.EVENTS_IN_BROKER_PORT_CONF_PATH));
        eventBroker.addListener(conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH), new EventMqttObserver(statementBroker));
    }

    @Override
    public boolean isUp() {

        return eventBroker.isConnected() && statementBroker.isConnected();
    }
}
