package eu.linksmart.gc.networkmanager.rest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.utils.Part;
import eu.linksmart.it.utils.ITConfiguration;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NetworkManagerRestIT {
	
	private static Logger LOG = Logger.getLogger(NetworkManagerRestIT.class.getName());
	
	private String base_url = "http://localhost:8882/NetworkManager";
	
	private static final String KEY_ENDPOINT = "Endpoint";
	private static final String KEY_BACKBONE_NAME = "BackboneName";
	private static final String KEY_ATTRIBUTES = "Attributes";
	private static final String KEY_VIRTUAL_ADDRESS = "VirtualAddress";
	
	private HttpClient httpClient = null;
	
	private String virtualAddress = null;
	
    @Configuration
    public Option[] config() {
        return new Option[] {
        		ITConfiguration.regressionDefaults(),
        		features(ITConfiguration.getTestingFeaturesRepoURL(),"gc-network-manager-rest-it"),  
        };
    }
    
    @Before
    public void setUp() {
    	httpClient = new HttpClient();
    }
    
    @Test
    public void testNMRest() {
    	try {
    		
    		LOG.info("testing NetworkManager REST");

            // GET
            testGetByDesc("WeatherService", HttpStatus.SC_OK);
            testGetByDesc("NoService", HttpStatus.SC_NOT_FOUND);

            // POST
            testPOST(createPostJSON(), HttpStatus.SC_OK);
            
            // PUT
            testPUT(createPutJSON(), HttpStatus.SC_OK);

            // DELETE
            testDELETE(virtualAddress, HttpStatus.SC_OK);
            testDELETE("0.0.0.1", HttpStatus.SC_NOT_FOUND);

            LOG.info("HttpTunnel test successfully completed");
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
	}
    
    private void testGetByDesc(String description, int expectedStatus) {
    	
    	try {
    		
    		LOG.info("testing testGetByDesc: " + base_url + " - desc: " + description);
    			  	
        	//
        	// with queryString  ?description=NetworkManager:LinkSmartUser
        	//
        	NameValuePair[] description_qs = { new NameValuePair("description", description) };
        	HttpMethod  description_get_request = new GetMethod(base_url);
        	description_get_request.setQueryString(description_qs);
        	
        	int statusCode = this.httpClient.executeMethod(description_get_request);
        	System.out.println("status-code: " + statusCode);
        	System.out.println("get-description: " + new String(description_get_request.getResponseBody()));
        	
        	description_get_request.releaseConnection();
        	assertEquals(expectedStatus, statusCode);
        	
        	LOG.info("testGetByDesc successfully completed");
        	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
    }
    
    private void testGetByOthers(int expectedStatus) {
    	
    	try {

        	//
        	// with queryString  ?pid=eu.linksmart.network
        	//
//        	NameValuePair[] pid_qs = { new NameValuePair("pid", "eu.linksmart.network") };
//        	HttpMethod  pid_qs_get_request = new GetMethod(base_url);
//        	pid_qs_get_request.setQueryString(pid_qs);
//        	assertEquals(404, client.executeMethod(pid_qs_get_request));
//        	System.out.println("pid-response-string" + new String(pid_qs_get_request.getResponseBody()));
//        	pid_qs_get_request.releaseConnection();
//        	
//        	//
//        	// with queryString  ?query=querytoexecute
//        	//
//        	NameValuePair[] query_qs = { new NameValuePair("query", "NetworkManager:LinkSmartUser") };
//        	HttpMethod  query_qs_get_request = new GetMethod(base_url);
//        	query_qs_get_request.setQueryString(query_qs);
//        	assertEquals(404, client.executeMethod(query_qs_get_request));
//        	System.out.println("query-response-string" + new String(query_qs_get_request.getResponseBody()));
//        	query_qs_get_request.releaseConnection();
//        	
//        	//
//        	// with queryString  ?att-name=att-value
//        	//
//        	NameValuePair[] single_att_qs = { new NameValuePair("att-name", "att-value") };
//        	HttpMethod  single_att_qs_get_request = new GetMethod(base_url);
//        	single_att_qs_get_request.setQueryString(single_att_qs);
//        	assertEquals(404, client.executeMethod(single_att_qs_get_request));
//        	System.out.println("single_att-response-string" + new String(single_att_qs_get_request.getResponseBody()));
//        	single_att_qs_get_request.releaseConnection();
//        	
//        	//
//        	// with queryString  ?description=NetworkManager:LinkSmartUser&pid=eu.linksmart.network
//        	//
//        	NameValuePair[] multi_att_qs = { new NameValuePair("description", "NetworkManager:LinkSmartUser"), new NameValuePair("pid", "eu.linksmart.network") };
//        	HttpMethod  multi_att_qs_get_request = new GetMethod(base_url);
//        	multi_att_qs_get_request.setQueryString(multi_att_qs);
//        	assertEquals(404, client.executeMethod(multi_att_qs_get_request));
//        	System.out.println("multi_att-response-string" + new String(multi_att_qs_get_request.getResponseBody()));
//        	multi_att_qs_get_request.releaseConnection();
//        	
//        	//
//        	// with queryString  ?description=NetworkManager:LinkSmartUser&timeOut=12345678&returnFirst=false&isStrictRequest=false
//        	//
//        	NameValuePair[] multi_att_params_qs = { new NameValuePair("description", "NetworkManager:LinkSmartUser"),
//        			new NameValuePair("timeOut", "3000"),
//        			new NameValuePair("returnFirst", "true"),
//        			new NameValuePair("isStrictRequest", "false") };
//        	HttpMethod  multi_att_params_qs_get_request = new GetMethod(base_url);
//        	multi_att_params_qs_get_request.setQueryString(multi_att_params_qs);
//        	assertEquals(404, client.executeMethod(multi_att_params_qs_get_request));
//        	System.out.println("multi_att_params-response-string" + new String(multi_att_params_qs_get_request.getResponseBody()));
//        	multi_att_params_qs_get_request.releaseConnection();
        	
//        	LOG.info("testGetMethod successfully completed");
        	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
    }
    
    //@Test
    private void testPOST(String jsonString, int expectedStatus) {
    	
    	try {
    		
    		//
        	// register service
        	//
    		
    		LOG.info("testing POST method: " + base_url);
    		
			PostMethod post_request = new PostMethod(base_url);
			
			StringRequestEntity requestEntity = new StringRequestEntity(jsonString, "application/json", "UTF-8");
			post_request.setRequestEntity(requestEntity);
			
			int statusCode = this.httpClient.executeMethod(post_request);
			System.out.println("post-status-code: " + statusCode);
			
    		String registrationJsonString = new String(post_request.getResponseBody());
        	System.out.println("post-response: " + registrationJsonString);
        	
        	post_request.releaseConnection();
        	assertEquals(expectedStatus, statusCode);
        	
        	JSONObject jsonObject = new JSONObject(registrationJsonString.toString());
			this.virtualAddress = jsonObject.getString(KEY_VIRTUAL_ADDRESS);
	
        	LOG.info("testPOST successfully completed");
        	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
    }
    
    private void testPUT(String jsonString, int expectedStatus) {
    	
    	try {
    		
        	//
        	// updating service
        	//
			
			LOG.info("testing PUT method: " + base_url);
        	
			PutMethod put_request = new PutMethod(base_url);
			
			StringRequestEntity put_requestEntity = new StringRequestEntity(jsonString, "application/json", "UTF-8");
			put_request.setRequestEntity(put_requestEntity);
			
			int statusCode = this.httpClient.executeMethod(put_request);
			System.out.println("put-status-code: " + statusCode);
			
    		String updateJsonString = new String(put_request.getResponseBody());
        	System.out.println("put-response: " + updateJsonString);
        	
        	put_request.releaseConnection();
        	assertEquals(expectedStatus, statusCode);
        	
        	JSONObject updateJsonObject = new JSONObject(updateJsonString.toString());
			this.virtualAddress = updateJsonObject.getString(KEY_VIRTUAL_ADDRESS);
			
        	LOG.info("testPUT successfully completed");
        	
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception: " + e.getMessage());
		}
    }

    private void testDELETE(String virtualAddress, int expectedStatus) {
    	
    	try {
    		
    		//
        	// removing service
        	//
    		
    		LOG.info("testing DELETE method: " + base_url + " for VAD: " + virtualAddress);
    		
        	DeleteMethod delete_request = new DeleteMethod(base_url + "/" + virtualAddress);
        	
        	int statusCode = this.httpClient.executeMethod(delete_request);
    		System.out.println("delete-status-code: " + statusCode);
    		
    		System.out.println("delete-response: " + new String(delete_request.getResponseBody()));
        	
    		delete_request.releaseConnection();
    		assertEquals(expectedStatus, statusCode);
    		
        	LOG.info("testDELETE successfully completed");
        	
    	} catch (Exception e) {
    		e.printStackTrace();
    		fail("Exception: " + e.getMessage());
    	}
    }
    
    private String createPostJSON() throws Exception {
    	
    	JSONObject registrationJson = new JSONObject();
		
		registrationJson.put(KEY_ENDPOINT, "http://localhost:8882/WeatherService");
		registrationJson.put(KEY_BACKBONE_NAME, "eu.linksmart.gc.network.backbone.protocol.http.HttpImpl");
		
		Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(), "WeatherService"), 
				new Part(ServiceAttribute.SID.name(), "eu.linksmart.gc.example.weatherservice") };
		
		JSONObject attributesJson = new JSONObject();
		for(Part p : attributes) {
			attributesJson.put(p.getKey(), p.getValue());
		}
		registrationJson.put(KEY_ATTRIBUTES, attributesJson);
		
    	return registrationJson.toString();
    }
    
    private String createPutJSON() throws Exception {
    	
    	JSONObject updateJson = new JSONObject();
    	
    	updateJson.put(KEY_ENDPOINT, "http://localhost:8882/WeatherService2");
    	updateJson.put(KEY_BACKBONE_NAME, "eu.linksmart.gc.network.backbone.protocol.http.HttpImpl");
    	updateJson.put(KEY_VIRTUAL_ADDRESS, this.virtualAddress);
    	
		Part[] update_attributes = { new Part(ServiceAttribute.DESCRIPTION.name(), "WeatherService2"), 
				new Part(ServiceAttribute.SID.name(), "eu.linksmart.gc.example.weatherservice2") };

		JSONObject update_attributesJson = new JSONObject();
		for(Part p : update_attributes) {
			update_attributesJson.put(p.getKey(), p.getValue());
		}
		updateJson.put(KEY_ATTRIBUTES, update_attributesJson);
    	
		return updateJson.toString();
    }
}