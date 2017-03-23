package eu.linksmart.test.services.utils.serialization;

import eu.linksmart.services.utils.mqtt.subscription.ForwardingListener;
import eu.linksmart.services.utils.mqtt.subscription.MqttMessageObserver;
import eu.linksmart.services.utils.mqtt.subscription.TopicMessageDeliverable;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import eu.linksmart.services.utils.mqtt.types.Topic;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by José Ángel Carvajal on 28.02.2017 a researcher of Fraunhofer FIT.
 */
public class MqttPackageTest {
    public static final String topic = "topic1", topic2 =  "topic2";
    public static final byte[] message = "world".getBytes();
    @Test
    public void topicMessageDeliverableTest(){
        TopicMessageDeliverable tmp = new TopicMessageDeliverable(topic);

        TestObserver testObserver = new TestObserver(topic);
        tmp.addObserver(testObserver);

        tmp.addMessage(new MqttMessage(topic, message, 0, false, 0, UUID.randomUUID()));
        assertEquals(1, tmp.countObservers());
        tmp.countObservers();

        for (int i=0;!testObserver.received ; i++) {
            if(i>10){
                fail();
                return;
            }else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    fail();
                }
            }
        }
        assertEquals(true,testObserver.received);
        tmp.deleteObserver(testObserver);
        assertEquals(0, tmp.countObservers());
    }
    @Test
    public void forwardingListenerTest(){
        TestConnectionObserver connectionObserver = new TestConnectionObserver();
        ForwardingListener forwardingListener = new ForwardingListener(connectionObserver,UUID.randomUUID());
        connectionObserver.forwardingListener = forwardingListener;
        TestObserver testObserver = new TestObserver(topic), testObserver2 = new TestObserver(topic2);

        assertEquals(true, forwardingListener.getListeningTopics().isEmpty());

        forwardingListener.addObserver(topic,testObserver);
        forwardingListener.addObserver(topic2,testObserver2);

        assertEquals(false, forwardingListener.getListeningTopics().isEmpty());

        forwardingListener.messageArrived(topic, new org.eclipse.paho.client.mqttv3.MqttMessage(message));
        forwardingListener.messageArrived(topic2, new org.eclipse.paho.client.mqttv3.MqttMessage(message));

        forwardingListener.connectionLost(new Exception(topic));



        for (int i=0;!testObserver.received && !testObserver2.received; i++) {
            if(i>10){
                fail();
                return;
            }else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    fail();
                }
            }
        }
        assertEquals(false,  forwardingListener.removeObserver(topic2, testObserver));
        assertEquals(false,  forwardingListener.removeObserver(topic,testObserver2));

        assertEquals(false, forwardingListener.getListeningTopics().isEmpty());

        assertEquals(true,  forwardingListener.removeObserver(topic, testObserver));
        assertEquals(true,  forwardingListener.removeObserver(topic2,testObserver2));
        assertEquals(true, forwardingListener.getListeningTopics().isEmpty());


    }
    @Test
    public void topicTest(){
        Topic ttopic1 = new Topic(topic),ttopic11 = new Topic(topic), ttopic2 = new Topic(topic2), wildTopic =new Topic("#") , wildTopic2= new Topic("+/"+topic), wildTopic3 =new Topic("/#") , wildTopic4= new Topic("/+/"+topic);

        assertEquals(true,ttopic1.equals(ttopic1));
        assertEquals(true,ttopic1.equals(ttopic11));
        assertEquals(true,ttopic1.equals(topic));

        assertEquals(true, wildTopic.equals(ttopic1));
        assertEquals(true, wildTopic3.equals("/"+topic));
        assertEquals(true, wildTopic2.equals("+/"+topic));

        assertEquals(true, wildTopic3.equals("/"+topic));
        assertEquals(true, wildTopic4.equals("/+/"+topic));

        assertEquals(false,ttopic1.equals(wildTopic));
        assertEquals(false,ttopic1.equals(wildTopic2));


        assertEquals(false,ttopic1.equals(ttopic2));
        assertEquals(false,ttopic1.equals(topic2));


    }

    private class TestObserver implements MqttMessageObserver {
        String topic;
        boolean received = false;

        public TestObserver(String topic) {
            this.topic = topic;
        }

        @Override
        public void update(String topic, MqttMessage orgMessage) {
            assertEquals(true, received = Arrays.deepEquals(ArrayUtils.toObject(orgMessage.getPayload()), ArrayUtils.toObject(message)));

        }
    }
    private class TestConnectionObserver implements Observer {

        public ForwardingListener forwardingListener;


        @Override
        public void update(Observable o, Object arg) {
            assertEquals(forwardingListener,arg);
        }
    }
}
