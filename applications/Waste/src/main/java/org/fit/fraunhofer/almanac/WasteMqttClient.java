package org.fit.fraunhofer.almanac;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Werner-Kyt�l� on 12.05.2015.
 */
public class WasteMqttClient extends Observable implements Observer, MqttCallback {
    private MqttClient mqttClient;
    private IssueManagement.Issue issueUpdate;

    public WasteMqttClient(){
        openMqttConnection();
    }

    private void openMqttConnection(){
        try {
//            mqttClient = new MqttClient("tcp://localhost:1883","waste", new MemoryPersistence());
            mqttClient = new MqttClient("tcp://almanac.fit.fraunhofer.de:1883","waste", new MemoryPersistence());
//            mqttClient = new MqttClient("tcp://m2m.eclipse.org:1883","waste", new MemoryPersistence());
            mqttClient.setCallback(this);
            mqttClient.connect();
            subscribe("/federation1/amiat/v2/cep/test123"); // "test123" after cep still to be replaced by the query code which Ángel will generate
//            subscribe("/+/+/+/cep/108797012059995192299003590625306557191293393213306404837883519641824178766181");
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
        System.out.println("Connection lost!");

        // reconnect to the broker
        while (!mqttClient.isConnected()) {
            try {

                System.out.println("Connection lost!");

                mqttClient.connect();
                if(!mqttClient.isConnected()){
                    try {
                        mqttClient.close();
                    }catch (Exception e){}
                    openMqttConnection();
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
    // waste application, it will be a message related to ALMANAC's Data Fusion: a waste bin fill
    // level has surpassed the threshold. A new issue has to be created out of it.
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("-------------------------------------------------");
        System.out.println("| Topic:" + topic);
        System.out.println("| Message: " + new String(message.getPayload()));
        System.out.println("-------------------------------------------------");

        // When this mesg arrives from DF, the update method in IssueManagement creates a new issue out of it!!

        setChanged();
        notifyObservers(message);
    }


    // This callback is invoked whenever an issue's state, etc and/or priority has changed.
    // The issue change will be published.
    @Override
    public void update(Observable observable, Object arg) {
        issueUpdate = (IssueManagement.Issue) observable;
        this.publish(((IssueManagement.Issue) observable).id(), arg.toString());

/*        MqttTunnelledMessage data = (MqttTunnelledMessage)arg;
        System.out.println(new String(data.getPayload()));*/
    }

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
}
