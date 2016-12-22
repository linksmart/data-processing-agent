package eu.linksmart.testing.tooling;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Hashtable;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 24.11.2016 a researcher of Fraunhofer FIT.
 */
public class Consumer implements MqttCallback{

    private final MessageValidator validator;
    private final ObjectMapper mapper;
    private final MqttAsyncClient client;
    MqttConnectOptions options;
    public Consumer(MqttAsyncClient client, int lotSize) {
        this.client =client;
        validator = new MessageValidator(this.getClass(),"0", lotSize);
        mapper = new ObjectMapper();

        options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setMaxInflight(1000);
        options.setAutomaticReconnect(false);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("Possible connection lost");
        while (!client.isConnected())


            System.err.print("Consumer: Connection lost: ");
            try {
                System.err.print("Reconnecting....");
                client.connect(options);
                if(!client.isConnected()) {
                    System.err.println(" re-connection fails!");
                    Thread.sleep(100);
                }else {
                    client.subscribe( "/federation1/#", 2);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        System.out.println(" re-connected!");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        validator.addMessage(topic, (int)mapper.readValue(message.getPayload(), Hashtable.class).get("ResultValue"));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        System.out.print("Finished");
    }
}
