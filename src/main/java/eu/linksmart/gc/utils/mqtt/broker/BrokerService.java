package eu.linksmart.gc.utils.mqtt.broker;


import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.mqtt.subscription.ForwardingListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.apache.log4j.Logger;

import javax.net.ssl.SSLSocketFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class BrokerService implements Observer, Broker {
    protected static Logger loggerService = Logger.getLogger(BrokerService.class.getName());
    // this is the MQTT client to broker in the local broker
    private Configurator conf = Configurator.getDefaultConfig();
    protected MqttClient mqttClient;
    protected ForwardingListener listener;
    private UUID ID;
    protected ArrayList<String> topics = new ArrayList<String>();

    protected int preloadedQoS = 0,preloadedTriesReconnect = 10, preloadedRetryTime =3000;
    protected boolean preloadedPolicy = false;

    private int CONNECTION_MQTT_WATCHDOG_TIMEOUT = 30000;
    private int SUBSCRIPTION_QoS =0;

    private boolean CERTIFICATE_BASE_SECURITY = false;
    private String CA_CERTIFICATE_PATH ="";
    private String CLIENT_CERTIFICATE_PATH="";
    private String KEY_PATH="";
    private String CERTIFICATE_KEY_PASSWORD="";

    private Boolean watchdog = false;

    private final static Object lock  = new Object();
    private String brokerName ;
    private String brokerPort;
    private int CONNECTION_MQTT_CONNECTION_TIMEOUT=60000;
    private int CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT=60000;
    private MqttConnectOptions mqttOptions;

    public BrokerService(String brokerName, String brokerPort, UUID ID) throws MqttException {
        listener = new ForwardingListener(this,ID);

        // loggerService = new LoggerService(LoggerFactory.getLogger(BrokerService.class));
        watchdog = conf.getBoolean(BrokerServiceConst.CONNECTION_MQTT_WATCHDOG_CONF_PATH);
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

            mqttClient.connect(mqttOptions);

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
    public static String getSecureBrokerURL(String brokerName, String brokerPort){

        if (ipPattern.matcher(brokerName).find())
            return "ssl://"+brokerName+":"+brokerPort;
        else
            return "ssl://"+brokerName;
    }
    public static String getBrokerURL(String brokerName, String brokerPort, boolean isSSL_URL){

        if (isSSL_URL)
            return getSecureBrokerURL(brokerName,brokerPort);
        else
            return getBrokerURL(brokerName, brokerPort);
    }
    public static boolean isBrokerURL(String string){

      return  urlPattern.matcher(string).find();

    }
    public String getBrokerURL(){
        return getBrokerURL(brokerName, brokerPort, CERTIFICATE_BASE_SECURITY);
    }

    public void createClient() throws MqttException {

        mqttClient = new MqttClient(getBrokerURL(),ID.toString()+":"+UUID.randomUUID().toString(), new MemoryPersistence());
        connectionWatchdog();

        mqttClient.setCallback(listener);
    }

    protected void preloadConfiguration() throws MqttException {

        preloadedQoS =conf.getInt(Const.DEFAULT_QOS);
        preloadedPolicy = conf.getBoolean(Const.DEFAULT_RETAIN_POLICY);
        preloadedTriesReconnect =conf.getInt(Const.RECONNECTION_TRY);
        preloadedRetryTime = conf.getInt(Const.RECONNECTION_MQTT_RETRY_TIME);
        CONNECTION_MQTT_WATCHDOG_TIMEOUT = conf.getInt(BrokerServiceConst.CONNECTION_MQTT_WATCHDOG_TIMEOUT);
        SUBSCRIPTION_QoS = conf.getInt(BrokerServiceConst.SUBSCRIPTION_QoS);
        try {
            CONNECTION_MQTT_CONNECTION_TIMEOUT = conf.getInt(BrokerServiceConst.CONNECTION_MQTT_CONNECTION_TIMEOUT);
        }catch (NoSuchElementException ex){
            loggerService.error("property CONNECTION_MQTT_CONNECTION_TIMEOUT not found loading hardcoded property",ex);
        }
        try {

            CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT= conf.getInt(BrokerServiceConst.CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT);
        }catch (NoSuchElementException ex){
            loggerService.error("property CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT not found loading hardcoded property",ex);
        }
        try {
            CERTIFICATE_BASE_SECURITY = conf.getBoolean(Const.CERTIFICATE_BASE_SECURITY);
        }catch (NoSuchElementException ex){
            loggerService.error("property CERTIFICATE_BASE_SECURITY not found loading hardcoded property",ex);
        }

        try {
            if(CERTIFICATE_BASE_SECURITY) {
                CA_CERTIFICATE_PATH = conf.getString(Const.CA_CERTIFICATE_PATH);
                CLIENT_CERTIFICATE_PATH = conf.getString(Const.CERTIFICATE_FILE_PATH);
                KEY_PATH = conf.getString(Const.KEY_FILE_PATH);
                CERTIFICATE_KEY_PASSWORD = conf.getString(Const.CERTIFICATE_KEY_PASSWORD);
            }
        }catch (NoSuchElementException ex){
            loggerService.error("security properties not found loading hardcoded property",ex);
        }


        mqttOptions = new MqttConnectOptions();

        mqttOptions.setConnectionTimeout(CONNECTION_MQTT_CONNECTION_TIMEOUT/1000);
        mqttOptions.setKeepAliveInterval(CONNECTION_MQTT_KEEP_ALIVE_TIMEOUT/1000);
        mqttOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);


        if(CERTIFICATE_BASE_SECURITY) {
            SSLSocketFactory socketFactory = null;
            try {
                socketFactory = Utils.getSocketFactory(CA_CERTIFICATE_PATH, CLIENT_CERTIFICATE_PATH, KEY_PATH, CERTIFICATE_KEY_PASSWORD);
            } catch (Exception e) {
                loggerService.error(e.getMessage(), e);
                throw new MqttException(e);
            }
            mqttOptions.setSocketFactory(socketFactory);
        }
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

                            synchronized (lock) {
                                if (!mqttClient.isConnected())
                                    _connect();
                            }
                        } catch (Exception e) {
                            loggerService.error("Error in the watch dog of broker service:" + e.getMessage(), e);
                        }

                        try {
                            Thread.sleep(CONNECTION_MQTT_WATCHDOG_TIMEOUT);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
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
            int[] qoss= new int[aux.length];
            Arrays.fill(qoss,SUBSCRIPTION_QoS);
            mqttClient.subscribe(aux,qoss);

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
                        mqttClient.connect(mqttOptions);

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