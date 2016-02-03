package eu.linksmart.gc.utils.mqtt.broker;


import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;
import eu.linksmart.gc.utils.logging.LoggerService;
import eu.linksmart.gc.utils.mqtt.subscription.ForwardingListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class BrokerService implements Observer, Broker {
    protected static LoggerService loggerService;
    // this is the MQTT client to broker in the local broker
    private Configurator conf = Configurator.getDefaultConfig();
    protected MqttClient mqttClient;
    protected ForwardingListener listener;
    private UUID ID;
    protected ArrayList<String> topics = new ArrayList<String>();

    protected int preloadedQoS = 0,preloadedTriesReconnect = 10, preloadedRetryTime =3000;
    protected boolean preloadedPolicy = false;




    private Boolean watchdog = false;

    private String brokerName;
    private String brokerPort;




    public BrokerService(String brokerName, String brokerPort, UUID ID) throws MqttException {
        listener = new ForwardingListener(this,ID);

         loggerService = new LoggerService(LoggerFactory.getLogger(BrokerService.class));
        watchdog = conf.getBool(BrokerServiceConst.CONNECTION_MQTT_WATCHDOG_CONF_PATH);
        preloadConfiguration();
        if (brokerName.equals("*"))
            this.brokerName = getHostName();
        else
            this.brokerName = brokerName;

        this.brokerPort = brokerPort;
        this.ID = ID;
       createClient();

    }
    public boolean isConnected()  {

        return mqttClient.isConnected();
    }
    protected void _connect() throws Exception {


        loggerService.info("MQTT broker UUID:"+ID.toString()+" URL:"+this.getBrokerURL()+" is connecting...");
        if(!mqttClient.isConnected()) {

            mqttClient.connect();

        }
        startWatchdog();
        loggerService.info("MQTT broker UUID:"+ID.toString()+" URL:"+this.getBrokerURL()+" is connected");
    }
    protected void _disconnect() throws Exception {
        loggerService.info("MQTT broker UUID:"+ID.toString()+" URL:"+this.getBrokerURL()+" is disconnecting...");
        stopWatchdog();
        try {

            mqttClient.disconnect();
        }catch (Exception e){
            loggerService.error(e.getMessage(),e);
            throw e;
        }
        loggerService.info("MQTT broker UUID:"+ID.toString()+" URL:"+this.getBrokerURL()+" is disconnected");

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
    public static String getBrokerURL(String brokerName, String brokerPort){

        if (ipPattern.matcher(brokerName).find())
            return "tcp://"+brokerName+":"+brokerPort;
        else
            return "tcp://"+brokerName;
    }
    public static boolean isBrokerURL(String string){
        return urlPattern.matcher(string).find();
    }
    public String getBrokerURL(){
        return getBrokerURL(brokerName, brokerPort);
    }

    public void createClient() throws MqttException {
        mqttClient = new MqttClient(getBrokerURL(),ID.toString()+":"+UUID.randomUUID().toString(), new MemoryPersistence());
        connectionWatchdog();
        mqttClient.setCallback(listener);
    }

    protected void preloadConfiguration(){

        preloadedQoS =conf.getInt(Const.DEFAULT_QOS);
        preloadedPolicy = conf.getBool(Const.DEFAULT_RETAIN_POLICY);
        preloadedTriesReconnect =conf.getInt(Const.RECONNECTION_TRY);
        preloadedRetryTime = conf.getInt(Const.RECONNECTION_MQTT_RETRY_TIME);
    }
    private void connectionWatchdog(){

        if(watchdog) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (watchdog) {

                        try {
                            try {

                                preloadConfiguration();

                            }catch (Exception e){
                                loggerService.warn("Error while loading configuration, doing the action from hardcoded values");
                            }
                            //noinspection SynchronizeOnNonFinalField
                            synchronized (watchdog) {
                                if (!mqttClient.isConnected())
                                    _connect();
                            }
                        } catch (Exception e) {
                            loggerService.error("Error in the watch dog of broker service:" + e.getMessage(), e);
                        }
                        try {

                            Thread.sleep(conf.getInt(BrokerServiceConst.CONNECTION_MQTT_WATCHDOG_TIMEOUT));
                        } catch (Exception e) {
                            loggerService.warn("Error while loading configuration, doing the action from hardcoded values");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e1) {
                                loggerService.error(e.getMessage(), e);
                            }
                        }
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
        //noinspection SynchronizeOnNonFinalField
        synchronized (watchdog) {
            if (!this.watchdog) {
                this.watchdog = true;
                connectionWatchdog();
            }
        }

    }
    public void stopWatchdog() {
        //noinspection SynchronizeOnNonFinalField
        synchronized (watchdog) {
            this.watchdog = false;
        }

    }



    private String getHostName(){
        String hostname = "localhost";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
        }
        catch (UnknownHostException ex)
        {
            loggerService.error("Error while getting hostname:"+ex.getMessage(),ex);
        }
        return hostname;
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
                preloadedQoS,
                preloadedPolicy);
    }
    public void publish(String topic, String payload) throws Exception {

        publish(topic,payload.getBytes());
    }



    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) throws Exception {
        boolean wasConnected =isConnected();
        if(!this.brokerName.equals(brokerName)) {

            this.brokerName = brokerName;

          restart(wasConnected);

        }

    }

    public String getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(String brokerPort) throws Exception {
        boolean wasConnected =isConnected();

        if(!this.brokerPort.equals(brokerPort)) {
            this.brokerPort = brokerPort;

            restart(wasConnected);
        }
    }
    public void setBroker(String brokerName, String brokerPort) throws Exception {
        boolean wasConnected =isConnected();
        if(!this.brokerName.equals(brokerName) || !this.brokerPort.equals(brokerPort)) {

            if (!this.brokerPort.equals(brokerPort)) {
                this.brokerPort = brokerPort;

            }
            if (!this.brokerName.equals(brokerName)) {
                this.brokerName = brokerName;

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
    public synchronized boolean addListener(String topic, Observer stakeholder)  {


        try {

            _connect();

            topics.add(topic);
            String[] aux = topics.toArray(new String[topics.size()] ) ;
            mqttClient.subscribe(aux);
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
    public void update(Observable o, Object arg) {
        ForwardingListener source = (ForwardingListener)arg;
        switch (source.getStatus()){
            case Disconnected:
                for(int i=0; i<preloadedTriesReconnect && !mqttClient.isConnected();i++){
                    try {
                        mqttClient.connect();

                    } catch (MqttException e) {
                        loggerService.error(e.getMessage(), e);
                    }
                    try {
                        Thread.sleep(preloadedRetryTime);
                    } catch (InterruptedException e) {
                        loggerService.error(e.getMessage(), e);
                    }
                }

                break;
        }

    }
}