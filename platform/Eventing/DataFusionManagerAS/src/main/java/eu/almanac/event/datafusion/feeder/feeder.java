package eu.almanac.event.datafusion.feeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.almanac.event.datafusion.esper.EsperQuery;
import eu.almanac.event.datafusion.logging.LoggerHandler;
import eu.almanac.event.datafusion.utils.payload.IoTPayload.IoTEntityEvent;
import eu.almanac.event.datafusion.utils.payload.OGCSensorThing.ObservationNumber;
import eu.almanac.event.datafusion.utils.payload.SenML.Event;
import eu.linksmart.api.event.datafusion.DataFusionWrapper;
import eu.linksmart.api.event.datafusion.EventFeeder;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.api.event.datafusion.core.EventFeederLogic;
import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import eu.linksmart.gc.network.backbone.protocol.mqtt.ForwardingListener;
import it.ismb.pertlab.ogc.sensorthings.api.datamodel.Observation;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by Caravajal on 22.05.2015.
 */
public abstract class Feeder extends Thread implements EventFeeder, EventFeederLogic, Observer {
    ForwardingListener forwardingListener ;
    protected Map<String,DataFusionWrapper> dataFusionWrappers = new HashMap<String, DataFusionWrapper>();

    private final String DFM_QUERY_TOPIC = "#";
    private final String TOPIC = "/federation1/trn/v2/observation/#";

    protected Boolean toShutdown = false;


    private Boolean down =false;

    public Feeder(String brokerName,String brokerPort, String topic){
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
                    LoggerHandler.report("info",this.getClass().getSimpleName()+" logged off");

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

        LoggerHandler.report("info","message arrived with topic: "+((MqttTunnelledMessage)mqttMessage).getTopic() );

        mangeEvent(((MqttTunnelledMessage)mqttMessage).getTopic(), ((MqttTunnelledMessage)mqttMessage).getPayload() );

    }


    protected abstract void mangeEvent(String topic, byte[] rawEvent);

}
