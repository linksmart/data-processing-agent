package eu.linksmart.services.events.testing;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.CharStreams;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.types.EventEnvelope;
import eu.linksmart.api.event.types.Statement;
import eu.linksmart.api.event.types.impl.MultiResourceResponses;
import eu.linksmart.services.payloads.ogc.sensorthing.OGCEventBuilder;
import eu.linksmart.services.payloads.ogc.sensorthing.Observation;
import eu.linksmart.services.payloads.ogc.sensorthing.linked.ObservationImpl;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by José Ángel Carvajal on 26.06.2018 a researcher of Fraunhofer FIT.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LABasicTest {
    private ObjectMapper mapper ;
    private MqttClient sender, subscriber;
    private String agentURL;
    private static final int initialStatements=0;
    private boolean lastOK= true;
    private final boolean[] arrived= {false};
    private static final int totalEvents=10;

    @Before
    public void initialization(){
        String url = System.getenv().getOrDefault("BROKER_URL", "tcp://localhost:1883");

        agentURL = System.getenv().getOrDefault("AGENT_URL", "http://localhost:8319/");
        if (agentURL.trim().charAt(agentURL.length() - 1) != '/')
            agentURL = agentURL + "/";
        waitingAgent();
        try {
            mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            sender = new MqttClient(url, "sender"+ UUID.randomUUID().toString(),new MemoryPersistence());
            subscriber = new MqttClient(url, "subscriber"+UUID.randomUUID().toString(),new MemoryPersistence());

            connect(sender);
            connect(subscriber);

        }catch (Exception e){
            e.printStackTrace();
            fail(e.getMessage());
        }

    }
    @Test
    public void T00_CEMLHello(){
        upkeep();

        Response response = execute(Request.Get(testURL(agentURL)));
        HttpResponse httpResponse = collectHttpResponse(response);
        String content= toString(httpResponse.getEntity());

        assertEquals("Must be 200 (OK)", 200, httpResponse.getStatusLine().getStatusCode());

        Map<String, Object> body = parse(content,Map.class);

        assertEquals("Body should be a not empty map", true, !body.isEmpty());

        MultiResourceResponses<Statement> root = processResponse(execute(Request.Get(testURL(agentURL+"/ceml/"))),200);

        ending();
    }
    @Test
    public void T1_0_BasicTest() {
       basicTest("");
    }
    //@Test
    public void T1_1_BasicLegacyTest() {
       basicTest("legacy");
    }
    private void basicTest(String testType){
        upkeep();
        final String tutorial_short_name = "linear_regression"+(("".equals(testType) || testType ==null)?"":"_"+testType);

        processResponse(execute(
                Request.Put(testURL(agentURL + "/ceml/" + tutorial_short_name + "/")).bodyString(
                        toString(getStatement(tutorial_short_name)),
                        ContentType.APPLICATION_JSON
                )), 201);
        final int[] count = {0};

        connectSubscribe(tutorial_short_name,count);


        for (int i = 0; i < totalEvents; i++) {
            if (!subscriber.isConnected()) {

                connectSubscribe(tutorial_short_name,count);
            }
            EventEnvelope envelope = createEvent("1", "1", i);
            publish(envelope.getClassTopic() + envelope.getAttributeId(), envelope);
            wait(500);
        }
        wait(2000);
        execute(Request.Delete(testURL(agentURL + "/ceml/" + tutorial_short_name + "/")));


        assertEquals("the expected number of events didn't arrived ", (double) totalEvents, (double)count[0],4.0);

        ending();
    }
    private void connectSubscribe(String id, int[] count){
        subscriber.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                if (topic.contains(id) && topic.contains("LA"))
                    count[0]++;
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        subscribe("#");
    }

    private Map getStatement(String name){
        Map statement = null;
        String path = "./tests.json";
        File source = new File(path);
        if(!source.exists())
            fail("statement source file not found!");
        try {
            statement =parse( Files.readAllBytes(Paths.get(path)), Map.class);
            return (Map) statement.get(name);
        } catch (Exception e) {
            fail(e.getMessage());

        }
        return statement;
    }
    private void waitingAgent() {
        boolean loaded =false;
        int i=0;
        do {
            try {

                if(Request.Get(testURL(agentURL)).execute().returnResponse().getStatusLine().getStatusCode()==200) {
                    loaded = true;
                    Thread.sleep(5000);
                }else {
                    fail("Agent did not start!");
                    System.exit(-1);
                }
            }catch (Exception ignored){
                try {
                    i++;
                    System.err.println("Waiting agent...");
                    Thread.sleep(1000);
                }catch (Exception ig){
                    // nothing
                }
                if(i>60*3){
                    fail("Agent did not start!");
                    System.exit(-1);
                }

            }
        }while (!loaded );
    }

    private void tryTill(long time, MqttClient client,String... topics){
        long before =((new Date()).getTime()), after;
        do{
            connect(client);
            subscribe(client,topics);
            after =((new Date()).getTime());
            wait(100);
        }while (after-before<time&&!arrived[0]);

    }
    private MqttClient prepareSecondBroker(){
        try {
            String url2 = System.getenv().getOrDefault("BROKER2_URL", System.getenv().getOrDefault("CITY_URL", "tcp://localhost:7881"));
            return new MqttClient(url2, "city"+UUID.randomUUID().toString(),new MemoryPersistence());
        }catch (Exception e){
            fail(e.getMessage());
            System.exit(-1);
            return null;
        }
    }
    private void upkeep(){
        isLastOK();

        MultiResourceResponses<Statement> root = processResponse(execute(Request.Get(testURL(agentURL+"/ceml/"))),200);

        assertEquals("There should be only the initial statements",initialStatements,root.getResources().size());
    }

    private void ending(){

        MultiResourceResponses<Statement> root = processResponse(execute(Request.Get(testURL(agentURL+"/ceml/"))),200);

        assertEquals("There should be only the initial statements",initialStatements,root.getResources().size());

        lastOK =true;
    }
    private String toString(HttpEntity entity){

        try (final Reader reader = new InputStreamReader(entity.getContent())) {
            return CharStreams.toString(reader);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }
    private String toString(Object object){
        try {
            return  mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
        return null;
    }
    private void isLastOK(){
        if(lastOK)
            lastOK = false;
        else
            fail("Last test failed!");
    }
    private void wait(int i){

        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private EventEnvelope createEvent(String thingId, String streamId, Object value){

        try {
            return (new OGCEventBuilder()).factory(thingId,streamId,value,(new Date()).getTime(),null,new Hashtable<>());
        }catch (Exception e){
            fail(e.getMessage());
        }
        return null;
    }
    private void publish(String topic,Object payload){
        connect(sender);
        try {
            sender.publish(topic,serialize(payload),0,false);
        }catch (Exception e){
            fail(e.getMessage());
        }
    }

    private <T> MultiResourceResponses<T> processResponse(Response response, int expectedResponseCode, TypeReference reference){


        MultiResourceResponses<T> responses = parse(_processResponse(response,expectedResponseCode),reference);
        //   assertEquals("The header response code and the body response code  must be equal", responses.getOverallStatus(), httpResponse.getStatusLine().getStatusCode());

        return responses;
    }
    private <T> MultiResourceResponses<T> processResponse(Response response, int expectedResponseCode){


        MultiResourceResponses<T> responses = parse(_processResponse(response,expectedResponseCode),MultiResourceResponses.class);

        //   assertEquals("The header response code and the body response code  must be equal", responses.getOverallStatus(), httpResponse.getStatusLine().getStatusCode());

        return responses;
    }
    private String _processResponse(Response response, int expectedResponseCode){
        HttpResponse httpResponse = null;
        try {
            httpResponse = response.returnResponse();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        String content= toString(httpResponse.getEntity());
        if(expectedResponseCode!= httpResponse.getStatusLine().getStatusCode()){
            try {
                MultiResourceResponses responses = mapper.readValue(content,MultiResourceResponses.class);
                fail("Expected code "+expectedResponseCode+" received "+httpResponse.getStatusLine().getStatusCode()+" server response message"+responses.getResponsesTail().getMessage());
            }catch (Exception ignored){
                assertEquals("Must be "+expectedResponseCode+" ", expectedResponseCode, httpResponse.getStatusLine().getStatusCode());
            }
        }


        return content;
    }
    private void subscribe(String... topics) {
        subscribe(subscriber,topics);

    }
    private void subscribe(MqttClient subscriber,String... topics) {
        connect(subscriber);
        try {
            int[] qos= new int[topics.length];

            for (int i=0;i<topics.length;i++)
                qos[i]=2;

            subscriber.subscribe(topics,qos);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
    private void unsubscribe(String... topics) {
        subscribe(subscriber,topics);

    }
    private void unsubscribe(MqttClient subscriber,String... topics) {
        connect(subscriber);
        try {
            subscriber.unsubscribe(topics);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }
    private void connect(MqttClient client) {
        int i = 5;
        while (!client.isConnected() && i > 0)
            try {
                i--;
                client.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        if(i<=0)
            fail("Unable to connect to the MQTT broker!");
    }
    private Response execute(Request request) {
        try {
            return request.execute();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    private HttpResponse collectHttpResponse(Response response) {
        try {
            return  response.returnResponse();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    private Content collectBody(Response response) {
        try {
            return response.returnContent();
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
    private <T> T  parse(String content, Class<? extends T> clas)  {
        try {
            return  mapper.readValue(content, clas);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
        return null;
    }
    private <T> T  parse(byte[] content, Class<? extends T> clas)  {
        try {
            return  mapper.readValue(content, clas);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
        return null;
    }
    private <T> T  parse(String content, TypeReference clas)  {
        try {
            return  mapper.readValue(content, clas);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
        return null;
    }
    private <T> T  parse(byte[] content, TypeReference clas)  {
        try {
            return  mapper.readValue(content, clas);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
        return null;
    }
    private byte[]  serialize(Object object)  {
        try {
            return  mapper.writeValueAsBytes(object);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
        return null;
    }
    private URI testURL(String url)  {
        try {
            return  new URI(url);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());

        }
        return null;
    }


}
