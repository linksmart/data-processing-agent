package testing;

import eu.linksmart.services.event.connectors.Observers.EventMqttObserver;
import eu.linksmart.services.event.intern.Const;
import eu.linksmart.services.event.intern.Utils;
import eu.linksmart.services.utils.configuration.Configurator;
import org.slf4j.Logger;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;

import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 23.03.2016 a researcher of Fraunhofer FIT.
 */
public class FeederTester implements Runnable{

    protected static Configurator conf;
    protected static Logger loggerService;
    EventMqttObserver feederImplEvents = null;
    public FeederTester() {
        init("conf.cfg");
    }

    protected  boolean init(String args){


        if(args != null) {
            Configurator.addConfFile(args);

        }else
            Configurator.addConfFile(Const.DEFAULT_CONFIGURATION_FILE);
        conf = Configurator.getDefaultConfig();

        loggerService = Utils.initLoggingConf(FeederTester.class);







        // TODO: change the loading of feeder the same way as the CEP engines are loaded
        // loading of feeders

        try {

           // feederImplEvents = new EventMqttFeederImpl(conf.getString(Const.EVENTS_IN_BROKER_CONF_PATH).toString(), conf.getString(Const.EVENTS_IN_BROKER_PORT_CONF_PATH).toString(), conf.getString(Const.EVENT_IN_TOPIC_CONF_PATH).toString());



        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return false;
        }

       /* if(feederImplEvents ==null || feederImplEvents.isDown() ){
            loggerService.error("The feeders couldn't start! MDF now is stopping");
            return false;
        }*/



        return true;

    }

    @Override
    public void run() {
        MqttMessage message = new MqttMessage("/f1/p1/v2/observation/1/1", "{\"Time\":1458563087990,\"ResultValue\":1,\"ResultType\":\"simulation\",\"Datastream\":{\"id\":\"0\",\"Description\":null,\"Thing\":null,\"Observations\":null,\"ObservedProperty\":null},\"FeatureOfInterest\":null,\"Sensor\":null}".getBytes(),0,false, 0L, UUID.randomUUID());
        while (true)

            feederImplEvents.update(null,message);

    }
}
