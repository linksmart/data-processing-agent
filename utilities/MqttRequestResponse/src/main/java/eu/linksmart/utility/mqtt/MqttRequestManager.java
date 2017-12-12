package eu.linksmart.utility.mqtt;

import eu.linksmart.api.event.types.impl.AsyncRequest;
import eu.linksmart.api.event.types.impl.GeneralRequestResponse;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import eu.linksmart.services.utils.mqtt.broker.StaticBroker;
import eu.linksmart.services.utils.mqtt.subscription.MqttMessageObserver;
import eu.linksmart.services.utils.mqtt.types.MqttMessage;
import eu.linksmart.services.utils.serialization.DefaultDeserializer;
import eu.linksmart.services.utils.serialization.DefaultSerializer;
import eu.linksmart.services.utils.serialization.Deserializer;
import eu.linksmart.services.utils.serialization.Serializer;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by José Ángel Carvajal on 06.12.2017 a researcher of Fraunhofer FIT.
 */
public class MqttRequestManager<T>  {
    private static final String BROKER_PROFILE = "default", SERVICE_WILL="will_message", SERVICE_WILL_TOPIC="will_topic", DEFAULT_TOPIC_STRUCTURE="return_topic_structure",TIMEOUT="timeout";

    private transient StaticBroker broker;

    static transient private Configurator conf = Configurator.getDefaultConfig();
    static transient private Logger loggerService = Utils.initLoggingConf(MqttRequestManager.class);
    public static final String id = UUID.randomUUID().toString();

    private Serializer serializer = new DefaultSerializer();
    private Deserializer deserializer = new DefaultDeserializer();

    public MqttRequestManager() throws MalformedURLException, MqttException {
        this.broker = new StaticBroker(
                BROKER_PROFILE,
                conf.getString(SERVICE_WILL),
                conf.getString(SERVICE_WILL_TOPIC)

        );
    }
  /*  public void request(String topic, byte[] payload, String returnTopic, int n, Observer observer) {
        new Thread(() -> {
            try {
                observer.update(null, request(topic, payload, returnTopic,n));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }*/
    public MultiResourceResponses<T> request(String topic, byte[] payload, int n, long timeout, String[] targets) throws Exception {
        AsyncRequest request = packRequest(payload,targets);
        RequestProcessor requester= new RequestProcessor(request.getId(),n,timeout);
        broker.addListener(request.getReturnEndpoint(),requester);
        if(!request.getReturnEndpoint().equals(request.getReturnErrorEndpoint()))
            broker.addListener(request.getReturnErrorEndpoint(),requester);

        broker.publish(topic,serializer.serialize(request));
        requester.run();
        MultiResourceResponses responses =constructResponse(requester.messages);
        if ( responses.getResponses().size()<n )
            responses.addResponse(new GeneralRequestResponse("Not Found", id, requester.id,"REST-MQTT proxy","The user expected "+String.valueOf(n)+" response(s). We got "+responses.getResponses().size(),404 ));

        return responses;
    }

    public MultiResourceResponses<T> request(String topic, byte[] payload) throws Exception {

        return request(topic, payload,-1,conf.getLong(TIMEOUT));
    }

    public MultiResourceResponses<T> request(String topic, String json) throws Exception {
        return request(topic,json.getBytes());
    }

    public MultiResourceResponses<T> request(String topic, Object unserializedObject, int n, long timeout) throws Exception {
        return request(topic,serializer.serialize(unserializedObject));
    }
    private AsyncRequest packRequest(byte[] orgReq, String[] targets){
        AsyncRequest request = new AsyncRequest();
        request.setResource(orgReq);
        request.setReturnEndpoint(constructTopic(request.getId()));
        request.setReturnErrorEndpoint(constructTopic(request.getId()));
        if(targets!=null)
            request.setTargets(Arrays.asList(targets));

        return request;

    }
    private String constructTopic(String requesterID){
        return id +""+requesterID;

    }
    private MultiResourceResponses<T> constructResponse(ArrayList<MqttMessage> rawResponses){
        MultiResourceResponses resourceResponses = new MultiResourceResponses<>();
        rawResponses.forEach(i-> {
                    try {
                        MultiResourceResponses responses1 = deserializer.deserialize(i.getPayload(), MultiResourceResponses.class);

                        resourceResponses.addAllResponses(responses1.getResponses());

                        responses1.getResources().forEach((k, j) -> {
                            String id =UUID.randomUUID().toString();
                            if(j instanceof Map) {
                                Map aux = (Map) j;
                                if (aux.containsKey("AgentID"))
                                    id=aux.get("AgentID").toString();
                                if(aux.containsKey("ProducerID"))
                                    id=aux.get("ProducerID").toString();
                            }

                            resourceResponses.addResources(k.toString()+":"+id, j);
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return resourceResponses;

    }
    private class RequestProcessor implements MqttMessageObserver, Runnable{

        final protected ArrayList<MqttMessage> messages;
        final protected String id;
        final protected int targetResponses;
        final private long timeout, tickSize=1000;
        private long currentTime=0;
        RequestProcessor(String id, int targetResponses,long timeout){
            this.id=id;
            this.targetResponses=targetResponses;
            messages = new ArrayList<>(targetResponses>0?targetResponses:0);
            this.timeout = timeout;
        }
        @Override
        public void update(String topic, MqttMessage message) {
            synchronized (this){
                messages.add(message);
            }
        }

        @Override
        public void run() {
            while (!isDone()){
                long before=(new Date()).getTime();
                try {
                    Thread.sleep(tickSize);
                } catch (InterruptedException e) {
                    loggerService.error(e.getMessage(),e);
                }finally {
                    long after=(new Date()).getTime();
                    tick(after-before);
                }
            }

        }
        public synchronized boolean isDone(){
            return (messages.size()>=targetResponses && targetResponses>0)||isTimeout();
        }
        public synchronized boolean isComplete(){
            return messages.size()>=targetResponses;
        }
        public synchronized boolean isTimeout(){
            return currentTime>timeout;
        }
        private synchronized void tick(long actualTick){
            currentTime+=tickSize;
        }

    }

}
