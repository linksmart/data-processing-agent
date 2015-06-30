package eu.almanac.event.datafusion.feeders;

import eu.almanac.event.datafusion.esper.utils.Tools;
import eu.almanac.event.datafusion.intern.LoggerService;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.Feeder;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import eu.linksmart.gc.network.backbone.protocol.mqtt.ForwardingListener;
import org.eclipse.paho.client.mqttv3.*;

import java.util.*;

/**
 * Created by Caravajal on 22.05.2015.
 */
public abstract class FeederImpl extends Thread implements Feeder, EventFeederLogic, Observer {
    protected ForwardingListener forwardingListener ;
    protected Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<String, DataFusionWrapper>();

    protected Boolean toShutdown = false;

    static protected Boolean down =false;

    public FeederImpl(String brokerName, String brokerPort, String topic){
        try {
            forwardingListener = new ForwardingListener(brokerName,brokerPort,topic,this);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        start();


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

    public boolean isDown(){
        synchronized (down) {
            return down;
        }
    }

    @Override
    public void run(){
        while (!down) {
            synchronized (toShutdown) {
                if (toShutdown  ) {



                    for (DataFusionWrapper i : dataFusionWrappers.values())
                        i.destroy();

                    try {
                        forwardingListener.close();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    LoggerService.report("info", this.getClass().getSimpleName() + " logged off");

                    synchronized (down) {
                        down = true;
                    }

                }
            }
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public boolean subscribeToTopic(String topic) {
        try {
          forwardingListener.setListening(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        return true;
    }



    @Override
    public void update(Observable topic, Object mqttMessage)  {

        LoggerService.report("info", Tools.getDateNowString()+" message arrived with topic: " + ((MqttTunnelledMessage) mqttMessage).getTopic());

        mangeEvent(((MqttTunnelledMessage)mqttMessage).getTopic(), ((MqttTunnelledMessage)mqttMessage).getPayload() );

    }


    protected abstract void mangeEvent(String topic, byte[] rawEvent);

}
