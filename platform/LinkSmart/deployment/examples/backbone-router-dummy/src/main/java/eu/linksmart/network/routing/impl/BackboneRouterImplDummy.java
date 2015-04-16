package eu.linksmart.network.routing.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.*;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.backbone.Backbone;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.network.routing.RouteEntry;
import eu.linksmart.gc.api.security.communication.SecurityProperty;

@Component(name="LinkSmartBackboneRouter", immediate=true)
@Service
public class BackboneRouterImplDummy implements BackboneRouter {
	
	private Logger LOG = Logger.getLogger(BackboneRouterImplDummy.class.getName());

	private Map<VirtualAddress, Backbone> activeRouteMap = new HashMap<VirtualAddress, Backbone>();

    private Map<String, Backbone> availableBackbones = new ConcurrentHashMap<String, Backbone>();
    
    private BackboneRouterConfigurator configurator;
    
    @Reference(name="ConfigurationAdmin",
            cardinality = ReferenceCardinality.MANDATORY_UNARY,
            bind="bindConfigAdmin",
            unbind="unbindConfigAdmin",
            policy=ReferencePolicy.STATIC)
    private ConfigurationAdmin mConfigAdmin = null;

    @Reference(name="Backbone",
            cardinality = ReferenceCardinality.OPTIONAL_MULTIPLE,
            bind="bindBackbone",
            unbind="unbindBackbone",
            policy= ReferencePolicy.DYNAMIC)
    private Backbone mBackbone;
    
    @Reference(name="NetworkManagerCore",
            cardinality = ReferenceCardinality.OPTIONAL_UNARY,
            bind="bindNMCore",
            unbind="unbindNMCore",
            policy= ReferencePolicy.DYNAMIC)
    private NetworkManagerCore nmCore;
    
    protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
    	LOG.debug("BackboneRouterDummy::binding configadmin");
        this.mConfigAdmin = configAdmin;
    }

    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	LOG.debug("BackboneRouterDummy::un-binding configadmin");
        this.mConfigAdmin = null;
    }

    protected void bindBackbone(Backbone backbone) {
    	LOG.debug("BackboneRouterDummy::binding backbone");
        mBackbone = backbone;
        if (!(availableBackbones.containsValue(backbone))) {
        	availableBackbones.put(backbone.getClass().getName(), backbone);
        	LOG.info("Added backbone to router: " + backbone.getName());
        } 
    }
    
    protected void unbindBackbone(Backbone backbone) {
    	LOG.debug("BackboneRouterDummy::un-binding backbone");
        mBackbone = backbone;
        availableBackbones.values().remove(backbone);
    }
    
    protected void bindNMCore(NetworkManagerCore core) {
    	LOG.debug("BackboneRouterDummy::binding networkmanager-core");
        nmCore = core;
    }

    protected void unbindNMCore(NetworkManagerCore core) {
    	LOG.debug("BackboneRouterDummy::un-binding networkmanager-core");
        nmCore = null;
    }

    @Activate
    protected void activate(ComponentContext context) {
    	LOG.info("[activating BackboneRouterDummy]");
        configurator = new BackboneRouterConfigurator(this, context.getBundleContext(), mConfigAdmin);
        configurator.registerConfiguration();
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
    	LOG.info("de-activating BackboneRouterDummy");
    }
    
    @Override
    public boolean addRouteToBackbone(VirtualAddress virtualAddress, String backboneName, String endpoint) {
    	Backbone backbone = availableBackbones.get(backboneName);
    	if(backbone != null) {
    		LOG.info("backbone found with name: " + backbone.getName());
    		if (activeRouteMap.containsKey(virtualAddress)) {
    			LOG.info("VAD is already added in router map: " + virtualAddress);
    			return false;
    		}   
        	if (!backbone.addEndpoint(virtualAddress, endpoint)) {
        		LOG.error("addEndpoint to backbone failed");
        		return false;
        	}
        	LOG.info("adding VAD to router map: " + virtualAddress);	
        	activeRouteMap.put(virtualAddress, backbone);
            return true;
    	} else {
    		LOG.error("backbone is not found with name: " + backboneName);
    	}
    	return false;	
    }
    	
    @Override
    public boolean removeRoute(VirtualAddress virtualAddress, String backbone) {
    	if (backbone == null) {
            return (activeRouteMap.remove(virtualAddress) != null);
        }
        if (activeRouteMap.get(virtualAddress) == null || !activeRouteMap.get(virtualAddress).getClass().getName().equals(backbone)) {
            return false;
        }
        activeRouteMap.remove(virtualAddress);
        return true;
    }

    @Override
    public NMResponse sendDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
    	LOG.debug("received request by Backbone router for VAD: " + receiverVirtualAddress);
        Backbone b = (Backbone) activeRouteMap.get(receiverVirtualAddress);
        if(b!= null)  {
        	LOG.debug("found Backbone in map for VAD: " + receiverVirtualAddress + " - with name: " + b.getName());
        	return b.sendDataSynch(senderVirtualAddress, receiverVirtualAddress, data);
        } else {
        	LOG.debug("Backbone not found by router for VAD: " + receiverVirtualAddress);
        	return new NMResponse(NMResponse.STATUS_ERROR);
        }	
    }

    @Override
    public NMResponse sendDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
    	Backbone b = (Backbone) activeRouteMap.get(receiverVirtualAddress);
        if(b!= null) 
        	return b.sendDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);
        else
        	return new NMResponse(NMResponse.STATUS_ERROR);
    }

    @Override
    public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, 
                                       VirtualAddress receiverVirtualAddress, byte[] data,
                                       Backbone originatingBackbone) {
    	Backbone b = (Backbone) activeRouteMap.get(senderVirtualAddress);
        if (b == null) 
            activeRouteMap.put(senderVirtualAddress, originatingBackbone);
        if (nmCore != null) 
        	return nmCore.receiveDataSynch(senderVirtualAddress, receiverVirtualAddress, data);
        else
        	return new NMResponse(NMResponse.STATUS_ERROR);
    }

    @Override
    public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress,
                                        VirtualAddress receiverVirtualAddress, byte[] data,
                                        Backbone originatingBackbone) {
    	Backbone b = (Backbone) activeRouteMap.get(senderVirtualAddress);
        if (b == null) 
        	activeRouteMap.put(senderVirtualAddress, originatingBackbone);
        if (nmCore != null) 
            return nmCore.receiveDataAsynch(senderVirtualAddress, receiverVirtualAddress, data);
        else
            return new NMResponse(NMResponse.STATUS_ERROR);
    }

    @Override
    public void applyConfigurations(Hashtable updates) {
        if (updates.containsKey(BackboneRouterConfigurator.COMMUNICATION_TYPE)) {
            System.out.println("default route: " + (String) configurator.get(BackboneRouterConfigurator.COMMUNICATION_TYPE));
        }
    }

    @Override
    public NMResponse broadcastData(VirtualAddress senderVirtualAddress, byte[] data) {
        boolean success = false;
        for (Backbone bb : availableBackbones.values()) {
            NMResponse response = bb.broadcastData(senderVirtualAddress, data);
            if (response != null && response.getStatus() == NMResponse.STATUS_SUCCESS) {
                success = true;
            }
        }
        if (success)
            return new NMResponse(NMResponse.STATUS_SUCCESS);
        else
            return new NMResponse(NMResponse.STATUS_ERROR);
    }

    @Override
    public String getRoute(VirtualAddress virtualAddress) {
        Backbone b = activeRouteMap.get(virtualAddress);
        if (b == null) {
            return null;
        } else {
            return b.getName().concat(":").concat(b.getEndpoint(virtualAddress));
        }
    }

    @Override
    public String getRouteBackbone(VirtualAddress virtualAddress) {
        Backbone b = activeRouteMap.get(virtualAddress);
        if (b == null) {
            return null;
        } else {
            return b.getName();
        }
    }

    @Override
    public boolean addRoute(VirtualAddress virtualAddress, String backboneName) {
    	Backbone backbone = availableBackbones.get(backboneName);
    	if(backbone != null)
    		return false;
    	if (activeRouteMap.containsKey(virtualAddress)) 
            return false; 
    	activeRouteMap.put(virtualAddress, null);
        return true;
    }

    @Override
    public void addRouteForRemoteService(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
        Backbone senderBackbone = activeRouteMap.get(senderVirtualAddress);
        if (senderBackbone != null) {
            addRoute(remoteVirtualAddress, senderBackbone.getName());
            senderBackbone.addEndpointForRemoteService(senderVirtualAddress, remoteVirtualAddress);
        }
    }

    @Override
    public List<String> getAvailableBackbones() {
        return new ArrayList<String>(availableBackbones.keySet());
    }

    @Override
    public List<SecurityProperty> getBackboneSecurityProperties(String backbone) {
        if (!availableBackbones.containsKey(backbone)) {
            return null;
        } else {
            Backbone b = availableBackbones.get(backbone);
            return b.getSecurityTypesRequired();
        }
    }

    @Override
    public Map<VirtualAddress, Backbone> getCopyOfActiveRouteMap() {
        HashMap<VirtualAddress, Backbone> copiedMap = new HashMap<VirtualAddress, Backbone>();
        copiedMap.putAll(activeRouteMap);
        return copiedMap;
    }

    @Override
    public Map<VirtualAddress, List<RouteEntry>> getCopyOfPotentialRouteMap() {
        HashMap<VirtualAddress, List<RouteEntry>> copiedMap = new HashMap<VirtualAddress, List<RouteEntry>>();
        return copiedMap;
    }

}
