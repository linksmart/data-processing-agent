package eu.linksmart.gc.examples.weather.service;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.utils.Part;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class WeatherServiceTest {
	
	private static final String KEY_ENDPOINT = "Endpoint";
	private static final String KEY_BACKBONE_NAME = "BackboneName";
	private static final String KEY_ATTRIBUTES = "Attributes";
	private static final String KEY_VIRTUAL_ADDRESS = "VirtualAddress";
	
	private String nm_base_url = "http://localhost:8882/NetworkManager";
	
	private String endPoint = null;
	
    @Before
    public void setUp() {
    	
    	try {
    		
    		System.out.println("registreing service into NetworkManager & retrieve service Endpoint for Tunneling");
    		
    		HttpClient client = new HttpClient();
    		
    		//
        	// register service
        	//
//    		JSONObject registrationJson = new JSONObject();
//    		
//			registrationJson.put(KEY_ENDPOINT, "http://localhost:8082/WeatherService");
//			registrationJson.put(KEY_BACKBONE_NAME, "eu.linksmart.gc.network.backbone.protocol.http.HttpImpl");
//			
//			Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(), "WeatherService"), 
//					new Part(ServiceAttribute.SID.name(), "eu.linksmart.gc.examples.weather.service") };
//			
//			JSONObject attributesJson = new JSONObject();
//			
//			for(Part p : attributes) {
//				attributesJson.put(p.getKey(), p.getValue());
//			}
//			
//			registrationJson.put(KEY_ATTRIBUTES, attributesJson);
//			
//			PostMethod post_request = new PostMethod(nm_base_url);
//			
//			StringRequestEntity requestEntity = new StringRequestEntity(registrationJson.toString(), "application/json", "UTF-8");
//			post_request.setRequestEntity(requestEntity);
//			int post_status_code = client.executeMethod(post_request);
//    		String registrationJsonString = new String(post_request.getResponseBody());
//    		System.out.println("register-service-response: " + registrationJsonString);
//        	post_request.releaseConnection();
//        	assertEquals(200, post_status_code);
        	
    		//
    		// get service registration from network-manager using its ResT interface with queryString  ?description=name
    		//
        	NameValuePair[] description_qs = { new NameValuePair("description", "WeatherService") };
        	HttpMethod  description_get_request = new GetMethod(nm_base_url);
        	description_get_request.setQueryString(description_qs);
        	int get_status_code = client.executeMethod(description_get_request);
        	String serviceJsonString = new String(description_get_request.getResponseBody());
        	System.out.println("get-service-response: " + serviceJsonString);
        	description_get_request.releaseConnection();
        	assertEquals(200, get_status_code);
        	
        	JSONObject jsonObject = new JSONArray(serviceJsonString).getJSONObject(0);
			
        	endPoint = jsonObject.getString(KEY_ENDPOINT);
        	
        	System.out.println("setup completed, service is accessible at endpoint: " + endPoint);
        	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
    }
    
    @Test
    public void testHttpTunnel() {
    	
    	try {
    		
    		System.out.println("testing HttpTunnel");
    		
    		HttpClient client = new HttpClient();
    		
    		//
    		// GET method
    		//
            try {
            	System.out.println("invoking GET at endpoint: " + endPoint);
            	HttpMethod  getMethod = new GetMethod(endPoint);
    			int getStatusCode = client.executeMethod(getMethod);
    			System.out.println("get-response: " + new String(getMethod.getResponseBody()));
    			getMethod.releaseConnection();
    			assertEquals(200, getStatusCode);
            } catch (IOException e) {
                e.printStackTrace();
                fail("Get-Exception: " + e.getMessage());
            }
            
            //
    		// POST method
    		//
            try {
            	System.out.println("invoking POST at endpoint: " + endPoint);
                PostMethod postMethod = new PostMethod(endPoint);
            	JSONObject postJson = new JSONObject();
            	postJson.put("Temperature", 28);
            	StringRequestEntity requestEntity = new StringRequestEntity(postJson.toString(), "application/json", "UTF-8");
                postMethod.setRequestEntity(requestEntity);
                int postStatusCode = client.executeMethod(postMethod);
                System.out.println("post-response: " + new String(postMethod.getResponseBody()));
                postMethod.releaseConnection();
                assertEquals(200, postStatusCode);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Post-Exception: " + e.getMessage());
            }
            
            //
    		// PUT method
    		//
            try {
            	System.out.println("invoking PUT at endpoint: " + endPoint);
                PutMethod putMethod = new PutMethod(endPoint);
                JSONObject updateJson = new JSONObject();
            	updateJson.put("Temperature", 20);
                StringRequestEntity requestEntity = new StringRequestEntity(updateJson.toString(), "application/json", "UTF-8");
                putMethod.setRequestEntity(requestEntity);
                int putStatusCode = client.executeMethod(putMethod);
                System.out.println("put-response: " + new String(putMethod.getResponseBody()));
                putMethod.releaseConnection();
                assertEquals(200, putStatusCode);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception: " + e.getMessage());
            }
            
            //
    		// DELETE method
    		//
            try {
            	endPoint = endPoint + "/123";
            	System.out.println("invoking DELETE at endpoint: " + endPoint);
                DeleteMethod deleteMethod = new DeleteMethod(endPoint);
                int deleteStatusCode = client.executeMethod(deleteMethod);
                System.out.println("delete-response: " + new String(deleteMethod.getResponseBody()));
                deleteMethod.releaseConnection();
                assertEquals(200, deleteStatusCode);
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception: " + e.getMessage());
            }
            
			System.out.println("HttpTunnel test successfully completed");
        	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
    }

}
