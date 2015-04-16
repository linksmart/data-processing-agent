package eu.linksmart.gc.network.backbone.protocol.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import eu.linksmart.gc.api.types.TunnelRequest;
import eu.linksmart.gc.api.types.TunnelResponse;
import eu.linksmart.gc.api.types.utils.SerializationUtil;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.io.IOException;

@Component(name="BackboneHTTP", immediate=true)
@Service({Backbone.class})
public class HttpImpl implements Backbone {

	private Logger LOG = Logger.getLogger(HttpImpl.class.getName());
	
	private final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
	private final String ENDCODING_TYPE = "UTF-8";
	
	private Map<VirtualAddress, URL> virtualAddressUrlMap = new HashMap<VirtualAddress, URL>();
	
	private HttpConfigurator configurator = null;
	
	@Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy=ReferencePolicy.STATIC)
    private ConfigurationAdmin mConfigAdmin = null;
	
	@Reference(name="BackboneRouter",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindBackboneRouter",
            unbind="unbindBackboneRouter",
            policy= ReferencePolicy.STATIC)
	private BackboneRouter bbRouter;
	
    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.mConfigAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
        this.mConfigAdmin = null;
    }
    
    protected void bindBackboneRouter(BackboneRouter bbRouter) {
        this.bbRouter = bbRouter;
    }

    protected void unbindBackboneRouter(BackboneRouter bbRouter) {
        this.bbRouter = null;
    }
    
    @Activate
	protected void activate(ComponentContext context) {
    	LOG.info("[activating Backbone HttpProtocol]");
    	this.configurator = new HttpConfigurator(this, context.getBundleContext(), mConfigAdmin);
		this.configurator.registerConfiguration();
	}

    @Deactivate
	public void deactivate(ComponentContext context) {
    	LOG.info("[de-activating Backbone HttpProtocol]");
	}
    
    @Override
	public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
    	
		try {
			
			//TODO perform decoding if Encoding of messages is in place through http.properties 
			
			//
			// decoding LinkSmart Message Object
			//
			byte[] tunnel_data = SerializationUtil.deserializeMessage(data, senderVirtualAddress).getData();
			
			//
			// deserialize tunnel request
			//
			TunnelRequest tunnel_request = (TunnelRequest) SerializationUtil.deserialize(tunnel_data,  TunnelRequest.class);
			
			LOG.debug("method: " + tunnel_request.getMethod());
			LOG.debug("path: " + tunnel_request.getPath());
			LOG.debug("headers: " + tunnel_request.getHeaders().length);
			LOG.debug("body: " + new String(tunnel_request.getBody()));
				
			//
			// check if service endpoint is available
			//
			URL urlEndpoint = virtualAddressUrlMap.get(receiverVirtualAddress);
			 
			if (urlEndpoint == null) {
				String message = "cannot send tunneled data to service at virtualAddress: " + receiverVirtualAddress.toString() + ", unknown endpoint";
				LOG.error(message);
				TunnelResponse tunnel_response = new TunnelResponse();
				tunnel_response.setStatusCode(404);
				tunnel_response.setBody(message.getBytes());
				NMResponse nm_response = new NMResponse();
				nm_response.setStatus(NMResponse.STATUS_ERROR);
				nm_response.setBytesPrimary(true);
				nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response));
				return nm_response;
			}
			
			String uriEndpoint = urlEndpoint.toURI().toString();
			
			NMResponse nm_response = null;
			
			//
			// determine HTTP method
			//
			if(tunnel_request.getMethod().equals("GET")) {
				nm_response = processGET(tunnel_request, uriEndpoint);
			} else if(tunnel_request.getMethod().equals("POST")) {
				nm_response = processPOST(tunnel_request, uriEndpoint);
			} else if(tunnel_request.getMethod().equals("PUT")) {
				nm_response = processPUT(tunnel_request, uriEndpoint);
			} else if(tunnel_request.getMethod().equals("DELETE")) {
				nm_response = processDELETE(tunnel_request, uriEndpoint);
			} else {
				throw new Exception("unsupported HTTP method for endpoint:" + uriEndpoint);
			}
			
			//
			// creating & encoding LinkSmart Message Object
			//
			Message r_message = new Message("applicationData", senderVirtualAddress, receiverVirtualAddress, nm_response.getMessageBytes());
			nm_response.setMessageBytes(SerializationUtil.serializeMessage(r_message, true));
			
			return nm_response;
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			TunnelResponse tunnel_response = new TunnelResponse();
			tunnel_response.setStatusCode(500);
			tunnel_response.setBody(e.getMessage().getBytes());
			NMResponse nm_response = new NMResponse();
			nm_response.setStatus(NMResponse.STATUS_ERROR);
			nm_response.setBytesPrimary(true);
			try { nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response)); } catch (IOException e1) {	e1.printStackTrace(); }
			return nm_response;
		}
	}
    
    private NMResponse processGET(TunnelRequest tunnel_request, String uriEndpoint) throws Exception {
    	
    	NMResponse nm_response = new NMResponse();
    	
    	TunnelResponse tunnel_response = new TunnelResponse();
    	
    	//
    	// parse service path for query string
    	//
    	List<NameValuePair> query_string_pairs = new ArrayList<NameValuePair>();
    	
		String query_string = null;
		
		String path = tunnel_request.getPath();
		
		if(path != null && !(path.isEmpty())) {
			if(path.contains("?")) {
				if(path.startsWith("?")) {
					query_string = path.substring(1);
				} else {
					StringTokenizer path_tokens = new StringTokenizer(path, "?");
					uriEndpoint = uriEndpoint + path_tokens.nextToken();
					query_string = path_tokens.nextToken();
				}
				if(query_string != null) {
		    		StringTokenizer query_tokens = new StringTokenizer(query_string, "&");
		    		while(query_tokens.hasMoreTokens()) {
		    			String[] parameter = query_tokens.nextToken().split("=");
		    			if(parameter.length == 2) {
		    				query_string_pairs.add(new NameValuePair(parameter[0], parameter[1]));
		    			}
		    		}
				}
	    	} else {
	    		uriEndpoint = uriEndpoint + path;
	    	}
		} 
			
    	//
		// invoke service endpoint
		//
		HttpClient client = new HttpClient();

    	HttpMethod  get_request = new GetMethod(uriEndpoint);
    	
    	if(query_string_pairs.size() > 0) {
    		get_request.setQueryString(query_string_pairs.toArray(new NameValuePair[query_string_pairs.size()]));
    	}
    	
    	int status_code = client.executeMethod(get_request);
    	byte[] service_response = get_request.getResponseBody();
    	get_request.releaseConnection();
    	
    	//
		// wrap tunnel response inside network-manager response object
		//
    	tunnel_response.setStatusCode(status_code);
		tunnel_response.setBody(service_response);
		//TODO tunnel_response.setHeaders()
		nm_response.setStatus(NMResponse.STATUS_SUCCESS);
		nm_response.setBytesPrimary(true);
		nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response));
		
		return nm_response;
    }
    
    private NMResponse processPOST(TunnelRequest tunnel_request, String uriEndpoint) throws Exception {
    	
    	NMResponse nm_response = new NMResponse();
    	
    	TunnelResponse tunnel_response = new TunnelResponse();
    	
		HttpClient client = new HttpClient();
		PostMethod post_request = new PostMethod(uriEndpoint + tunnel_request.getPath());
		StringRequestEntity requestEntity = new StringRequestEntity(new String(tunnel_request.getBody()), APPLICATION_JSON_CONTENT_TYPE, ENDCODING_TYPE);
		post_request.setRequestEntity(requestEntity);
		int status_code = client.executeMethod(post_request);
    	byte[] service_response = post_request.getResponseBody();
    	post_request.releaseConnection();
    	//TODO check for chunk response
    	tunnel_response.setStatusCode(status_code);
		tunnel_response.setBody(service_response);
		//TODO tunnel_response.setHeaders()
		nm_response.setStatus(NMResponse.STATUS_SUCCESS);
		nm_response.setBytesPrimary(true);
		nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response));
		
		return nm_response;
    }
    
    private NMResponse processPUT(TunnelRequest tunnel_request, String uriEndpoint) throws Exception {
    	
    	NMResponse nm_response = new NMResponse();
    	
    	TunnelResponse tunnel_response = new TunnelResponse();
    	
		HttpClient client = new HttpClient();
		PutMethod put_request = new PutMethod(uriEndpoint + tunnel_request.getPath());
		StringRequestEntity put_requestEntity = new StringRequestEntity(new String(tunnel_request.getBody()), APPLICATION_JSON_CONTENT_TYPE, ENDCODING_TYPE);
		put_request.setRequestEntity(put_requestEntity);
		int status_code = client.executeMethod(put_request);
    	byte[] service_response = put_request.getResponseBody();
    	put_request.releaseConnection();
    	//TODO check for chunk response
    	tunnel_response.setStatusCode(status_code);
		tunnel_response.setBody(service_response);
		//TODO tunnel_response.setHeaders()
		nm_response.setStatus(NMResponse.STATUS_SUCCESS);
		nm_response.setBytesPrimary(true);
		nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response));
		
		return nm_response;
    }
    
    private NMResponse processDELETE(TunnelRequest tunnel_request, String uriEndpoint) throws Exception {

    	NMResponse nm_response = new NMResponse();
    	
    	TunnelResponse tunnel_response = new TunnelResponse();
    	
		HttpClient client = new HttpClient();
		DeleteMethod delete_request = new DeleteMethod(uriEndpoint + tunnel_request.getPath());
		int status_code = client.executeMethod(delete_request);
		byte[] service_response = delete_request.getResponseBody();
    	delete_request.releaseConnection();
    	
    	tunnel_response.setStatusCode(status_code);
		tunnel_response.setBody(service_response);
		//TODO tunnel_response.setHeaders()
		nm_response.setStatus(NMResponse.STATUS_SUCCESS);
		nm_response.setBytesPrimary(true);
		nm_response.setMessageBytes(SerializationUtil.serialize(tunnel_response));
		
		return nm_response;
    }

    @Override
	public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
    	throw new RuntimeException("Asynchronous sending not supported by HTTP!");
	}
	
    @Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] receivedData) {
    	return null;
	}
	
    @Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress,
			byte[] receivedData) {
    	return null;
	}
    
    @Override
	public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data) {
    	return null;
	}

	@Override
	public List<SecurityProperty> getSecurityTypesRequired() {
		String configuredSecurity = this.configurator.get(HttpConfigurator.SECURITY_PARAMETERS);
		String[] securityTypes = configuredSecurity.split("\\|");
		SecurityProperty oneProperty;
		List<SecurityProperty> answer = new ArrayList<SecurityProperty>();
		for (String s : securityTypes) {
			try {
				oneProperty = SecurityProperty.valueOf(s);
				answer.add(oneProperty);
			} catch (Exception e) {
				LOG.error("Security property value from configuration is not recognized: " + s + ": " + e);
			}
		}
		return answer;
	}
	
	@Override
	public String getEndpoint(VirtualAddress virtualAddress) {
        if (!virtualAddressUrlMap.containsKey(virtualAddress)) {
            return null;
        }
        return virtualAddressUrlMap.get(virtualAddress).toString();
	}

	@Override
	public boolean addEndpoint(VirtualAddress virtualAddress, String endpoint) {
        if (this.virtualAddressUrlMap.containsKey(virtualAddress)) {
        	LOG.info("virtual-addess is already store for endpoint: " + endpoint);
            return false;
        }
        try {
            URL url = new URL(endpoint);
            this.virtualAddressUrlMap.put(virtualAddress, url);
            LOG.info("virtual-addess is added for endpoint: " + endpoint);
            return true;
        } catch (MalformedURLException e) {
            LOG.debug("Unable to add endpoint " + endpoint + " for VirtualAddress " + virtualAddress.toString(), e);
        }
        return false;
    }

	@Override
	public boolean removeEndpoint(VirtualAddress virtualAddress) {
        return this.virtualAddressUrlMap.remove(virtualAddress) != null;
	}
	
	@Override
	public void addEndpointForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
        URL endpoint = virtualAddressUrlMap.get(senderVirtualAddress);
        if (endpoint != null) {
            virtualAddressUrlMap.put(remoteVirtualAddress, endpoint);
        } else {
            LOG.warn("endpoint of VirtualAddress " + senderVirtualAddress + " cannot be found");
        }
	}

	@Override
	public String getName() {
		return HttpImpl.class.getName();
	}
	
	public void applyConfigurations(Hashtable updates) {
	}

	public Dictionary getConfiguration() {
		return this.configurator.getConfiguration();
	}
}
