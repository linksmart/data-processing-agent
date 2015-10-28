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

    protected BrokerService brokerService;
    protected String clientID;

    StaticBroker(String brokerName, String brokerPort) throws MalformedURLException, MqttException {
        clientID = UUID.randomUUID().toString();
        brokerService = StaticBrokerService.getBrokerService(clientID,brokerName,brokerPort);
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public void connect() throws Exception {

    }

    @Override
    public void disconnect() throws Exception {

    }

    @Override
    public void destroy() throws Exception {

    }

    @Override
    public String getBrokerURL() {
        return null;
    }

    @Override
    public void createClient() throws MqttException {

    }

    @Override
    public boolean isWatchdog() {
        return false;
    }

    @Override
    public void startWatchdog() {

    }

    @Override
    public void stopWatchdog() {

    }

    @Override
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {

    }

    @Override
    public void publish(String topic, byte[] payload) throws Exception {

    }

    @Override
    public void publish(String topic, String payload) throws Exception {

    }

    @Override
    public String getBrokerName() {
        return null;
    }

    @Override
    public void setBrokerName(String brokerName) throws Exception {

    }

    @Override
    public String getBrokerPort() {
        return null;
    }

    @Override
    public void setBrokerPort(String brokerPort) throws Exception {

    }

    @Override
    public void setBroker(String brokerName, String brokerPort) throws Exception {

    }

    @Override
    public boolean addListener(String topic, Observer stakeholder) {
        return false;
    }

    @Override
    public boolean removeListener(String topic, Observer stakeholder) {
        return false;
    }

    @Override
    public void removeListener(Observer stakeholder) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
