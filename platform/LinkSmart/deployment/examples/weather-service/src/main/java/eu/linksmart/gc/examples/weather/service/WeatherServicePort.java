package eu.linksmart.gc.examples.weather.service;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;
import org.apache.felix.scr.annotations.*;

import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.network.networkmanager.NetworkManager;
import eu.linksmart.gc.api.utils.Part;

@Component(name="WeatherService", immediate=true)
public class WeatherServicePort {

	private static Logger LOG = Logger.getLogger(WeatherServicePort.class.getName());

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
    
    private Registration registration = null;

    protected void bindHttpServlet(HttpService http) {
        this.http = http;
    }

    protected void unbindHttpServlet(HttpService http) {
        this.http = null;
    }
    
    protected void bindNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}

	protected void unbindNetworkManager(NetworkManager networkManager) {
		this.networkManager = null;
	}
    
    @Activate
	protected void activate(ComponentContext context) {
    	LOG.info("activating WeatherService Servlet");
        try {
			this.http.registerServlet("/WeatherService", new WeatherServiceServlet(), null, null);
			registerService();
		} catch (Exception e) {
			LOG.error("error registering WeatherService Servlet", e);
		}
	}

    @Deactivate
	protected void deactivate(ComponentContext context) {
		try {
			this.http.unregister("/WeatherService");
			removeService();
		} catch (Exception e) {
			LOG.error("error unregistering WeatherService Servlet", e);
		}	
	}
    
    private void registerService() throws Exception {
    	registration = this.networkManager.registerService(new Part[] { new Part(ServiceAttribute.DESCRIPTION.name(), "WeatherService") }, 
				"http://localhost:8882/WeatherService", 
				"eu.linksmart.gc.network.backbone.protocol.http.HttpImpl");
		LOG.info("virtualAddress of a weather service: " + registration.getVirtualAddressAsString());
    }
    
    private void removeService() throws Exception {
    	this.networkManager.removeService(registration.getVirtualAddress());
    }
}