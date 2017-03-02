package eu.linksmart.services.utils.mqtt.broker;


import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.mqtt.subscription.ForwardingListener;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class BrokerService implements Observer, Broker {
    protected transient static Logger loggerService = Logger.getLogger(BrokerService.class.getName());
    // this is the MQTT client to broker in the local broker
    private transient Configurator conf = Configurator.getDefaultConfig();
    protected transient MqttClient mqttClient;
    protected transient ForwardingListener listener;

    protected ArrayList<String> topics = new ArrayList<>();
    protected ArrayList<Integer> qoss = new ArrayList<>();

    private transient Boolean watchdog = false;

    private final transient static Object lock  = new Object();

    protected final BrokerConfiguration brokerConf;

    public BrokerService(String alias, UUID ID, String will, String topicWill) throws MqttException {

        brokerConf = new BrokerConfiguration(alias,ID.toString());
        brokerConf.setWill(will);
        brokerConf.setWillTopic(topicWill);
        listener = new ForwardingListener(this,ID);

        createClient();

    }
    public BrokerService(String brokerName, String brokerPort, UUID ID) throws MqttException {

        brokerConf = new BrokerConfiguration("",ID.toString());
        brokerConf.setHostname(brokerName);
        brokerConf.setPort(Integer.valueOf(brokerPort));
        listener = new ForwardingListener(this,ID);

        createClient();

    }
    public boolean isConnected()  {

        return mqttClient.isConnected();
    }
    protected void _connect() throws Exception {



        if(!mqttClient.isConnected()) {
            loggerService.info("MQTT broker UUID:"+brokerConf.getId()+" Alias:"+brokerConf.getAlias()+" with configuration "+brokerConf.toString()+" is connecting...");
            mqttClient.connect(brokerConf.getMqttConnectOptions());

            startWatchdog();
            loggerService.info("MQTT broker UUID:"+brokerConf.getId()+" Alias:"+brokerConf.getAlias()+" is connected");
        }
    }
    protected void _disconnect() throws Exception {
        loggerService.info("MQTT broker UUID:"+brokerConf.getId()+" Alias:"+brokerConf.getAlias()+" with configuration "+brokerConf.toString()+" is disconnecting...");
        stopWatchdog();
        try {

            mqttClient.disconnect();
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw e;
        }
        loggerService.info("MQTT broker UUID:"+brokerConf.getId()+" Alias:"+brokerConf.getAlias()+" is disconnected");

    }
    protected void _destroy() throws Exception {


        try {

            if( mqttClient.isConnected())
                _disconnect();

            mqttClient.close();
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw e;
        }



    }
    public void connect() throws Exception {
        _connect();
    }
    public void disconnect() throws Exception {

       _disconnect();

    }
    public void destroy() throws Exception {
        _destroy();

    }

    public String getBrokerURL(){
        return brokerConf.getURL();
    }

    public void createClient() throws MqttException {

        mqttClient = brokerConf.initClient();
        connectionWatchdog();

        mqttClient.setCallback(listener);
        try {
            _connect();
        } catch (MqttException e) {
            throw e;
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
        }
    }


    private void connectionWatchdog(){

        if(watchdog) {
            new Thread(() -> {
                while (watchdog) {

                    try {
                        try {

                            // TODO define what we do with the watchdog

                        }catch (Exception e){
                            loggerService.warn("Error while loading configuration, doing the action from hardcoded values");
                        }

                        synchronized (lock) {
                            if (!mqttClient.isConnected())
                                _connect();
                        }
                    } catch (Exception e) {
                        loggerService.error("Error in the watch dog of broker service:" + e.getMessage(), e);
                    }

                    try {
                        Thread.sleep(brokerConf.getTimeOut());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
            ).start();
        }
    }
    public boolean isWatchdog() {
        return watchdog;
    }

    public void startWatchdog() {
        synchronized (lock) {
            if (!this.watchdog) {
                this.watchdog = true;
                connectionWatchdog();
            }
        }

    }
    public void stopWatchdog() {
        synchronized (lock) {
            this.watchdog = false;
        }

    }
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {

        if(!mqttClient.isConnected())
            _connect();

        // create the topic and the publish suppose to solve the:
        // 106ed26f-74f8-4048-9035-cb9146e35c7c:67c62857-af3c-4aa0-9f28-3f4db6baf811: Timed out as no activity, keepAlive=60,000 lastOutboundActivity=1,446,124,817,035 lastInboundActivity=1,446,124,826,676 time=1,446,124,936,865 lastPing=1,446,124,784,576
        mqttClient.getTopic(topic).publish(payload,qos,retained);

    }
    public void publish(String topic, byte[] payload) throws Exception {


        publish(
                topic,
                payload,
                brokerConf.getPubQoS(),
                brokerConf.isRetainPolicy());
    }
    public void publish(String topic, String payload) throws Exception {

        publish(topic,payload.getBytes());
    }



    public String getBrokerName() {
        return brokerConf.getHostname();
    }

    public void setBrokerName(String brokerName) throws Exception {
        boolean wasConnected =isConnected();
        if(!this.brokerConf.getHostname().equals(brokerName)) {

            this.brokerConf.setHostname(brokerName);

          restart(wasConnected);

        }

    }

    public String getBrokerPort() {
        return String.valueOf(brokerConf.getPort());
    }

    public void setBrokerPort(String brokerPort) throws Exception {
        boolean wasConnected =isConnected();

        if(!getBrokerPort().equals(brokerPort)) {
            brokerConf.setPort(Integer.valueOf(brokerPort));

            restart(wasConnected);
        }
    }
    public void setBroker(String brokerName, String brokerPort) throws Exception {
        boolean wasConnected =isConnected();
        if(!this.getBrokerName().equals(brokerName) || !this.getBrokerPort().equals(brokerPort)) {

            if (!this.getBrokerPort().equals(brokerPort)) {
                brokerConf.setPort(Integer.valueOf(brokerPort));

            }
            if (!this.getBrokerName().equals(brokerName)) {
                this.brokerConf.setHostname(brokerName);

            }

            restart(wasConnected);
        }

    }
    private void restart(boolean wasConnected) throws Exception {
        try {
            destroy();
        }catch (Exception e){
            loggerService.error("Error while restarting broker Service:"+e.getMessage(),e);
        }

        createClient();
        if(wasConnected){
            connect();
        }

    }
    public boolean addListener(String topic, Observer stakeholder)  {

        return addListener(topic,stakeholder,brokerConf.getSubQoS());
    }
    public synchronized boolean addListener(String topic, Observer stakeholder, int QoS)  {


        try {

            _connect();

            topics.add(topic);
            qoss.add(QoS);


            subscribeAll();

            listener.addObserver(topic, stakeholder);
        } catch (Exception e) {
            loggerService.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
    public synchronized boolean removeListener(String topic, Observer stakeholder){

        return listener.removeObserver(topic,stakeholder);

    }

    public synchronized void removeListener( Observer stakeholder){

        for (String topic: topics) {
          listener.removeObserver(topic, stakeholder);

        }

    }

    @Override
    public BrokerConfiguration getConfiguration() {
        return brokerConf;
    }

    private synchronized void subscribeAll() throws MqttException {

        mqttClient.subscribe(topics.toArray(new String[topics.size()]), ArrayUtils.toPrimitive(qoss.toArray(new Integer[qoss.size()])));

    }

    @Override
    public void update(Observable o, Object arg) {

                loggerService.warn("Disconnection of the client with id: " + brokerConf.getId() + " and alias: " + brokerConf.getAlias() + " with conf: " + brokerConf.toString());
                for(int i=0; i<brokerConf.getNoTries() && !mqttClient.isConnected();i++){
                    try {
                        loggerService.info("Reconnecting...");
                        _connect();

                        subscribeAll();

                    } catch (Exception e) {
                        loggerService.error(e.getMessage(), e);
                    }
                    try {
                        Thread.sleep(brokerConf.getReconnectWaitingTime());
                    } catch (InterruptedException e) {
                        loggerService.error(e.getMessage(), e);
                    }
                }



    }

    @Override
    public String getAlias() {
        return brokerConf.getAlias();
    }
    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;
        if (o!=null && o instanceof BrokerService) {
            BrokerService aux = (BrokerService) o;

            return brokerConf.equals(aux.brokerConf) && topics.equals(aux.topics);
        }
        return false;
    }
    @Override
    public String toString(){

        return "{" +
                "\"brokerConfiguration\":"+brokerConf.toString()+"," +
                "\"subscribedTopics\":["+topics.stream().map(i->"\""+i+"\"").collect(Collectors.joining(","))+"]" +
                "}";

    }
    @Override
    public int hashCode(){

        return toString().hashCode();
    }
}