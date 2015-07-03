package org.fit.fraunhofer.almanac;

import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttReceivedMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Werner-Kyt�l� on 12.05.2015.
 */
//public class WasteMqttClient extends Observable implements Observer, MqttCallback {
public class WasteMqttClient extends Observable implements MqttCallback {
    private static WasteMqttClient instancePub = null;
    private static WasteMqttClient instanceSub = null;

    private MqttClient mqttClient;
    private Issue issueUpdate;

    /***************** CONSTANTS */
    public static final String CLIENTID_PUB = "wastePub";
    public static final String CLIENTID_SUB = "wasteSub";



    public WasteMqttClient(String clientId){
        openMqttConnection(clientId);
    }

    public static synchronized WasteMqttClient getInstanceSub() {
        if (instanceSub == null) {
            instanceSub = new WasteMqttClient(CLIENTID_SUB);
        }

        return instanceSub;
    }

    public static synchronized WasteMqttClient getInstancePub() {
        if (instancePub == null) {
            instancePub = new WasteMqttClient(CLIENTID_PUB);
        }

        return instancePub;
    }

    private void openMqttConnection(String clientId){
        try {
//            mqttClient = new MqttClient("tcp://localhost:1883","waste", new MemoryPersistence());
//            mqttClient = new MqttClient("tcp://almanac.fit.fraunhofer.de:1883","wasteBackEnd", new MemoryPersistence());
            mqttClient = new MqttClient("tcp://m2m.eclipse.org:1883", clientId, new MemoryPersistence());
            mqttClient.setCallback(this);
            mqttClient.connect();
//            subscribe(DF_WASTEBINFULL_TOPIC); // Data Fusion query
//            subscribe("/+/+/+/cep/+");
//            subscribe(CITIZENAPP_TOPIC); // get notified by a CitizenApp about issues
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message){
        try{
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }

            mqttClient.publish(topic, message.getBytes(), 0, false);
            System.out.println("----------------------------------");
            System.out.println("Published under topic: " + topic + " message: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic){
        try{
            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }
            mqttClient.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


     // This callback is invoked upon losing the MQTT connection
    @Override
    public void connectionLost(Throwable t) {
        System.out.println("Connection lost! Cause: " + t.getCause().toString() + " Message: " + t.getMessage());

        // reconnect to the broker
        while (!mqttClient.isConnected()) {
            try {

                System.out.println("Connection lost!");

                mqttClient.connect();
                if(!mqttClient.isConnected()){
                    try {
                        mqttClient.close();
                    }catch (Exception e){}
                    openMqttConnection(mqttClient.getClientId());
                }

                if(!mqttClient.isConnected())
                    Thread.sleep(1000);         // is this really needed?

            } catch (Exception e) {
                System.out.println("Client disconnected!");
            }

        }
    }

    // This callback is invoked when a message published by this client
    // is successfully received by the broker.
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //System.out.println("Pub complete" + new String(token.getMessage().getPayload()));
    }

    // This callback is invoked when a message is received on a subscribed topic. In case of the
    // waste application, it will be a message related to the CitizenApp or related to ALMANAC's
    // Data Fusion: a waste bin fill level has surpassed the threshold. A new issue has to be created out of it.
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message arrived  --------------------------------------------");
        System.out.println("| Topic:" + topic);
        System.out.println("| Message: " + new String(message.getPayload()));
        System.out.println("-------------------------------------------------");

        //wraps the message into an internal class MqttMessageWithTopic to contain the topic the message
        // was received on
        MqttMessageWithTopic messageWithTopic = new MqttMessageWithTopic(topic, message);

        setChanged();
//        notifyObservers(message);
        notifyObservers(messageWithTopic);
    }


    // This callback is invoked whenever an issue's state, etc and/or priority has changed.
    // The issue change will be published.
/*    @Override
    public void update(Observable observable, Object arg) {
        issueUpdate = (Issue) observable;
        this.publish(((Issue) observable).id(), arg.toString());

//        MqttTunnelledMessage data = (MqttTunnelledMessage)arg;
//        System.out.println(new String(data.getPayload()));
    }
*/
    @Override
    public void finalize() {
        try {
            if(mqttClient.isConnected())
                mqttClient.disconnect();

        } catch (MqttException e) {
            e.printStackTrace();
        }

        try {
            mqttClient.close();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public class MqttMessageWithTopic{
        private String topic;
        private MqttMessage payload;

        public MqttMessageWithTopic(String topic, MqttMessage message){
            this.topic = topic;
            this.payload = message;
        }

        public MqttMessage Payload(){
            return payload;
        }
        public String Topic(){
            return topic;
        }
    }
}
