package eu.linksmart.gc.network.tunneling.http;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.http.HttpService;

import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;

@Component(name="HttpTunnel", immediate=true)
public class HttpTunnel {

	private static Logger LOG = Logger.getLogger(HttpTunnel.class.getName());
	
	@Reference(name="HttpService",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindHttpServlet", 
			unbind="unbindHttpServlet", 
			policy=ReferencePolicy.STATIC)
	private HttpService http;
	
	@Reference(name="NetworkManagerCore",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindNetworkManagerCore", 
			unbind="unbindNetworkManagerCore",
			policy=ReferencePolicy.DYNAMIC)
	private NetworkManagerCore nmCore;
	
	protected void bindHttpServlet(HttpService http) {
		this.http = http;
	}
	
	protected void unbindHttpServlet(HttpService http) {
		this.http = null;
	}
	
	protected void bindNetworkManagerCore(NetworkManagerCore nmCore) {
		this.nmCore = nmCore;
	}
	
	protected void unbindNetworkManagerCore(NetworkManagerCore nmCore) {
		this.nmCore = null;
	}
	
	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("[activating HttpTunnel]");
		try {
			this.http.registerServlet("/HttpTunneling", new HttpTunnelServlet(this.nmCore),	null, null);
			LOG.info("registring /HttpTunneling servlet]");
		} catch (Exception e) {
			LOG.error("Error registering http-tunneling servlet", e);
		}
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info("[de-activating HttpTunnel]");
		try {
			this.http.unregister("/HttpTunneling");
		} catch (Exception e) {
			LOG.error("Error unregistering http-tunneling servlet", e);
		}	
	}
	
}
