package eu.linksmart.gc.network.backbone.protocol.mqtt;

import eu.linksmart.gc.api.types.MqttTunnelledMessage;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

public class ForwardingListener extends Observable implements MqttCallback {

    private Logger LOG = Logger.getLogger(MqttBackboneProtocolImpl.class.getName());
    private MqttClient client;
    private String brokerURL;
    private String listening;
    private Observer observer;
    private long id = 0;
    private final UUID originProtocol;
    // needed to remove the files created by the Paho client
    private String _fileName;


    public ForwardingListener(String brokerURL, String listening,UUID originProtocol, Observer observer ) throws MqttException {
        this.observer = observer;
        this.brokerURL = brokerURL;
        this.listening = listening;
        _fileName= ".~" + UUID.randomUUID().toString();
        this.originProtocol =originProtocol;
        init();
    }


    private void init() throws MqttException {
        client = new MqttClient(brokerURL, _fileName);
        client.connect();
        client.setCallback(this);
        client.subscribe(listening);


    }
    public void restart() throws MqttException {
        disconnect();
        init();
    }
    @Override
    public void connectionLost(Throwable throwable) {

        do {
            try {

                LOG.info("A listened was disconnected, no is trying to reconnect");

                client.connect();

                if(!client.isConnected())
                        Thread.sleep(1000);



            } catch (Exception e) {
                LOG.error("Listener disconnected:"+e.getMessage(),e);
            }

        }while (!client.isConnected());


    }
    private synchronized long getMessageIdentifier(){
        id = (id+1)%Long.MAX_VALUE;
        return id;
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        LOG.info("Message arrived in listener");
        observer.update(this, new MqttTunnelledMessage(topic,mqttMessage.getPayload(),mqttMessage.getQos(),mqttMessage.isRetained(),getMessageIdentifier(),originProtocol));

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

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }
}