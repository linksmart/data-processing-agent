package eu.linksmart.gc.utils.mqtt.subscription;


import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.types.CurrentStatus;
import eu.linksmart.gc.utils.mqtt.types.MqttMessage;
import eu.linksmart.gc.utils.mqtt.types.Topic;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;


import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public  class ForwardingListener implements MqttCallback {
    protected final UUID originProtocol;
    protected  Observer  connectionListener = null;
    protected static final LoggerService LOG= new LoggerService(LoggerFactory.getLogger(ForwardingListener.class));

    protected long sequence ;
    protected ExecutorService executor = Executors.newCachedThreadPool();
    protected Map<Topic, MessageDeliverer> observables;

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
        observables = new Hashtable<Topic, MessageDeliverer>();
    }


    protected void initObserver(String listening, Observer mqttEventsListener){
        observables = new Hashtable<Topic, MessageDeliverer>();
        observables.put(new Topic(listening), new MessageDeliverer());
    }
    public void addObserver(String topic, Observer listener){
        Topic t = new Topic(topic);
        if(!observables.containsKey(t))
            observables.put(t, new MessageDeliverer());

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
               // observables.get(t).notifyObservers(new MqttMessage(topic, mqttMessage.getPayload(), mqttMessage.getQos(), mqttMessage.isRetained(), getMessageIdentifier(), originProtocol));
                observables.get(t).setMqttMessage(new MqttMessage(topic, mqttMessage.getPayload(), mqttMessage.getQos(), mqttMessage.isRetained(), getMessageIdentifier(), originProtocol));
                executor.execute(observables.get(t));
                processed= true;
            }
        }

        if(!processed)
            LOG.warn("A message arrived and no one listening to it");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.debug("delivery complete  in listener");

    }


    public CurrentStatus getStatus(){
        return status;
    }



}
