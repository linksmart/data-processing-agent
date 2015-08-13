package eu.almanac.event.datafusion.feeder;

import eu.almanac.event.datafusion.esper.utils.Tools;
import eu.almanac.event.datafusion.intern.Const;
import eu.almanac.event.datafusion.intern.Utils;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.broker.StaticBrokerService;
import org.antlr.v4.runtime.misc.NotNull;
import org.eclipse.paho.client.mqttv3.*;

import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by Caravajal on 22.05.2015.
 */
public abstract class FeederImpl extends Thread implements Feeder, EventFeederLogic, Observer {

    protected Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<>();
    protected LoggerService loggerService = Utils.initDefaultLoggerService(this.getClass());
    protected Configurator conf =  Configurator.getDefaultConfig();
    protected StaticBrokerService brokerService= null;
    @NotNull
    protected static Boolean toShutdown = false;
    protected long debugCount=0;

    static protected @NotNull Boolean down =false;

    public FeederImpl(String brokerName, String brokerPort, String topic) throws MalformedURLException, MqttException {

        brokerService = initBrokerService(brokerName, brokerPort);
        brokerService.addListener(topic,this);

        start();


    }


    private StaticBrokerService initBrokerService(String brokerName, String brokerPort) throws MalformedURLException, MqttException {
        return  StaticBrokerService.getBrokerService(
                this.getClass().getCanonicalName(),
                brokerName,
                brokerPort

        );

    }


    @Override
    public boolean dataFusionWrapperSignIn(DataFusionWrapper dfw) {
        dataFusionWrappers.put(dfw.getName(), dfw);

        //TODO: add code for the OSGi future
        return false;
    }

    @Override
    public boolean dataFusionWrapperSignOut(DataFusionWrapper dfw) {
        dataFusionWrappers.remove(dfw.getName());

        //TODO: add code for the OSGi future
        return false;
    }

    
    @SuppressWarnings("SynchronizeOnNonFinalField")
    public @NotNull boolean isDown(){
        @NotNull
        boolean tmp ;

        synchronized (down) {

            tmp = down;
        }

        return tmp;
    }

    @SuppressWarnings("SynchronizeOnNonFinalField")
    @Override
    public void run(){
        while (!down) {
            synchronized (toShutdown) {
                if (toShutdown  ) {



                    for (DataFusionWrapper i : dataFusionWrappers.values())
                        i.destroy();



                    brokerService.removeListener(this);
                    try {
                        brokerService.destroy(this.getClass().getCanonicalName());
                    } catch (Exception e) {
                        loggerService.error(e.getMessage(),e);
                    }

                    loggerService.info(this.getClass().getSimpleName() + " logged off");

                    synchronized (down) {
                        down = true;
                    }

                }
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
               loggerService.error(e.getMessage(),e);
            }
        }

    }
    @Override
    public boolean subscribeToTopic(String topic) {

          brokerService.addListener(topic,this);


        return true;
    }




    @Override
    public void update(Observable topic, Object mqttMessage)  {

        debugCount=(debugCount+1)%Long.MAX_VALUE;
        if(debugCount%conf.getInt(Const.LOG_DEBUG_NUM_IN_EVENTS_REPORTED_CONF_PATH) == 0)
            loggerService.info(Tools.getDateNowString() + " message arrived with topic: " + ((MqttTunnelledMessage) mqttMessage).getTopic());


        mangeEvent(((MqttTunnelledMessage)mqttMessage).getTopic(), ((MqttTunnelledMessage)mqttMessage).getPayload() );

    }


    protected abstract void mangeEvent(String topic, byte[] rawEvent);

}
