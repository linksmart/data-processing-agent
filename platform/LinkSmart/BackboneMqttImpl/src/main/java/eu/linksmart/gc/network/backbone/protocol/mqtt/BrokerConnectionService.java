package eu.linksmart.gc.network.backbone.protocol.mqtt;

import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.networkmanager.NetworkManager;
import eu.linksmart.gc.api.utils.Part;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * Created by Caravajal on 22.04.2015.
 */
public class BrokerConnectionService {
    private Logger LOG = Logger.getLogger(MqttBackboneProtocolImpl.class.getName());
    // this is the MQTT client to publish in the local broker
    private MqttClient mqttClient;
    private NetworkManager networkManager;
    private UUID ID;


    private Boolean watchdog = false;

    private String brokerName;
    private String brokerPort;

    private Registration brokerRegistrationInfo = null;
    BrokerConnectionService(  String brokerName, String brokerPort , UUID ID, NetworkManager netMngr) throws Exception {
        if (brokerName.equals("*"))
            this.brokerName = getHostName();
        else
            this.brokerName = brokerName;

        this.brokerPort = brokerPort;
        this.ID = ID;
        this.networkManager = netMngr;
       createClient();

    }
    public boolean isConnect() throws Exception {

        return mqttClient.isConnected();
    }
    public void connect() throws Exception {

        if(!isConnect()) {

            mqttClient.connect();

            try {
                registerBroker();

            } catch (Exception e) {

                disconnect();
                throw e;
            }
        }
        startWatchdog();

    }
    public void disconnect() throws Exception {


        try {

            mqttClient.disconnect();
        }catch (Exception e){
           throw e;
        } finally {

            try{
                deregisterBroker();

            }catch (Exception ex){

                throw ex;
            }
        }
       stopWatchdog();

    }
    public VirtualAddress getVirtualAddress(){
        return brokerRegistrationInfo.getVirtualAddress();
    }
    public void destroy() throws Exception {


        try {

            if(isConnect())
                disconnect();

            mqttClient.close();
        }catch (Exception e){
            throw e;
        }



    }

    public String getBrokerURL(){
        return getBrokerURL(brokerName, brokerPort);
    }
    public static String getBrokerURL(String brokerName, String brokerPort){
        return "tcp://"+brokerName+":"+brokerPort;
    }
    public void createClient() throws MqttException {
        mqttClient = new MqttClient(getBrokerURL(),ID.toString());

    }

    private void connectionWatchdog(){
        new Thread() {
            @Override
            public void run() {
                while (watchdog){

                    try {
                        synchronized (watchdog) {
                            if (!isConnect())
                                connect();
                        }
                        this.sleep(30000);
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
            }
        }.start();

    }
    public boolean isWatchdog() {
        return watchdog;
    }

    public void startWatchdog() {
        synchronized (watchdog) {
            if (!this.watchdog) {
                this.watchdog = true;
                connectionWatchdog();
            }
        }

    }
    public void stopWatchdog() {
        synchronized (watchdog) {
            this.watchdog = false;
        }

    }
    private void registerBroker() throws RemoteException {

        String description = "Broker:"+getBrokerURL();


        Registration[] registration = networkManager.getServiceByDescription(description);
        if(registration != null && registration.length > 0){
            new RemoteException("There is already a service with this description");

        }else {
            Part[] attributes = {
                    new Part("DESCRIPTION",description),
                    new Part("UUID",ID.toString())

            };

            brokerRegistrationInfo = networkManager.registerService(attributes,getBrokerURL(),MqttBackboneProtocolImpl.class.getName());
        }
    }
    private void deregisterBroker() throws RemoteException {
        Registration[] registration = networkManager.getServiceByDescription(brokerRegistrationInfo.getDescription());
        if(registration != null && registration.length > 0)
            networkManager.removeService(brokerRegistrationInfo.getVirtualAddress());


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
            LOG.error(ex);
        }
        return hostname;
    }
    public void publish(String topic, byte[] payload, int qos, boolean retained) throws Exception {

        if(!isConnect())
            connect();

         mqttClient.publish(topic,payload,qos,retained);
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) throws Exception {
        if(!this.brokerName.equals(brokerName)) {
            this.brokerName = brokerName;

            try {
                destroy();
            }catch (Exception e){
                // TODO: add message
            }

            createClient();
        }

    }

    public String getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(String brokerPort) throws Exception {
        if(!this.brokerPort.equals(brokerPort)) {
            this.brokerPort = brokerPort;

            destroy();
            createClient();
        }
    }
}
