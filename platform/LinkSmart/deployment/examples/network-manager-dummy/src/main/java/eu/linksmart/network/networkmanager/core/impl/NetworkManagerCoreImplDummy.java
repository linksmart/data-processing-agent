package eu.linksmart.network.networkmanager.core.impl;

import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.MessageDistributor;
import eu.linksmart.gc.api.network.MessageProcessor;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.security.communication.SecurityProperty;
import eu.linksmart.gc.api.utils.Part;

@Component(name="NetworkManagerCore", immediate=true)
@Service({NetworkManagerCore.class})
public class NetworkManagerCoreImplDummy implements NetworkManagerCore, MessageDistributor {
	
	private Logger LOG = Logger.getLogger(NetworkManagerCoreImplDummy.class.getName());
	
	protected VirtualAddress myVirtualAddress;
	protected ConcurrentHashMap<VirtualAddress, Registration> localServices = new ConcurrentHashMap<VirtualAddress, Registration>();
	
	@Reference(name="ConfigurationAdmin",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindConfigAdmin", 
			unbind="unbindConfigAdmin",
			policy=ReferencePolicy.STATIC)
	protected ConfigurationAdmin configAdmin = null;
	
	@Reference(name="BackboneRouter",
			cardinality = ReferenceCardinality.MANDATORY_UNARY,
			bind="bindBackboneRouter", 
			unbind="unbindBackboneRouter",
			policy=ReferencePolicy.DYNAMIC)
	protected BackboneRouter backboneRouter = null;
	
	protected void bindConfigAdmin(ConfigurationAdmin configAdmin) {
		LOG.debug("NetworkManagerCoreDummy::binding configadmin");
		this.configAdmin = configAdmin;
    }
    
    protected void unbindConfigAdmin(ConfigurationAdmin configAdmin) {
    	LOG.debug("NetworkManagerCoreDummy::un-binding configadmin");
    	this.configAdmin = null;
    }
    
	protected void bindBackboneRouter(BackboneRouter backboneRouter) {
		LOG.debug("NetworkManagerCoreDummy::binding backbone-router");
		this.backboneRouter = backboneRouter;
	}

	protected void unbindBackboneRouter(BackboneRouter backboneRouter) {
		LOG.debug("NetworkManagerCoreDummy::un-binding backbone-router");
		this.backboneRouter = null;
	}
	
	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("[activating NetworkManagerCoreDummy]");
		NetworkManagerCoreConfigurator configurator = new NetworkManagerCoreConfigurator(this, context.getBundleContext(), this.configAdmin);
		configurator.registerConfiguration();
		Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(),	"NetworkManager:LinkSmartUser") };
		try {
			this.myVirtualAddress = registerService(attributes, "http://localhost:9090/cxf/services/NetworkManager", "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl").getVirtualAddress();
		} catch (RemoteException e) {
			LOG.error("PANIC - RemoteException thrown on local access of own method", e);
		} catch (Exception e) {
			LOG.error("Error creating registraiton for NetworkManager. This will cause serious problems!", e);
		}
	}
	
	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info("de-activating NetworkManagerCoreDummy");
	}
	
	@Override
	@Deprecated
	public VirtualAddress getService() {
		return this.myVirtualAddress;
	}

	@Override
	public VirtualAddress getVirtualAddress() {
		return this.myVirtualAddress;
	}

	@Override
	public Registration registerService(Part[] attributes, String endpoint, String backboneName) throws RemoteException {
		VirtualAddress virtualAddress = createUniqueVirtualAddress();
		Registration newRegistration = new Registration(virtualAddress, attributes);
		if (!localServices.containsKey(virtualAddress)) {
			localServices.put(virtualAddress, newRegistration);
		}
		this.backboneRouter.addRouteToBackbone(newRegistration.getVirtualAddress(), backboneName, endpoint);
		LOG.info("service is registered with virtual-addess: " + newRegistration.getVirtualAddress());
		return newRegistration;
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress) throws RemoteException {
		if (localServices.containsKey(virtualAddress)) {
			localServices.remove(virtualAddress);
			LOG.info("service is removed with virtual-addess: " + virtualAddress);
		}
		this.backboneRouter.removeRoute(virtualAddress, null);
		return true;
	}
	
	@Override
	public Registration[] getServiceByAttributes(Part[] attributes, long timeOut, boolean returnFirst, boolean isStrictRequest) {
		return getServiceByAttributes(attributes);
	}
	
	@Override
	public Registration[] getServiceByAttributes(Part[] attributes) {
		Registration[] serviceRegistrations = new Registration[1];
		if(attributes.length == 1 && attributes[0].getKey().equals(ServiceAttribute.DESCRIPTION.name())) {
			String description = attributes[0].getValue();
			Iterator<Registration> registrations = localServices.values().iterator();
			while(registrations.hasNext()) {
				Registration registration = registrations.next();
				if(registration.getDescription().equals(description)) {
					serviceRegistrations[0] = registration;
					break;
				}
			}
		} else if(attributes.length == 1 && attributes[0].getKey().equals(ServiceAttribute.PID.name())) {
			String key = attributes[0].getKey();
			String pid = attributes[0].getValue();
			Iterator<Registration> registrations = localServices.values().iterator();
			while(registrations.hasNext()) {
				Registration registration = registrations.next();
				Part[] attrs = registration.getAttributes();
				for(Part attr : attrs) {
					if((attr.getKey().equals(key))) {
						if(attr.getValue().equals(pid)) {
							serviceRegistrations[0] = registration;
							break;
						}
					}
				}
			}
		}
		return serviceRegistrations;
	}
	
	@Override
	public Registration[] getServiceByDescription(String description) {
		Part part_description = new Part(ServiceAttribute.DESCRIPTION.name(), description);
		return getServiceByAttributes(new Part[] { part_description });
	}

	@Override
	public Registration[] getServiceByQuery(String query) {
		Part part_description = new Part(ServiceAttribute.DESCRIPTION.name(), query);
		return getServiceByAttributes(new Part[] { part_description });
	}

	@Override
	public Registration getServiceByPID(String PID) throws IllegalArgumentException {
		Part part_description = new Part(ServiceAttribute.PID.name(), PID);
		Registration[] registrations = getServiceByAttributes(new Part[] { part_description });
		return registrations[0];
	}
	
	@Override
	public NMResponse sendData(VirtualAddress sender, VirtualAddress receiver, byte[] data, boolean synch) throws RemoteException {
		return this.sendMessage(new Message(Message.TOPIC_APPLICATION, sender, receiver, data), synch);
	}
	
	@Override
	public NMResponse sendMessage(Message message, boolean synch) {
		//byte[] serializedData = MessageSerializerUtiliy.serializeMessage(message, true, true);
		NMResponse response = this.backboneRouter.sendDataSynch(message.getSenderVirtualAddress(), message.getReceiverVirtualAddress(), message.getData());
		return response;
	}
	
	@Override
	public String[] getAvailableBackbones() {
		List<String> backbones = this.backboneRouter.getAvailableBackbones();
		String[] backboneNames = new String[backbones.size()];
		return backbones.toArray(backboneNames);
	}
	
	public void updateDescription(String description) {
	}

	@Override
	public void updateSecurityProperties(List<VirtualAddress> virtualAddressesToUpdate, List<SecurityProperty> properties) {
	}
	
	@Override
	public void addRemoteVirtualAddress(VirtualAddress senderVirtualAddress, VirtualAddress remoteVirtualAddress) {
	}
	
	@Override
	public NMResponse receiveDataSynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		return null;
	}

	@Override
	public NMResponse receiveDataAsynch(VirtualAddress senderVirtualAddress, VirtualAddress receiverVirtualAddress, byte[] data) {
		return null;
	}

	@Override
	public NMResponse broadcastMessage(Message message) {
		return null;
	}
	
	@Override
	public void subscribe(String topic, MessageProcessor observer) {
	}

	@Override
	public void unsubscribe(String topic, MessageProcessor observer) {
	}
	
	private VirtualAddress createUniqueVirtualAddress() {
		VirtualAddress virtualAddress;
		do {
			virtualAddress = new VirtualAddress();
		} while (existsDeviceID(virtualAddress.getDeviceID()));
		return virtualAddress;
	}
	
	private boolean existsDeviceID(long deviceID) {
		boolean is = false;
		Enumeration<VirtualAddress> virtualAddresses;
		virtualAddresses = localServices.keys();
		while (virtualAddresses.hasMoreElements()) {
			if (virtualAddresses.nextElement().getDeviceID() == deviceID) {
				is = true;
				LOG.debug("Duplicated deviceID " + deviceID + ". ");
				break;
			}
		}
		return is;
	}
}
