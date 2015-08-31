package eu.linksmart.gc.utils.mqtt.broker;


import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.constants.Const;
import eu.linksmart.gc.utils.mqtt.subscription.ForwardingListener;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Pattern;

public class BrokerService implements Observer {
    protected Logger LOG = null;
    // this is the MQTT client to broker in the local broker
    private Configurator conf = Configurator.getDefaultConfig();
    protected MqttClient mqttClient;
    protected ForwardingListener listener;
    private UUID ID;
    protected ArrayList<String> topics = new ArrayList<String>();
    private static final Pattern ipPattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+"), urlPattern = Pattern.compile("\\b(tcp|ws)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|][:[0-9]+]");



    private Boolean watchdog = false;

    private String brokerName;
    private String brokerPort;




    BrokerService(String brokerName, String brokerPort, UUID ID, String serviceDescription) throws MqttException {
        listener = new ForwardingListener(this,ID);

        LOG = Logger.getLogger(BrokerService.class.getName()+":"+serviceDescription);
        if (brokerName.equals("*"))
            this.brokerName = getHostName();
        else
            this.brokerName = brokerName;

        this.brokerPort = brokerPort;
        this.ID = ID;
       createClient();

    }
    public boolean isConnect() throws Exception {

        return mqttClient.isConnected();
    }
    protected void _connect() throws Exception {

        if(!isConnect()) {

            mqttClient.connect();

        }
        startWatchdog();
        LOG.info("MQTT broker is connected");
    }
    protected void _disconnect() throws Exception {

        stopWatchdog();
        try {

            mqttClient.disconnect();
        }catch (Exception e){
            throw e;
        }
        LOG.info("MQTT broker is disconnected");

    }
    protected void _destroy() throws Exception {


        try {

            if(isConnect())
                disconnect();

            mqttClient.close();
        }catch (Exception e){
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
        mqttClient = new MqttClient(getBrokerURL(),ID.toString(), new MemoryPersistence());

        mqttClient.setCallback(listener);
    }

    private void connectionWatchdog(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (watchdog){

                    try {
                        //noinspection SynchronizeOnNonFinalField
                        synchronized (watchdog) {
                            if (!isConnect())
                                connect();
                        }
                        Thread.sleep(conf.getInt(BrokerServiceConst.CONNECTION_MQTT_WATCHDOG_TIMEOUT));
                    } catch (Exception e) {
                        LOG.error("Error in the watch dog of broker service:"+e.getMessage(),e);
                    }
                }
            }
        }
        ).start();

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
            LOG.error("Error while getting hostname:"+ex.getMessage(),ex);
        }
        return hostname;
    }
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {

        if(!isConnect())
            _connect();

         mqttClient.publish(topic,payload,qos,retained);
    }
    public void publish(String topic, byte[] payload) throws Exception {

        publish(
                topic,
                payload,
                conf.getInt(Const.DEFAULT_QOS),
                conf.getBool(Const.DEFAULT_RETAIN_POLICY));
    }
    public void publish(String topic, String payload) throws Exception {

        publish(topic,payload.getBytes());
    }



    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) throws Exception {
        boolean wasConnected =isConnect();
        if(!this.brokerName.equals(brokerName)) {

            this.brokerName = brokerName;

          restart(wasConnected);

        }

    }

    public String getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(String brokerPort) throws Exception {
        boolean wasConnected =isConnect();

        if(!this.brokerPort.equals(brokerPort)) {
            this.brokerPort = brokerPort;

            restart(wasConnected);
        }
    }
    public void setBroker(String brokerName, String brokerPort) throws Exception {
        boolean wasConnected =isConnect();
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
            LOG.error("Error while restarting broker Service:"+e.getMessage(),e);
        }

        createClient();
        if(wasConnected){
            connect();
        }

    }
    public synchronized boolean addListener(String topic, Observer stakeholder)  {


        try {
            mqttClient.subscribe(topic);
            listener.addObserver(topic, stakeholder);
            topics.add(topic);
        } catch (MqttException e) {
                LOG.error(e.getMessage(), e);
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
                for(int i=0; i<conf.getInt(Const.RECONNECTION_TRY) && !mqttClient.isConnected();i++){
                    try {
                        mqttClient.connect();

                    } catch (MqttException e) {
                        LOG.error(e.getMessage(), e);
                    }
                    try {
                        Thread.sleep(conf.getInt(Const.RECONNECTION_MQTT_RETRY_TIME));
                    } catch (InterruptedException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }

                break;
        }

    }
}