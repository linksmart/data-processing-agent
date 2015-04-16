package eu.linksmart.gc.network.tunneling.http;

import eu.linksmart.it.utils.ITConfiguration;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.*;
import org.apache.log4j.Logger;

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

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class HttpTunneltIT {

	private static Logger LOG = Logger.getLogger(HttpTunneltIT.class.getName());

	private String nmBaseUrl = "http://localhost:8882/NetworkManager";
	private static final String KEY_ENDPOINT = "Endpoint";
	private static final String KEY_TEMPERATURE = "Temperature";
	private String endPoint = null;
    private HttpClient httpClient = new HttpClient();

    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(true),
        		features(ITConfiguration.getTestingFeaturesRepoURL(),"gc-http-tunneling-it"),
        };
    }

    @Before
    public void setUp() {

    	try {
    		//
    		// get service registration from network-manager using its ResT interface with queryString  ?description=name
    		//
        	NameValuePair[] qs = { new NameValuePair("description", "WeatherService") };
        	HttpMethod  getMethod = new GetMethod(nmBaseUrl);
        	getMethod.setQueryString(qs);

        	assertEquals(HttpStatus.SC_OK, httpClient.executeMethod(getMethod));
        	String serviceJson = new String(getMethod.getResponseBody());
        	LOG.info("get-service-response: " + serviceJson);
        	getMethod.releaseConnection();

        	JSONArray registrationsJson = new JSONArray(serviceJson);
        	JSONObject jsonObject = registrationsJson.getJSONObject(0);
        	endPoint = jsonObject.getString(KEY_ENDPOINT);

        	LOG.info("setup completed, service is accessible at endpoint: " + endPoint);

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Test
    public void testHttpTunnel() {
        LOG.info("testing HttpTunnel");

        // GET
        testGETRequest(endPoint + "/sensor/1?loc=abc", HttpStatus.SC_OK);
        testGETRequest(endPoint + "/sensor/1?loc=nowhere", HttpStatus.SC_NOT_FOUND);

        // POST
        testPOSTRequest(endPoint, HttpStatus.SC_OK);
//        	JSONObject jsonObject = new JSONObject(resBody);
//    		int temperature = jsonObject.getInt(KEY_TEMPERATURE);

        // PUT
        testPUTRequest(endPoint, HttpStatus.SC_OK);
//        	JSONObject updateJsonObject = new JSONObject(resBody);
//        	int updated_temperature = updateJsonObject.getInt(KEY_TEMPERATURE);

        // DELETE
        testDELETERequest(endPoint + "/123", HttpStatus.SC_OK);

        LOG.info("HttpTunnel test successfully completed");
	}


    @Test
    public void testInvalidVAD() {
        LOG.info("testing requests to invalid VAD on HttpTunnel");
        String endpoint;

        // Malicious sennder VAD
        String[] endpointParts = endPoint.split("/");
        endpointParts[endpointParts.length - 1] = "malicious-sender-vad";
        endpoint = join(endpointParts, "/");

        // GET
        testGETRequest(endpoint + "/service/path?foo=bar", HttpStatus.SC_BAD_REQUEST);

        // POST
        testPOSTRequest(endpoint, HttpStatus.SC_BAD_REQUEST);

        // PUT
        testPUTRequest(endpoint, HttpStatus.SC_BAD_REQUEST);

        // DELETE
        testDELETERequest(endpoint, HttpStatus.SC_BAD_REQUEST);

        // Malicious receiver VAD
        endpointParts = endPoint.split("/");
        endpointParts[endpointParts.length - 2] = "malicious-receiver-vad";
        endpoint = join(endpointParts, "/");

        // GET
        testGETRequest(endpoint + "/service/path?foo=bar", HttpStatus.SC_BAD_REQUEST);

        // POST
        testPOSTRequest(endpoint, HttpStatus.SC_BAD_REQUEST);

        // PUT
        testPUTRequest(endpoint, HttpStatus.SC_BAD_REQUEST);

        // DELETE
        testDELETERequest(endpoint, HttpStatus.SC_BAD_REQUEST);

        LOG.info("Test on requests to invalid VAD on HttpTunnel successfully completed");
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