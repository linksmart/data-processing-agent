package eu.linksmart.services.utils.mqtt.subscription;


import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.constants.Const;
import eu.linksmart.services.utils.mqtt.types.CurrentStatus;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import eu.linksmart.services.utils.mqtt.types.Topic;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.testing.tooling.MessageValidator;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;


import java.io.IOException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 06.08.2015 a researcher of Fraunhofer FIT.
 */
public  class ForwardingListener implements MqttCallback {
    protected final UUID originProtocol;
    protected  Observer  connectionListener = null;
    protected static Logger LOG = Logger.getLogger(ForwardingListener.class.getName());

    protected long sequence ;
   protected Map<Topic, TopicMessageDeliverable> observables;
    protected Map<String,TopicMessageDeliverable> compiledTopic = new Hashtable<>();
    protected final  Object muxMessageDelivererSet = new Object();
    protected  Set<Topic> messageDelivererSet = new HashSet<>();


    protected CurrentStatus status;

    //Start of code made for testing performance
    protected final boolean VALIDATION_MODE;
    private final Deserializer deserializer;
    private final MessageValidator validator;
    //End of code made for testing performance

    public ForwardingListener(String listening, Observer mqttEventsListener, UUID originProtocol) {
        this.originProtocol = originProtocol;

        /// Code for validation and test proposes
        if(VALIDATION_MODE = Configurator.getDefaultConfig().containsKey(Const.VALIDATION_FORWARDING)) {
            deserializer = new DefaultDeserializer();
            validator = new MessageValidator(this.getClass(),"0",Configurator.getDefaultConfig().getLong(Const.VALIDATION_LOT_SIZE));
        }else{
            deserializer = null;
            validator = null;
        }

        initObserver(listening, mqttEventsListener);

    }

    public ForwardingListener(String listening, Observer connectionListener, Observer mqttEventsListener, UUID originProtocol) {
        this.originProtocol = originProtocol;
        this.connectionListener = connectionListener;

        /// Code for validation and test proposes
        if(VALIDATION_MODE = Configurator.getDefaultConfig().containsKey(Const.VALIDATION_FORWARDING)) {
            deserializer = new DefaultDeserializer();
            validator = new MessageValidator(this.getClass(),"0",Configurator.getDefaultConfig().getLong(Const.VALIDATION_LOT_SIZE));
        }else{
            deserializer = null;
            validator = null;
        }

        initObserver(listening, mqttEventsListener);
    }
    public ForwardingListener( Observer connectionListener, UUID originProtocol) {
        this.originProtocol = originProtocol;
        this.connectionListener = connectionListener;

        /// Code for validation and test proposes
        if(VALIDATION_MODE = Configurator.getDefaultConfig().containsKey(Const.VALIDATION_FORWARDING)) {
            deserializer = new DefaultDeserializer();
            validator = new MessageValidator(this.getClass(),"0",Configurator.getDefaultConfig().getLong(Const.VALIDATION_LOT_SIZE));
        }else{
            deserializer = null;
            validator = null;
        }

        observables = new Hashtable<>();
    }


    protected void initObserver(String listening, Observer mqttEventsListener){
        observables = new Hashtable<>();
        observables.put(new Topic(listening), new TopicMessageDeliverable(listening));
    }
    public void addObserver(String topic, Observer listener){
        Topic t = new Topic(topic);
        if(!observables.containsKey(t))
            observables.put(t, new TopicMessageDeliverable(topic));

        observables.get(t).addObserver(listener);
        synchronized (muxMessageDelivererSet) {
            messageDelivererSet = observables.keySet();
        }

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

        synchronized (muxMessageDelivererSet) {
            messageDelivererSet = observables.keySet();
        }
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

        if(VALIDATION_MODE) toValidation(topic,mqttMessage.getPayload());

        boolean processed= false;
        if(!compiledTopic.containsKey(topic)){
            for(Topic t: messageDelivererSet)
                if(t.equals(topic)) {

                    compiledTopic.put(topic, observables.get(t));
                    compiledTopic.get(topic).addMessage(new MqttMessage(topic, mqttMessage.getPayload(), mqttMessage.getQos(), mqttMessage.isRetained(), getMessageIdentifier(), originProtocol));

                    processed = true;
                    break;
                }
        } else if(compiledTopic.containsKey(topic)) {
            // observables.get(t).notifyObservers(new MqttMessage(topic, mqttMessage.getPayload(), mqttMessage.getQos(), mqttMessage.isRetained(), getMessageIdentifier(), originProtocol));
            compiledTopic.get(topic).addMessage(new MqttMessage(topic, mqttMessage.getPayload(), mqttMessage.getQos(), mqttMessage.isRetained(), getMessageIdentifier(), originProtocol));

            processed = true;
        }


        if(!processed)
            LOG.warn("A message arrived and no one listening to it");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.debug("delivery complete in listener");

    }


    public CurrentStatus getStatus(){
        return status;
    }
    /// for validation and evaluation propose
    private void toValidation(String topic, byte[] payload){
        if (VALIDATION_MODE)
            try {
                validator.addMessage(topic,(int)deserializer.deserialize(payload, Hashtable.class).get("ResultValue"));
            } catch (IOException e) {
                e.printStackTrace();
            }

    }


}
