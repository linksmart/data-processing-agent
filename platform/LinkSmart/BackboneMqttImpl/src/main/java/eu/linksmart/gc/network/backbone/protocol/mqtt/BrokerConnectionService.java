package eu.linksmart.gc.network.backbone.protocol.mqtt;

import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.networkmanager.NetworkManager;
import eu.linksmart.gc.api.utils.Part;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by Caravajal on 22.04.2015.
 */
public class BrokerConnectionService  {
    private Logger LOG = Logger.getLogger(MqttBackboneProtocolImpl.class.getName());
    // this is the MQTT client to publish in the local broker
    private MqttClient mqttClient;
    private NetworkManager networkManager;
    private UUID ID;
    private static final Pattern ipPattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");


    private Boolean watchdog = false;

    private String brokerName;
    private String brokerPort;


    private String serviceDescription;

    private boolean isService;

    private Registration brokerRegistrationInfo = null;
    BrokerConnectionService(  String brokerName, String brokerPort , UUID ID, NetworkManager netMngr, boolean isService, String serviceDescription) throws Exception {
        if (brokerName.equals("*"))
            this.brokerName = getHostName();
        else
            this.brokerName = brokerName;

        this.brokerPort = brokerPort;
        this.ID = ID;
        this.networkManager = netMngr;
        this.isService = isService;
        this.serviceDescription = serviceDescription;
       createClient();

    }
    public boolean isConnect() throws Exception {

        return mqttClient.isConnected();
    }
    public void connect() throws Exception {

        if(!isConnect()) {

            mqttClient.connect();

            if(isService) {
                try {
                    registerBroker();

                } catch (Exception e) {

                    disconnect();
                    throw e;
                }
            }
        }
        startWatchdog();
        LOG.info("MQTT broker is connected");
    }
    public void disconnect() throws Exception {

        stopWatchdog();
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
        LOG.info("MQTT broker is disconnected");

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
        return Utils.getBrokerURL(brokerName, brokerPort);
    }

    public void createClient() throws MqttException {
        mqttClient = new MqttClient(getBrokerURL(),ID.toString(), new MemoryPersistence());

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
                        LOG.error("Error in the watch dog of broker service:"+e.getMessage(),e);
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


        Registration[] registration = networkManager.getServiceByDescription(serviceDescription);
        if(registration != null && registration.length > 0){
            new RemoteException("There is already a service with this description");

        }else {
            Part[] attributes = {
                    new Part("DESCRIPTION",description),
                    new Part("UUID",ID.toString())

            };

            brokerRegistrationInfo = networkManager.registerService(attributes,getBrokerName(),MqttBackboneProtocolImpl.class.getName());
            LOG.info("MQTT broker service is registered");
        }
    }
    private void deregisterBroker() throws RemoteException {
        Registration[] registration = networkManager.getServiceByDescription(brokerRegistrationInfo.getDescription());
        if(registration != null && registration.length > 0) {
            networkManager.removeService(brokerRegistrationInfo.getVirtualAddress());
            LOG.info("MQTT broker service is deregistered");
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
            connect();

         mqttClient.publish(topic,payload,qos,retained);
    }

    public void setServiceDescription(String serviceDescription) {
        try {
            deregisterBroker();
        } catch (RemoteException e) {
            LOG.error("Error while changing service description: "+e.getMessage(),e);
        }
        this.serviceDescription = serviceDescription;
        try {
            registerBroker();
        } catch (RemoteException e) {
            LOG.error("Error while changing service description: "+e.getMessage(),e);
        }
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
    public boolean isService() {
        return isService;
    }

    public void setService(boolean isService) throws RemoteException {
        if(this.isService!=isService) {
            if(this.isService){
                deregisterBroker();
            }else {
                registerBroker();
            }
            this.isService = isService;
        }
    }
}
