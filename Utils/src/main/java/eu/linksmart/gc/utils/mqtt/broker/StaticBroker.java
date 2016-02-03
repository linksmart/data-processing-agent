package eu.linksmart.gc.utils.mqtt.broker;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.MalformedURLException;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 23.10.2015 a researcher of Fraunhofer FIT.
 */
public class StaticBroker implements Broker{

    protected StaticBrokerService brokerService;
    protected UUID clientID;

    public StaticBroker(String brokerName, String brokerPort) throws MalformedURLException, MqttException {
        clientID = UUID.randomUUID();
        brokerService = StaticBrokerService.getBrokerService(clientID, brokerName,brokerPort);
    }
    public StaticBroker() throws MalformedURLException, MqttException {
        clientID = UUID.randomUUID();
        brokerService = StaticBrokerService.getBrokerService(clientID, "localhost","1883");
    }
    @Override
    public boolean isConnected() {
        return brokerService.isConnected(clientID);
    }

    @Override
    public void connect() throws Exception {

        brokerService.connect(clientID);
    }

    @Override
    public void disconnect() throws Exception {
        brokerService.disconnect(clientID);
    }

    @Override
    public void destroy() throws Exception {
        brokerService.destroy(clientID);
    }

    @Override
    public String getBrokerURL() {
        return brokerService.getBrokerURL();
    }

    @Override
    public void createClient() throws MqttException {
        brokerService.createClient();

    }

    @Override
    public boolean isWatchdog() {
        return brokerService.isWatchdog();
    }

    @Override
    public void startWatchdog() {

        brokerService.startWatchdog();
    }

    @Override
    public void stopWatchdog() {
        brokerService.stopWatchdog();

    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {

        brokerService.publish(topic,payload,qos,retained);
    }

    @Override
    public void publish(String topic, byte[] payload) throws Exception {

        brokerService.publish(topic,payload);
    }

    @Override
    public void publish(String topic, String payload) throws Exception {

        brokerService.publish(topic,payload);
    }

    @Override
    public String getBrokerName() {
        return brokerService.getBrokerName();
    }

    @Override
    public void setBrokerName(String brokerName) throws Exception {

        brokerService.setBrokerName(brokerName);
    }

    @Override
    public String getBrokerPort() {
        return brokerService.getBrokerPort();
    }

    @Override
    public void setBrokerPort(String brokerPort) throws Exception {

        brokerService.setBrokerPort(brokerPort);

    }

    @Override
    public void setBroker(String brokerName, String brokerPort) throws Exception {

        brokerService.setBroker(brokerName,brokerPort);
    }

    @Override
    public boolean addListener(String topic, Observer stakeholder) {
        return brokerService.addListener(topic,stakeholder);
    }

    @Override
    public boolean removeListener(String topic, Observer stakeholder) {
        return brokerService.removeListener(topic,stakeholder);
    }

    @Override
    public void removeListener(Observer stakeholder) {
         brokerService.removeListener(stakeholder);

    }

    @Override
    public void update(Observable o, Object arg) {
        brokerService.update(o,arg);

    }
}
