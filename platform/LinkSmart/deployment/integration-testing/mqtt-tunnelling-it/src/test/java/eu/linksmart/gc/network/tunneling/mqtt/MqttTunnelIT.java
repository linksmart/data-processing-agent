package eu.linksmart.gc.network.tunneling.mqtt;

import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.utils.Part;
import eu.linksmart.it.utils.ITConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import sun.awt.windows.ThemeReader;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class MqttTunnelIT {

	private static Logger LOG = Logger.getLogger(MqttTunnelIT.class.getName());

	private String nmBaseUrl = "http://localhost:8882/NetworkManager";
    private String BrokerURL = "tcp://localhost:1883";
	private static final String KEY_ENDPOINT = "Endpoint";
	private static final String KEY_TEMPERATURE = "Temperature";
    private static final String KEY_BACKBONE_NAME = "BackboneName";
    private static final String KEY_ATTRIBUTES = "Attributes";
    private static final String KEY_VIRTUAL_ADDRESS = "VirtualAddress";


    private HttpClient httpClient = null;
    private MqttClient mqttClient;

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(true),
        		features(ITConfiguration.getTestingFeaturesRepoURL(),"gc-mqtt-tunneling-it"),
        };
    }
    private String createPostJSON(String name) throws Exception {

        JSONObject registrationJson = new JSONObject();

        registrationJson.put(KEY_ENDPOINT, "/"+name);
        registrationJson.put(KEY_BACKBONE_NAME, "tcp://localhost:1883");

        Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(), name),
                new Part(ServiceAttribute.SID.name(), "/"+name) };

        JSONObject attributesJson = new JSONObject();
        for(Part p : attributes) {
            attributesJson.put(p.getKey(), p.getValue());
        }
        registrationJson.put(KEY_ATTRIBUTES, attributesJson);

        return registrationJson.toString();
    }

    @Before
    public void setUp() {

        try {
            mqttClient = new MqttClient(BrokerURL,"test");

        } catch (MqttException e) {
            fail(e.getMessage());
        }
        httpClient = new HttpClient();
    }

    Boolean done =false;
    int i =0;
    @Test
    public void testMqttTunnel() {
        LOG.info("testing MQTTTunnel");
        String endpoint = null;
        try {

            endpoint = getBroker("DOMAIN","fit.fraunhofer.de");

            LOG.info("setup completed, service is accessible at endpoint: " + endpoint);

        // Remote Subscribe
          //  testGETRequest(endpoint, HttpStatus.SC_ACCEPTED);
            testPub(endpoint);

            mqttClient.disconnect();
            mqttClient.close();
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
        /*
        // GET
        testGETRequest(endPoint + "/sensor/1?loc=abc", HttpStatus.SC_OK);
        testGETRequest(endPoint + "/sensor/1?loc=nowhere", HttpStatus.SC_NOT_FOUND);


//        	JSONObject jsonObject = new JSONObject(resBody);
//    		int temperature = jsonObject.getInt(KEY_TEMPERATURE);

        // PUT
        testPUTRequest(endPoint, HttpStatus.SC_OK);
//        	JSONObject updateJsonObject = new JSONObject(resBody);
//        	int updated_temperature = updateJsonObject.getInt(KEY_TEMPERATURE);

        // DELETE
        testDELETERequest(endPoint + "/123", HttpStatus.SC_OK);
*/
        LOG.info("HttpTunnel test successfully completed");
	}

    private String getBroker(String key, String description) {
        String endpoint =null;
        try {

            LOG.info("testing testGetByDesc: " + nmBaseUrl + " - "+key+": " + description);

            //
            // with queryString  ?description=NetworkManager:LinkSmartUser
            //
            NameValuePair[] description_qs = { new NameValuePair(key, description) };
            HttpMethod  description_get_request = new GetMethod(nmBaseUrl);
            description_get_request.setQueryString(description_qs);

            int statusCode = this.httpClient.executeMethod(description_get_request);
            System.out.println("status-code: " + statusCode);
            String rawRegInfo = new String(description_get_request.getResponseBody());
            JSONArray registrationJson = new JSONArray(rawRegInfo);
            endpoint = registrationJson.getJSONObject(0).getString("Endpoint");


            System.out.println("get-description: " +endpoint);

            description_get_request.releaseConnection();
            assertEquals(HttpStatus.SC_OK, statusCode);

            LOG.info("testGetByDesc successfully completed");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception: " + e.getMessage());
        }
        return endpoint;

    }
    private void testPub(String endpoint) throws Exception {
        if (!mqttClient.isConnected())
            mqttClient.connect();
        mqttClient.subscribe("/test");
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                if(s.equals("/test"))
                    i++;
                done =true;
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

        // POST
        testPOSTRequest(endpoint, HttpStatus.SC_ACCEPTED);

        do{
            Thread.sleep(500);
        }while (!done);
        done =false;
        mqttClient.unsubscribe("/test");
    }
    private void testSub(String endpoint) throws Exception {
        if (!mqttClient.isConnected())
            mqttClient.connect();
        mqttClient.subscribe("/test");
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                if(s.equals("/test"))
                    i++;

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

       mqttClient.publish("/test","test".getBytes(),0,false);

        do{
            Thread.sleep(500);
        }while (i==2);
        done =false;
        mqttClient.unsubscribe("/test");
    }

    private String getPostString() throws Exception {
    	JSONObject postJson = new JSONObject();
    	postJson.put("Temperature", 28);
		return postJson.toString();
    }
    
    private String getPutString() throws Exception {
    	
    	JSONObject updateJson = new JSONObject();
    	updateJson.put("Temperature", 20);
		return updateJson.toString();
    }

    private String testGETRequest(String endpoint, int expectedStatus) {
        int statusCode = 0;
        String resBody = null;

        LOG.info("invoking GET at endpoint: " + endpoint);
        HttpMethod getMethod = new GetMethod(endpoint);
        try {
            statusCode = httpClient.executeMethod(getMethod);
            resBody = new String(getMethod.getResponseBody());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception: " + e.getMessage());
        }
        LOG.info("get-tunnel-response: " + resBody);
        getMethod.releaseConnection();
        assertEquals(expectedStatus, statusCode);

        return resBody;
    }

    private String testPOSTRequest(String endpoint, int expectedStatus) {
        int statusCode = 0;
        String resBody = null;
        StringRequestEntity requestEntity = null;

        LOG.info("invoking POST at endpoint: " + endpoint);
        PostMethod postMethod = new PostMethod(endpoint);
        try {
            String postString = getPostString();
            requestEntity = new StringRequestEntity(postString, "application/json", "UTF-8");
            postMethod.setRequestEntity(requestEntity);

            statusCode = httpClient.executeMethod(postMethod);
            resBody = new String(postMethod.getResponseBody());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception: " + e.getMessage());
        }

        LOG.info("post-tunnel-response: " + resBody);
        postMethod.releaseConnection();
        assertEquals(expectedStatus, statusCode);

        return resBody;
    }

    private String testPUTRequest(String endpoint, int expectedStatus) {
        int statusCode = 0;
        String resBody = null;
        StringRequestEntity requestEntity = null;

        LOG.info("invoking PUT at endpoint: " + endpoint);
        PutMethod putMethod = new PutMethod(endpoint);
        String putString = null;
        try {
            putString = getPutString();
            requestEntity = new StringRequestEntity(putString, "application/json", "UTF-8");
            putMethod.setRequestEntity(requestEntity);

            statusCode = httpClient.executeMethod(putMethod);
            resBody = new String(putMethod.getResponseBody());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception: " + e.getMessage());
        }

        LOG.info("put-tunnel-response: " + resBody);
        putMethod.releaseConnection();
        assertEquals(expectedStatus, statusCode);

        return resBody;
    }

    private String testDELETERequest(String endpoint, int expectedStatus) {
        int statusCode = 0;
        String resBody = null;
        StringRequestEntity requestEntity = null;

        LOG.info("invoking DELETE at endpoint: " + endpoint);
        DeleteMethod deleteMethod = new DeleteMethod(endpoint);
        try {
            statusCode = httpClient.executeMethod(deleteMethod);
            resBody = new String(deleteMethod.getResponseBody());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception: " + e.getMessage());
        }
        LOG.info("delete-tunnel-response: " + resBody);
        deleteMethod.releaseConnection();
        assertEquals(expectedStatus, statusCode);

        return resBody;
    }

    // Big hello to java folks
    public static String join(String r[],String d)
    {
        if (r.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        int i;
        for(i=0;i<r.length-1;i++)
            sb.append(r[i]+d);
        return sb.toString()+r[i];
    }
}