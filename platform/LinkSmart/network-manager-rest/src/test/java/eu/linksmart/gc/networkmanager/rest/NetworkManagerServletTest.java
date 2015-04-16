package eu.linksmart.gc.networkmanager.rest;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import eu.linksmart.gc.networkmanager.rest.NetworkManagerRestPort;
import eu.linksmart.gc.networkmanager.rest.NetworkManagerServlet;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.networkmanager.NetworkManager;
import eu.linksmart.gc.api.utils.Part;

public class NetworkManagerServletTest {

	private static final String vadString = "0.0.0.6986094776732394497";
	private static final String CALCULATOR_DESCRIPTION = "Calculator";
	private static final String GET_RESPONSE_JSON = "[{\"Attributes\":{\"DESCRIPTION\":\"Calculator\"},\"VirtualAddress\":\"" + vadString + "\"}]";
	//private static final String GET_RESPONSE_JSON = "[{\"Attributes\":{\"DESCRIPTION\":\"Calculator\"},\"VirtualAddress\":\"" + vadString + "\",\"Endpoint\":\"/0/" + vadString + "\"}]";
	private NetworkManager networkManager;
	private VirtualAddress calculatorVad = new VirtualAddress(vadString);
	private Part[] descriptionOnly;
	private NetworkManagerRestPort nmRestPort;
	private NetworkManagerServlet nmRestPortServlet;
	private Registration calculatorRegistration;
	
	@Before
	public void setUp(){
		//mock objects
		this.networkManager = mock(NetworkManager.class);
		
		//init objects	
		this.nmRestPort = new NetworkManagerRestPort();
		this.nmRestPort.networkManager = this.networkManager;
		this.nmRestPortServlet = new NetworkManagerServlet(this.nmRestPort);
		this.descriptionOnly = new Part[]{new Part(ServiceAttribute.DESCRIPTION.name(), CALCULATOR_DESCRIPTION)};
		this.calculatorRegistration = new Registration(calculatorVad, descriptionOnly);

		//mock methods
		try{
			when(this.networkManager.getServiceByAttributes(any(Part[].class))).thenReturn(new Registration[]{new Registration(calculatorVad, this.descriptionOnly)});
		} catch (Exception e) {
			//NOP
		}
	}
	
	//@Test
	public void doGetTest() {
		
		String description = "Calculator";
		String pid = "Calculator";
		String query = "Calculator";
		
		try {
			when(this.networkManager.getServiceByDescription(description)).thenReturn(new Registration[]{this.calculatorRegistration});
			when(this.networkManager.getServiceByPID(pid)).thenReturn(this.calculatorRegistration);
			when(this.networkManager.getServiceByQuery(query)).thenReturn(new Registration[]{this.calculatorRegistration});
			when(this.networkManager.getServiceByAttributes(descriptionOnly)).thenReturn(new Registration[]{this.calculatorRegistration});
			when(this.networkManager.getServiceByAttributes(descriptionOnly, 12345678, false, false)).thenReturn(new Registration[]{this.calculatorRegistration});
		} catch (RemoteException e1) {
			//not relevant
		}
		
		try {
			
			HttpServletRequest request = mock(HttpServletRequest.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			ServletOutputStream outStream = mock(ServletOutputStream.class);
			
			when(response.getOutputStream()).thenReturn(outStream);
			
			String description_qs = "description=\"Calculator\"";
			when(request.getQueryString()).thenReturn(description_qs);
			when(request.getCharacterEncoding()).thenReturn("UTF-8");
			nmRestPortServlet.doGet(request, response);
			
			Mockito.verify(outStream).write(GET_RESPONSE_JSON.getBytes());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			
			HttpServletRequest request = mock(HttpServletRequest.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			ServletOutputStream outStream = mock(ServletOutputStream.class);
			
			when(response.getOutputStream()).thenReturn(outStream);
			
			String pid_qs = "pid=Calculator";
			when(request.getQueryString()).thenReturn(pid_qs);
			when(request.getCharacterEncoding()).thenReturn("UTF-8");
			nmRestPortServlet.doGet(request, response);
			
			Mockito.verify(outStream).write(GET_RESPONSE_JSON.getBytes());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			
			HttpServletRequest request = mock(HttpServletRequest.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			ServletOutputStream outStream = mock(ServletOutputStream.class);
			
			when(response.getOutputStream()).thenReturn(outStream);
			
			String query_qs = "query=Calculator";
			when(request.getQueryString()).thenReturn(query_qs);
			when(request.getCharacterEncoding()).thenReturn("UTF-8");
			nmRestPortServlet.doGet(request, response);
			
			Mockito.verify(outStream).write(GET_RESPONSE_JSON.getBytes());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			
			HttpServletRequest request = mock(HttpServletRequest.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			ServletOutputStream outStream = mock(ServletOutputStream.class);
			
			when(response.getOutputStream()).thenReturn(outStream);
			
			String single_attribute_qs = "single=one";
			when(request.getQueryString()).thenReturn(single_attribute_qs);
			when(request.getCharacterEncoding()).thenReturn("UTF-8");
			nmRestPortServlet.doGet(request, response);
			
			Mockito.verify(outStream).write(GET_RESPONSE_JSON.getBytes());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			
			HttpServletRequest request = mock(HttpServletRequest.class);
			HttpServletResponse response = mock(HttpServletResponse.class);
			ServletOutputStream outStream = mock(ServletOutputStream.class);
			
			when(response.getOutputStream()).thenReturn(outStream);
			
			String multiple_attribute_qs = "single=one&double=two";
			when(request.getQueryString()).thenReturn(multiple_attribute_qs);
			when(request.getCharacterEncoding()).thenReturn("UTF-8");
			nmRestPortServlet.doGet(request, response);
			
			Mockito.verify(outStream).write(GET_RESPONSE_JSON.getBytes());
			
		} catch (IOException e) {
			fail(e.getMessage());
		}

//		try {
//			
//			HttpServletRequest request = mock(HttpServletRequest.class);
//			HttpServletResponse response = mock(HttpServletResponse.class);
//			ServletOutputStream outStream = mock(ServletOutputStream.class);
//			
//			when(response.getOutputStream()).thenReturn(outStream);
//			
//			String multiple_attribute_params_qs = "single=one&double=two&timeOut=12345678&returnFirst=false&isStrictRequest=false";
//			when(request.getQueryString()).thenReturn(multiple_attribute_params_qs);
//			nmRestPortServlet.doGet(request, response);
//			
//			Mockito.verify(outStream).write(GET_RESPONSE_JSON.getBytes());
//			
//		} catch (IOException e) {
//			fail(e.getMessage());
//		}
	}
	
	@Test
	public void doPostTest() {
		
		String ENDPOINT = "http://localhost:9090/cxf/services/Calculator";
		String BACKBONE_NAME = "eu.linksmart.gc.network.backbone.protocol.http.HttpImpl";
		String POST_REQUEST_JSON = "{\"Attributes\":{\"DESCRIPTION\":\"Calculator\"}, \"Endpoint\":\"" + ENDPOINT + "\", \"BackboneName\":\"" + BACKBONE_NAME + "\"}";
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		ServletOutputStream outStream = mock(ServletOutputStream.class);
		
		BufferedReader reader = new BufferedReader(new StringReader(POST_REQUEST_JSON + "\n"));
		try {
			when(request.getReader()).thenReturn(reader);
			when(request.getContentLength()).thenReturn(POST_REQUEST_JSON.length());
			when(response.getOutputStream()).thenReturn(outStream);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			nmRestPortServlet.doPost(request, response);
		} catch (IOException e1) {
			fail(e1.getMessage());
		}
		
		try {
			Mockito.verify(this.networkManager).registerService(descriptionOnly, ENDPOINT, BACKBONE_NAME);
		} catch (RemoteException e) {
			// not relevant as local invocation
		}
	}
	
	//@Test
	public void doDeleteTest() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		when(request.getPathInfo()).thenReturn("/" + this.calculatorVad.toString());
		
		try {
			nmRestPortServlet.doDelete(request, response);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		
		try {
			Mockito.verify(this.networkManager).removeService(this.calculatorVad);
		} catch (RemoteException e) {
			//not relevant as local invocation
		}
	}
}
