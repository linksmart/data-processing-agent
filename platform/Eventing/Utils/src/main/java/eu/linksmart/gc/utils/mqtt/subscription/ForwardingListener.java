package eu.linksmart.gc.utils.mqtt.subscription;


import eu.linksmart.gc.utils.mqtt.types.CurrentStatus;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public  class ForwardingListener implements MqttCallback {
    protected final UUID originProtocol;
    protected  Observer  connectionListener = null;
    protected final Logger LOG= Logger.getLogger(ForwardingListener.class.getName());

    protected long sequence ;
    protected ExecutorService executor = Executors.newCachedThreadPool();
    protected Map<Topic, Observable> observables;

    protected CurrentStatus status;

    public ForwardingListener(String listening, Observer mqttEventsListener, UUID originProtocol) {
        this.originProtocol = originProtocol;
        initObserver(listening, mqttEventsListener);
    }

    public ForwardingListener(String listening, Observer connectionListener, Observer mqttEventsListener, UUID originProtocol) {
        this.originProtocol = originProtocol;
        this.connectionListener = connectionListener;
        initObserver(listening, mqttEventsListener);
    }
    public ForwardingListener( Observer connectionListener, UUID originProtocol) {
        this.originProtocol = originProtocol;
        this.connectionListener = connectionListener;
        observables = new Hashtable<Topic, Observable>();
    }


    protected void initObserver(String listening, Observer mqttEventsListener){
        observables = new Hashtable<Topic, Observable>();
        observables.put(new Topic(listening), new Observable());
    }
    public void addObserver(String topic, Observer listener){
        Topic t = new Topic(topic);
        if(!observables.containsKey(t))
            observables.put(t, new Observable());

        observables.get(t).addObserver(listener);

    }
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean removeObserver(String topic, Observer listener){
        if(observables.containsKey(topic))
            observables.get(topic).deleteObserver(listener);
        else
            return false;
        if(observables.get(topic).countObservers()==0)
            observables.remove(topic);

        if(observables.isEmpty())
            status = CurrentStatus.NotListening;

        return true;
    }
    public Set<Topic> getListeningTopics(){
        return observables.keySet();
    }
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean isObserversEmpty(String topic){
        return observables.containsKey(topic);
    }



    @Override
    public void connectionLost(Throwable throwable) {
        LOG.warn("Connection lost: "+throwable.getMessage(),throwable);
        status = CurrentStatus.Disconnected;
        connectionListener.update(null, this);


    }


    private synchronized long getMessageIdentifier(){
        sequence = (sequence + 1) % Long.MAX_VALUE;
        return sequence;
    }
    @Override
    public void messageArrived(String topic, org.eclipse.paho.client.mqttv3.MqttMessage mqttMessage) {
        LOG.debug("Message arrived in listener:" + topic);

        boolean processed= false;
     //if (observables.containsKey(topic))
        for(Topic t: observables.keySet()) {
            if(t.equals(topic)) {
                executor.execute(
                        MessageDeliverer.createMessageDeliverer(
                                new MqttMessage(topic, mqttMessage.getPayload(), mqttMessage.getQos(), mqttMessage.isRetained(), getMessageIdentifier(), originProtocol),
                                observables.get(t)
                        )
                );
                processed= true;
            }
        }

        if(!processed)
            LOG.warn("A message arrived and no one listening to it");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.info("delivery complete  in listener");

    }


    public CurrentStatus getStatus(){
        return status;
    }



}
