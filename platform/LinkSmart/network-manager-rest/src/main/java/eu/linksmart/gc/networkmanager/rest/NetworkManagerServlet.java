package eu.linksmart.gc.networkmanager.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.utils.Part;

public class NetworkManagerServlet extends HttpServlet {

	private static final long serialVersionUID = 4799084413331453345L;
	
	private static Logger LOG = Logger.getLogger(NetworkManagerServlet.class.getName());
	
	private static final String KEY_ENDPOINT = "Endpoint";
	private static final String KEY_BACKBONE_NAME = "BackboneName";
	private static final String KEY_ATTRIBUTES = "Attributes";
	private static final String KEY_VIRTUAL_ADDRESS = "VirtualAddress";
	
	private static final String TUNNELING_PATH = "/HttpTunneling/0/";
	
	private NetworkManagerRestPort networkManagerPort = null;

	public NetworkManagerServlet(NetworkManagerRestPort networkManagerPort) {
		this.networkManagerPort = networkManagerPort;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String queryString = request.getQueryString();
		
		LOG.debug("received Query String in doGet: " + queryString);
			
		if(queryString == null || queryString.length() == 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "service query string is empty");
			return;
		}
		
		Registration[] registrations = null;
		
		try {
			
			if(request.getCharacterEncoding() != null)
				queryString = URLDecoder.decode(queryString, request.getCharacterEncoding());
			else
				queryString = URLDecoder.decode(queryString, "UTF-8");
			
			String[] queryAttributes = queryString.split("&");

			if(queryAttributes.length == 1) {
				String queryAttribute = queryAttributes[0];
				int separatorIndex = queryAttribute.indexOf("=");
				String attributeName = queryAttribute.substring(0, separatorIndex);
				String attributeValue = queryAttribute.substring(separatorIndex + 1);
				if(attributeName.equals("description")) {
					registrations = this.networkManagerPort.getNetworkManager().getServiceByDescription(removeCharacters(attributeValue));	
				} else if(attributeName.equals("pid")) {
					Registration registration = this.networkManagerPort.getNetworkManager().getServiceByPID(removeCharacters(attributeValue));
					if(registration != null) {
						registrations = new Registration[] { registration };
					}
				} else if(attributeName.equals("query")) {
					registrations = this.networkManagerPort.getNetworkManager().getServiceByQuery(removeCharacters(attributeValue));
				} else {
					Part single_part = new Part(attributeName, removeCharacters(attributeValue));
					registrations = this.networkManagerPort.getNetworkManager().getServiceByAttributes(new Part[] { single_part });
				}
			} else {
				ArrayList<Part> nmAttributes = new ArrayList<Part>();
				long timeOut = 0;
				boolean returnFirst = true;
				boolean isStrictRequest = true;
				boolean timeOutInit = false;
				boolean returnFirstInit = false;
				boolean isStrictRequestInit = false;
				for(String queryAttribute : queryAttributes) {
					int separatorIndex = queryAttribute.indexOf("=");
					String attributeName = queryAttribute.substring(0, separatorIndex);
					String attributeValue = queryAttribute.substring(separatorIndex + 1);
					if(attributeName.equals("timeOut")) {
						timeOut = Long.parseLong(attributeValue);
						timeOutInit = true;
					}
					else if(attributeName.equals("returnFirst")) {
						returnFirst = Boolean.parseBoolean(attributeValue);
						returnFirstInit = true;
					}
					else if(attributeName.equals("isStrictRequest")) {
						isStrictRequest = Boolean.parseBoolean(attributeValue);
						isStrictRequestInit = true;
					}
					else {
						nmAttributes.add(new Part(attributeName, removeCharacters(attributeValue)));
					}
			    }
				if(timeOutInit && returnFirstInit && isStrictRequestInit)
					registrations = this.networkManagerPort.getNetworkManager().getServiceByAttributes(nmAttributes.toArray(new Part[]{}), timeOut, returnFirst, isStrictRequest);
				else
					registrations = this.networkManagerPort.getNetworkManager().getServiceByAttributes(nmAttributes.toArray(new Part[]{}));
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		if(registrations == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "unable to create registration(s)");
			return;
		}
		
		if(registrations.length == 0) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such registration(s) found");
			return;
		}
		
		if(registrations.length == 1) {
			if(registrations[0] == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such registration(s) found");
				return;
			}
		}
		
		JSONArray registrationsJson = new JSONArray();

		try {
			for(Registration registration : registrations) {
				JSONObject registrationJson = new JSONObject();
				registrationJson.put(KEY_VIRTUAL_ADDRESS, registration.getVirtualAddressAsString());
				registrationJson.put(KEY_ENDPOINT, "http://" + request.getServerName() + ":" + request.getServerPort() + TUNNELING_PATH + registration.getVirtualAddressAsString());
				JSONObject attributesJson = new JSONObject();
				if(registration.getAttributes() != null) {
					for(Part p : registration.getAttributes()) {
						attributesJson.put(p.getKey(), p.getValue());
					}
					registrationJson.put(KEY_ATTRIBUTES, attributesJson);
				}
				registrationsJson.put(registrationJson);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		String responseString = registrationsJson.toString();
		response.setContentLength(responseString.length());
		response.getOutputStream().write(responseString.getBytes());
		response.getOutputStream().close();
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//
		// create JSONObject from message payload
		//
		StringBuilder requestBuilder = new StringBuilder();
		
		if (request.getContentLength() > 0) {
			try {
				BufferedReader reader = request.getReader();
				for (String line = null; (line = reader.readLine()) != null;)
					requestBuilder.append(line);
				reader.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "unable to read from request stream");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_NO_CONTENT, "request content is empty");
			return;
		}

            ArrayList<Part> attributes = new ArrayList<Part>();
		String endpoint = null;
		String backboneName = null;
		
		try {
			JSONObject registrationJson = new JSONObject(requestBuilder.toString());
			endpoint = registrationJson.getString(KEY_ENDPOINT);
			backboneName = registrationJson.getString(KEY_BACKBONE_NAME);
			JSONObject attributesJson = registrationJson.getJSONObject(KEY_ATTRIBUTES);
			Iterator iterator = attributesJson.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				attributes.add(new Part(key.toUpperCase(), attributesJson.getString(key)));
			}
		} catch (JSONException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		
		if(endpoint == null || backboneName == null || attributes.size() == 0) {
			LOG.error("Some required fields/attributes for registration are missing from request");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Some required fields/attributes for registration are missing from request");
			return;
		}
		
		Registration registration = null;
		
		try {
			registration = this.networkManagerPort.getNetworkManager().registerService(attributes.toArray(new Part[]{}), endpoint, backboneName);
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		//
		// return new registration as json
		//
		JSONObject registrationJson = new JSONObject();
		
		try {
			if(registration != null) {
				registrationJson.put(KEY_VIRTUAL_ADDRESS, registration.getVirtualAddressAsString());
				registrationJson.put(KEY_ENDPOINT, "http://" + request.getServerName() + ":" + request.getServerPort() + TUNNELING_PATH + registration.getVirtualAddressAsString());
				JSONObject attributesJson = new JSONObject();
				for(Part p : registration.getAttributes()) {
					attributesJson.put(p.getKey(), p.getValue());
				}
				registrationJson.put(KEY_ATTRIBUTES, attributesJson);
			} else{
				LOG.error("The registration was not successful, try again");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"The registration was not successful, try again");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		String responseString = registrationJson.toString();
		response.setContentLength(responseString.length());
		response.getOutputStream().write(responseString.getBytes());
		response.getOutputStream().close();
	}
	
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//
		// create JSONObject from message payload
		//
		StringBuilder requestBuilder = new StringBuilder();
		
		if (request.getContentLength() > 0) {
			try {
				BufferedReader reader = request.getReader();
				for (String line = null; (line = reader.readLine()) != null;)
					requestBuilder.append(line);
				reader.close();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "unable to read from request stream");
				return;
			}
		} else {
			response.sendError(HttpServletResponse.SC_NO_CONTENT, "request content is empty");
			return;
		}

		ArrayList<Part> attributes = new ArrayList<Part>();
		String endpoint = null;
		String backboneName = null;
		String virtualAddress = null;
		
		try {
			JSONObject registrationJson = new JSONObject(requestBuilder.toString());
			endpoint = registrationJson.getString(KEY_ENDPOINT);
			backboneName = registrationJson.getString(KEY_BACKBONE_NAME);
			virtualAddress = registrationJson.getString(KEY_VIRTUAL_ADDRESS);
			JSONObject attributesJson = registrationJson.getJSONObject(KEY_ATTRIBUTES);
			Iterator iterator = attributesJson.keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				attributes.add(new Part(key.toUpperCase(), attributesJson.getString(key)));
			}
		} catch (JSONException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
			return;
		}
		
		if(endpoint == null || backboneName == null || attributes.size() == 0) {
			LOG.error("Some required fields/attributes for registration update are missing from request");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Some required fields/attributes for registration update are missing from request");
			return;
		}
		
		if(new VirtualAddress(virtualAddress).getBytes().length != VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH) {
			LOG.error("provided virtual-address doesn't conform to its format: " + virtualAddress);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "provided virtual-address doesn't conform to its format: " + virtualAddress);
			return;
		}
		
		try {
			boolean serviceFound = this.networkManagerPort.getNetworkManager().removeService(new VirtualAddress(virtualAddress));
			if(!serviceFound) {
				LOG.error("no such service registered with virtual address: " + virtualAddress);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such service registered with this virtual address: " + virtualAddress);
				return;
			} 
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		Registration registration = null;
		
		try {
			registration = this.networkManagerPort.getNetworkManager().registerService(attributes.toArray(new Part[]{}), endpoint, backboneName);
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}

		//
		// return new registration as json
		//
		JSONObject registrationJson = new JSONObject();
		
		try {
			if(registration != null) {
				registrationJson.put(KEY_VIRTUAL_ADDRESS, registration.getVirtualAddressAsString());
				registrationJson.put(KEY_ENDPOINT, "http://" + request.getServerName() + ":" + request.getServerPort() + TUNNELING_PATH + registration.getVirtualAddressAsString());
				JSONObject attributesJson = new JSONObject();
				for(Part p : registration.getAttributes()) {
					attributesJson.put(p.getKey(), p.getValue());
				}
				registrationJson.put(KEY_ATTRIBUTES, attributesJson);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		String responseString = registrationJson.toString();
		response.setContentLength(responseString.length());
		response.getOutputStream().write(responseString.getBytes());
		response.getOutputStream().close();
	}
	
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String pathInfo = request.getPathInfo();
		
		if(pathInfo == null || pathInfo.length() == 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "pathInfo is empty, no virtual-address is given");
			return;
		}
		
		String virtualAddress = request.getPathInfo().substring(1);
		
		if(new VirtualAddress(virtualAddress).getBytes().length != VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH) {
			LOG.error("provided virtual-address doesn't conform to its format: " + virtualAddress);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "provided virtual-address doesn't conform to its format: " + virtualAddress);
			return;
		}
				
		try {
			boolean resp = this.networkManagerPort.getNetworkManager().removeService(new VirtualAddress(virtualAddress));
			if(!resp) {
				LOG.error("no such service registered with virtual address: " + virtualAddress);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "no such service registered with this virtual address: " + virtualAddress);
			} 
		} catch (RemoteException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
	}
	
	private String removeCharacters(String attributeValue) {
		//
		// remove quotation symbols
		//
		if(attributeValue.startsWith("\"") && attributeValue.endsWith("\"")) {
			attributeValue = attributeValue.substring(1, attributeValue.length() - 1);
		} else if(attributeValue.startsWith("%22") && attributeValue.endsWith("%22")) {
			attributeValue = attributeValue.substring(3, attributeValue.length() - 3);
		} 
		return attributeValue;
	}

}
