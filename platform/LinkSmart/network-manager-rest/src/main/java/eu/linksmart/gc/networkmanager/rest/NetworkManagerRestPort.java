package eu.linksmart.gc.networkmanager.rest;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.apache.felix.scr.annotations.*;

import eu.linksmart.gc.api.network.networkmanager.NetworkManager;

@Component(name="eu.linksmart.gc.networkmanager.rest", immediate=true)
public class NetworkManagerRestPort {

	private static Logger LOG = Logger.getLogger(NetworkManagerRestPort.class.getName());
	
	private final String networkManagerRestPath = "/NetworkManager";

    @Reference(name="HttpService",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindHttpServlet",
            unbind="unbindHttpServlet",
            policy=ReferencePolicy.STATIC)
    private HttpService http;
    
    @Reference(name="NetworkManager",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindNetworkManager",
            unbind="unbindNetworkManager",
            policy= ReferencePolicy.DYNAMIC)
	protected NetworkManager networkManager;

    protected void bindHttpServlet(HttpService http) {
        LOG.debug("NetworkManagerRestPort::binding http-service");
        this.http = http;
    }

    protected void unbindHttpServlet(HttpService http) {
        LOG.debug("NetworkManagerRestPort::un-binding http-service");
        this.http = null;
    }
    
    protected void bindNetworkManager(NetworkManager networkManager) {
    	LOG.debug("NetworkManagerRestPort::binding network-manager");
		this.networkManager = networkManager;
	}

	protected void unbindNetworkManager(NetworkManager networkManager) {
		LOG.debug("NetworkManagerRestPort::un-binding network-manager");
		this.networkManager = null;
	}
    
    @Activate
	protected void activate(ComponentContext context) {
        try {
			this.http.registerServlet(networkManagerRestPath, new NetworkManagerServlet(this), null, null);
		} catch (Exception e) {
			LOG.error("error registering NetworkManager Servlet", e);
		}
	}

    @Deactivate
	protected void deactivate(ComponentContext context) {
		try {
			this.http.unregister(networkManagerRestPath);
		} catch (Exception e) {
			LOG.error("error unregistering NetworkManager Servlet", e);
		}	
	}
    
    protected NetworkManager getNetworkManager() {
		return this.networkManager;
	}
}
