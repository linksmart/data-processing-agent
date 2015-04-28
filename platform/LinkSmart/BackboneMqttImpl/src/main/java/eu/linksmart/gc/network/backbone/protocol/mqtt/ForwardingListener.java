package eu.linksmart.gc.network.backbone.protocol.mqtt;

import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForwardingListener extends Observable implements MqttCallback {

    private Logger LOG = Logger.getLogger(MqttBackboneProtocolImpl.class.getName());
    private MqttClient client;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private MqttConnectOptions options;
    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) throws MqttException {
        if (!this.brokerName.equals(brokerName)) {
            this.brokerName = brokerName;
            restart();
        }

    }
    public void setBroker(String brokerName, String brokerPort) throws Exception {

        if(!this.brokerName.equals(brokerName) || !this.brokerPort.equals(brokerPort)) {

            if (!this.brokerPort.equals(brokerPort)) {
                this.brokerPort = brokerPort;

            }
            if (!this.brokerName.equals(brokerName)) {
                this.brokerName = brokerName;

            }

            restart();
        }

    }
    public String getBrokerPort() {
        return brokerPort;
    }

    public void setBrokerPort(String brokerPort) throws MqttException {
        if(!this.brokerPort.equals(brokerPort)) {
            this.brokerPort = brokerPort;
            restart();
        }
    }

    private String brokerName;
    private String brokerPort;
    private String listening;
    private Observer observer;
    private long id = 0;
    private final UUID originProtocol;




    public ForwardingListener(String brokerName,String brokerPort, String listening,UUID originProtocol, Observer observer ) throws MqttException {
        this.brokerPort = brokerPort;
        this.brokerName = brokerName;
        this.observer = observer;
        this.listening = listening;
        this.originProtocol =originProtocol;
        options = new MqttConnectOptions();
        options.setCleanSession(false);
        init();

    }

    private void init() throws MqttException {
        client = new MqttClient(BrokerConnectionService.getBrokerURL(brokerName,brokerPort), originProtocol.toString()+listening,new MemoryPersistence());
        client.connect(options);
        client.setCallback(this);
        client.subscribe(listening);
    }
    public void restart() throws MqttException {
        disconnect();
        init();
    }
    @Override
    public void connectionLost(Throwable throwable) {

        while (!client.isConnected()) {
            try {

                LOG.info("A listened was disconnected, no is trying to reconnect");

                client.connect(options);
                if(!client.isConnected()){
                    try {
                        client.close();
                    }catch (Exception e){}
                    init();
                }


                if(!client.isConnected())
                        Thread.sleep(1000);



            } catch (Exception e) {
                LOG.error("Listener disconnected:"+e.getMessage(),e);
            }

        }


    }

    private synchronized long getMessageIdentifier(){
        id = (id+1)%Long.MAX_VALUE;
        return id;
    }
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        LOG.info("Message arrived in listener:"+topic);

        executor.execute(new MessageDeliverer(new MqttTunnelledMessage(topic,mqttMessage.getPayload(),mqttMessage.getQos(),mqttMessage.isRetained(),getMessageIdentifier(),originProtocol),observer,this));
        LOG.info("Message sent");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.info("delivery complete  in listener");

    }

    public MqttClient getClient() {
        return client;
    }

    public void setClient(MqttClient client) {
        this.client = client;
    }



    public String getListening() {
        return listening;
    }

    public void setListening(String listening) {
        this.listening = listening;
    }
    public void disconnect() throws MqttException {

        client.unsubscribe(listening);
        client.disconnect();
        client.close();

    }


}