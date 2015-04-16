package eu.linksmart.gc.network.identity.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.gc.api.network.BroadcastMessage;
import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.MessageDistributor;
import eu.linksmart.gc.api.network.MessageProcessor;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.ServiceAttribute;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.identity.IdentityManager;
import eu.linksmart.gc.network.identity.util.AttributeQueryParser;
import eu.linksmart.gc.network.identity.util.AttributeResolveFilter;
import eu.linksmart.gc.network.identity.util.AttributeResolveResponse;
import eu.linksmart.gc.network.identity.util.BloomFilterFactory;
import eu.linksmart.gc.network.identity.util.ByteArrayCodec;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.gc.api.utils.Part;
import eu.linksmart.gc.api.utils.PartConverter;

@SuppressWarnings("deprecation")
@Component(name="IdentityManager", immediate=true)
@Service({IdentityManager.class})
public class IdentityManagerImpl implements IdentityManager, MessageProcessor {
	public final static String IDMANAGER_UPDATE_SERVICE_LIST_TOPIC = "IDManagerServiceListUpdate";
	public final static String IDMANAGER_NMADVERTISMENT_TOPIC = "NMAdvertisement";
	public final static String IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_REQ = "NMServiceAttributeResolveRequest";
	public final static String IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_RESP = "NMServiceAttributeResolveResponse";
	public final static String SERVICE_ATTR_RESOLVE_KEYS = "AttributeKeys";
	public final static String SERVICE_ATTR_RESOLVE_FILTER = "BloomFilter";
	public final static String SERVICE_ATTR_RESOLVE_RANDOM = "Random";
	public final static String SERVICE_ATTR_RESOLVE_ID = "RequestIdentifier";

	protected static String IDENTITY_MGR = IdentityManagerImpl.class.getSimpleName();

	protected static Logger LOG = Logger.getLogger(IDENTITY_MGR);

	protected ConcurrentHashMap<VirtualAddress, Registration> localServices;
	protected ConcurrentHashMap<VirtualAddress, Registration> remoteServices;

	protected ConcurrentHashMap<VirtualAddress, Long> serviceLastUpdate;
	protected ConcurrentLinkedQueue<String> queue;
	protected ConcurrentHashMap<String, List<Message>> resolveResponses;
	protected ConcurrentHashMap<String, Object> locks;

	/**Thread to delete not updated Services.*/
	protected Thread serviceClearerThread;

	/** Thread that checks for updated in ServiceList and sends respective broadcasts. */
	protected Thread serviceUpdaterThread;
	/** Time in milliseconds to wait between broadcasts. */
	protected int broadcastSleepMillis = 1000;

	/** Thread that sends network manager advertisement broadcasts. */
	protected Thread advertisingThread;
	/** Time in milliseconds to wait between advertisements. */
	/** old valua was 6000, now changed to 300000 */
	protected int advertisementSleepMillis = 300000;  
	/** Time in milliseconds before node is deleted if not updated.*/
	protected static long SERVICE_KEEP_ALIVE_MS = (long)(2 * 300000);

	/** Flag controlling advertising thread.*/
	private boolean advertisingThreadRunning;
	/** Flag controlling update broadcaster thread.*/
	private boolean serviceUpdaterThreadRunning;
	/** Flag controlling Service clearer thread.*/
	private boolean serviceClearerThreadRunning;
	
	@Reference(name="NetworkManagerCore",
			cardinality = ReferenceCardinality.OPTIONAL_UNARY,
			bind="bindNetworkManagerCore", 
			unbind="unbindNetworkManagerCore",
			policy=ReferencePolicy.DYNAMIC)
	protected NetworkManagerCore networkManagerCore;
		
	protected void bindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		LOG.debug("IdentityManager::binding networkmanager-core");
		this.networkManagerCore = networkManagerCore;
		startThreads();
	}

	protected void unbindNetworkManagerCore(NetworkManagerCore networkManagerCore) {
		LOG.debug("IdentityManager::un-binding networkmanager-core");
		this.networkManagerCore = null;
		stopThreads();
	}

	@Activate
	protected void activate(ComponentContext context) {
		LOG.info("[activating IdentityManager]");
		LOG.info(IDENTITY_MGR + " started");
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		LOG.info(IDENTITY_MGR + "stopped");
		//TODO clear all data structures
	}
	
	private void startThreads() {
		// Start the threads once NetworkManagerCore is available
		this.serviceClearerThread = new Thread(new ServiceClearer());
		serviceClearerThread.start();
		serviceClearerThreadRunning = true;

		serviceUpdaterThread = new Thread(new ServiceUpdaterThread());
		serviceUpdaterThread.start();
		serviceUpdaterThreadRunning = true;

		advertisingThread = new Thread(new AdvertisingThread());
		advertisingThread.start();
		advertisingThreadRunning = true;

		// subscribe to messages sent by other identity managers
		((MessageDistributor) this.networkManagerCore).subscribe(
				IDMANAGER_NMADVERTISMENT_TOPIC, this);
		((MessageDistributor) this.networkManagerCore).subscribe(
				IDMANAGER_UPDATE_SERVICE_LIST_TOPIC, this);
		((MessageDistributor) this.networkManagerCore).subscribe(
				IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_REQ, this);
		((MessageDistributor) this.networkManagerCore).subscribe(
				IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_RESP, this);
	}
	
	private void stopThreads() {
		advertisingThreadRunning = false;
		serviceUpdaterThreadRunning = false;
		serviceClearerThreadRunning = false;
		//TODO fix null reference for below code
		//unsubscribe to messages sent by other identity managers
		if(this.networkManagerCore != null) {
			((MessageDistributor) this.networkManagerCore).unsubscribe(IDMANAGER_NMADVERTISMENT_TOPIC, this);
			((MessageDistributor) this.networkManagerCore).unsubscribe(IDMANAGER_UPDATE_SERVICE_LIST_TOPIC, this);
		}
		
	}
	
	public IdentityManagerImpl() {
		init();
	}
	
	protected void init() {
		this.localServices = new ConcurrentHashMap<VirtualAddress, Registration>();
		this.remoteServices = new ConcurrentHashMap<VirtualAddress, Registration>();
		this.queue = new ConcurrentLinkedQueue<String>();
		this.serviceLastUpdate = new ConcurrentHashMap<VirtualAddress, Long>();
		this.resolveResponses = new ConcurrentHashMap<String, List<Message>>();
		this.locks = new ConcurrentHashMap<String, Object>();
	}

	@Override
	public Registration createServiceByAttributes(Part[] attributes) {
		VirtualAddress virtualAddress = createUniqueVirtualAddress();
		Registration info = new Registration(virtualAddress, attributes);
		addLocalService(virtualAddress, info);
		LOG.debug("Created VirtualAddress: " + info.toString());
		return info;
	}

	@Override
	public Registration createServiceByDescription(String description) {
		Part[] attributes = { new Part(ServiceAttribute.DESCRIPTION.name(), description)};
		return createServiceByAttributes(attributes);
	}

	protected VirtualAddress createUniqueVirtualAddress() {
		VirtualAddress virtualAddress;
		do {
			virtualAddress = new VirtualAddress();
		} while (existsDeviceID(virtualAddress.getDeviceID()));
		return virtualAddress;
	}

	@Override
	public Registration getServiceInfo(VirtualAddress virtualAddress) {
		Registration answer = localServices.get(virtualAddress);

		if (answer != null) {
			return answer;
		}
		// else, look into the remote list
		answer = remoteServices.get(virtualAddress);
		return answer;
	}

	/**
	 * Returns a vector containing all the VirtualAddress inside local+remote Table
	 * 
	 * @return a vector containing all the Services inside local + remote idTable
	 */
	public Set<Registration> getAllServices() {
		Set<Registration> union = getLocalServices();
		union.addAll(getRemoteServices());
		return union;
	}

	/**
	 * Returns a vector containing all the local VirtualAddress inside localServices Table
	 * 
	 * @return a vector containing all the Services inside idTable
	 */
	public Set<Registration> getLocalServices() {
		return new HashSet<Registration>(localServices.values());
	}

	/**
	 * Returns a vector containing all the remote VirtualAddress inside idTable
	 * 
	 * @return a vector containing all the remote Services inside idTable
	 */
	public Set<Registration> getRemoteServices() {
		return new HashSet<Registration>(remoteServices.values());
	}

	@Override
	public Registration[] getServiceByAttributes(
			Part[] attributes, long timeOut,
			boolean returnFirst, boolean isStrict) {
		//if only descriptions are searched use other method
		if(attributes.length == 1 
				&& attributes[0].getKey().equals(
						ServiceAttribute.DESCRIPTION.name())) {
			Set<Registration> serviceInfos = getServicesByDescription(attributes[0].getValue());
			//create array from set
			Registration[] serviceInfoRet = new Registration[serviceInfos.size()];
			serviceInfos.toArray(serviceInfoRet);
			return serviceInfoRet;
		}

		Set<Registration> matchingServices = new HashSet<Registration>();
		//first collect local Services that match
		matchingServices.addAll(checkAttributes(getLocalServices(), attributes, isStrict));

		//only search remotely if required
		if(matchingServices.size() == 0 || !returnFirst) {
			//check if attributes have already been retrieved
			matchingServices.addAll(checkAttributes(getRemoteServices(), attributes, isStrict));
			//only do discovery if required
			if(matchingServices.size() == 0 || !returnFirst) {
				//discover remote services
				Set<Registration> remoteServices = sendResolveAttributesMsg(
						attributes, timeOut, returnFirst, isStrict);
				if(remoteServices != null) {
					matchingServices.addAll(remoteServices);
				}
			}
		}
		//create array from matches
		Registration[] serviceInfos = new Registration[matchingServices.size()];
		matchingServices.toArray(serviceInfos);
		return serviceInfos;
	}

	HashSet<Registration> checkAttributes(Set<Registration> listOfServices, Part[] searchedAttributes, boolean isStrict) {
		HashSet<Registration> matchingServices = new HashSet<Registration>();
		for(Registration serviceInfo : listOfServices) {
			//check if all searched keys are available
			Part[] attrs = serviceInfo.getAttributes();
			boolean foundAllKeys = true;
			boolean attrsMatched = true;
			//used to say that at least one of the attribute keys was found
			boolean atLeastOneMatch = false;
			for(Part searchedAttr : searchedAttributes) {
				boolean foundKey = false;
				for(Part attr : attrs) {
					if(searchedAttr.getKey().equals(attr.getKey())) {
						atLeastOneMatch = true;
						if(!searchedAttr.getValue().equals(attr.getValue())) {
							attrsMatched = false;
							break;//loop checking key
						}
						foundKey = true;
						break;//loop checking key
					}
				}
				//did not find key or exited because found key
				if((!foundKey && isStrict) || !attrsMatched) {
					foundAllKeys = false;
					break;//loop checking all keys
				}
			}
			//all searched attributes were found
			//or exited because key missed or value did not match
			if((foundAllKeys || !isStrict) && attrsMatched && atLeastOneMatch) {
				matchingServices.add(serviceInfo);
			}
		}
		return matchingServices;
	}

	@Override
	public Set<Registration> getServicesByDescription(String description) {

		String[] toMatch;
		boolean exactMatch = false;

		if (description.contains("*")) { // the match means several strings to
			// match, separated by *
			toMatch = description.split("\\*");
			exactMatch = false;
		} else {
			toMatch = new String[1];
			toMatch[0] = description;
			exactMatch = true;
		}

		/**
		 * algorithm: assume all Registration entries in our table will match then,
		 * for every match string from the query (i.e. if query = ab*cd*ef then
		 * match strings are ab, cd, and ef verify all descriptions for which we
		 * still assume they will match if they indeed match, they survive to
		 * next match string verification if they do not match, then at least
		 * one criterion of the matchstring is not satisfied, hence that Registration
		 * entry will never be in the final set, so we can just remove it from
		 * our set under consideration this should be an optimization to the
		 * (each criterion x each Registration entry) approach
		 */

		Collection<Registration> allDescriptions = new HashSet<Registration>();
		allDescriptions.addAll(localServices.values());
		allDescriptions.addAll(remoteServices.values()); // because we are searching
		// in ALL Services, local
		// and remote

		String oneDescription;
		for (int i = 0; i < toMatch.length; i++) { // when having an exact
			// Match, length=1, so it
			// will only be executed
			// once, which is the
			// desired behavior
			for (Iterator<Registration> it = allDescriptions.iterator(); it
					.hasNext();) {
				Registration serviceInfo = it.next();
				try {
					oneDescription = serviceInfo.getDescription();
					if (oneDescription != null 
							&& ((!exactMatch && oneDescription.contains(toMatch[i]))
									|| (exactMatch && oneDescription.equals(toMatch[i])))) {
						// the
						// match
						// criteria
						// is
						// satisfied
						// just pass to next round, this Registration entry survives
						continue;
					} else {
						//TODO #NM Mark check
						// this Registration is already a candidate to be thrown out
						// of further consideration
						// but let's do a last check on CryptoHID just in case
						// it matches
						if (serviceInfo.getAttributes() != null) {
							oneDescription = serviceInfo.getDescription();
							if (oneDescription != null) {
								if ((!exactMatch && oneDescription
										.contains(toMatch[i]))
										|| (exactMatch && oneDescription
												.equals(toMatch[i]))) { // finally
									// we
									// have
									// a
									// match
									// this Registration entry is saved to the next
									// round
									continue;
								}
							}
						}
						// this is like a common ELSE block for all above if-s
						// the Registration entry has not survived, has not matched
						// at least one of the query string match criteria
						// so there's no need to check it in further iterations
						// against other match criteria
						// so let's just remove it now
						it.remove();
					}
				} catch (Exception e) {
					LOG.error("Unable to get VirtualAddress for description: "
							+ description, e);
				}

			}
		}
		return new HashSet<Registration>(allDescriptions);
	}

	@Override
	public Registration[] getServicesByAttributes(String query) {
		LinkedList<String> parsedQuery = AttributeQueryParser.parseQuery(query);
		/* Parse the query. */
		HashSet<Registration> results = new HashSet<Registration>();


		HashSet<Map.Entry<VirtualAddress, Registration>> allVirtualAddresses = new HashSet<Map.Entry<VirtualAddress,Registration>>();
		//search in local and remote Services
		allVirtualAddresses.addAll(localServices.entrySet());
		allVirtualAddresses.addAll(remoteServices.entrySet());
		Iterator<Map.Entry<VirtualAddress, Registration>> it = allVirtualAddresses.iterator();

		while (it.hasNext()) {
			Map.Entry<VirtualAddress, Registration> entry = it.next();
			Part[] attr = entry.getValue().getAttributes();
			if (attr != null) {
				if (AttributeQueryParser.checkAttributes(attr, parsedQuery)) {
					results.add(entry.getValue());
				}
			}
		}

		Registration[] serviceInfos = new Registration[results.size()];
		results.toArray(serviceInfos);
		return serviceInfos;
	}

	@Override
	public boolean updateServiceInfo(VirtualAddress virtualAddress, Properties attr) {
		Registration toUpdate = localServices.get(virtualAddress);
		if (toUpdate != null) {
			synchronized (queue) { // because we need to be sure that both
				// deletion of old and insertion of new
				// attributes are in the queue at the same
				// time
				toUpdate.setAttributes(PartConverter.fromProperties(attr));
				localServices.replace(virtualAddress, toUpdate);
				//careful, D always before A, because the NMs that listen to this will 
				//execute the actions in order, hence if A is after D, they will first update and then delete the just-entered VirtualAddress.
				queue.add("D;" + virtualAddress.toString());
				queue.add("A;" + virtualAddress.toString() + ";"
						+ toUpdate.getDescription());
			}
			return true;
		} else {
			return false;
		}
	}

	// keep the following two methods near each other
	// because they refer to the same format
	/**
	 * Looks for updates in the list of Services and transforms them into a
	 * {@link BroadcastMessage}
	 * 
	 * @return the update
	 */
	protected synchronized BroadcastMessage getServiceListUpdate() {
		String update = "";
		while (queue.peek() != null) {
			update = update + queue.poll() + " ";
		}
		if (update.equals("")) {
			update = " ";
		}
		BroadcastMessage updateMsg = null;
		try {
			updateMsg = new BroadcastMessage(
					IDMANAGER_UPDATE_SERVICE_LIST_TOPIC, networkManagerCore.getService(),
					update.getBytes());
		} catch (RemoteException e) {
			// local invocation
		}
		return updateMsg;
	}

	@Override
	public Message processMessage(Message msg) {
		try {
			if(!msg.getSenderVirtualAddress().equals(networkManagerCore.getService())) {
				if (msg.getTopic().contentEquals(IDMANAGER_UPDATE_SERVICE_LIST_TOPIC)) {
					return processNMUpdate(msg);
				} else if (msg.getTopic().contentEquals(IDMANAGER_NMADVERTISMENT_TOPIC)) {
					return processNMAdvertisement(msg);
				} else if (msg.getTopic().contentEquals(IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_REQ)) {
					return processServiceAttributeResolveReq(msg);
				} else if (msg.getTopic().contentEquals(IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_RESP)) {
					return processServiceAttributeResolveResp(msg);
				}else { // other message types should not have been passed
					return msg;
				}
			} else {
				return null;
			}
		} catch (RemoteException e) {
			return null; //local call and does not occur
		}
	}

	@Override
	public boolean removeService(VirtualAddress virtualAddress) {
		if (removeLocalService(virtualAddress) != null || removeRemoteService(virtualAddress) != null) {
			LOG.debug("Removed VirtualAddress: " + virtualAddress.toString());
			return true;
		}
		return false;
	}

	protected Message processServiceAttributeResolveResp(Message msg) {
		//check if there is resolve waiting with this id
		String reqId = msg.getProperty(SERVICE_ATTR_RESOLVE_ID);
		//does anyone wait for this resolve id
		if(locks.containsKey(reqId)) {
			Object lock = locks.get(reqId);
			//put message into map for waiting thread to access
			if(resolveResponses.containsKey(reqId)) {
				resolveResponses.get(reqId).add(msg);
			} else {
				List<Message> msgs = new ArrayList<Message>();
				msgs.add(msg);
				resolveResponses.put(reqId, msgs);
			}
			//inform waiting thread after each incoming message
			synchronized(lock){
				lock.notify();
			}
		}
		//the message was either not relevant or has been processed
		return null;
	}

	/**
	 * Checks if a filter object matches the
	 * provided attributes. Strictness and random
	 * are automatically handled.
	 * @param filter
	 * @param attributes
	 * @return
	 */
	protected boolean checkAttributesAgainstFilter(
			AttributeResolveFilter filter,
			Part[] attributes) {
		//check if all attributes are available which are
		//in the attribute key string
		if(filter.getIsStrictRequest()) {
			String[] keys = filter.getAttributeKeys().split(";");
			for(String key : keys) {
				boolean found = false;
				for(Part part : attributes) {
					//exit loop if there is a match
					if(part.getKey().equals(key)) {
						found = true;
						break;
					}
				}
				//if key not found there is no strict match
				if(!found) {
					return false;
				}
			}
			//we get here if there was no key which was not found
		}
		/*go through the attributes*/

		//used to say that at least one of the searched keys was available
		boolean atLeastOneMatch = false;
		for(Part part : attributes) {
			//if one of the attributes does not match return false
			if(filter.getAttributeKeys().contains(part.getKey())) {
				atLeastOneMatch = true;
				if(!BloomFilterFactory.
						containsValue(
								part.getValue(),
								filter.getBloomFilter(),
								filter.getRandom())) {
					return false;
				}
			}
		}
		if(atLeastOneMatch) {
			return true;
		} else {
			return false;
		}
	}

	protected Message processServiceAttributeResolveReq(Message msg) {
		//open message
		//store request id
		long reqId = Long.parseLong(msg.getProperty(SERVICE_ATTR_RESOLVE_ID));
		ByteArrayInputStream bis = new ByteArrayInputStream(msg.getData());
		ObjectInputStream ois;
		AttributeResolveFilter filter = null;
		try {
			ois = new ObjectInputStream(bis);
			filter = (AttributeResolveFilter)ois.readObject();
		} catch (IOException e) {
			LOG.warn("Cannot open attribute resolve message!",e);
			return null;
		} catch (ClassNotFoundException e) {
			//cannot occur
			return null;
		}
		//check if requester has the rights to access service
		//TODO
		//check if attributes can be found in local store
		Set<Registration> localServices = getLocalServices();
		Set<Registration> matches = new HashSet<Registration>();
		//go through all local Services
		for(Registration serviceInfo : localServices) {
			if(checkAttributesAgainstFilter(
					filter,
					serviceInfo.getAttributes())) {
				matches.add(serviceInfo);
			}
		}
		//if there are matches respond
		if(matches.size() != 0) {
			//return message confirming VirtualAddress
			Random rand = new Random();
			long respRandom = rand.nextLong();
			List<AttributeResolveResponse> matchesFilterList = 
					new ArrayList<AttributeResolveResponse>();
			//create Bloom-filters for all matches
			for(Registration serviceInfo : matches) {
				//collect queried attributes
				String attrKeys = null;
				List<String> attributes = new ArrayList<String>();
				StringBuilder sb = new StringBuilder();
				for(Part part : serviceInfo.getAttributes()) {
					//only include attributes which were searched
					if(filter.getAttributeKeys().contains(part.getKey())) {
						sb.append(part.getKey()+";");
						attributes.add(part.getValue());
					}
				}
				attrKeys = sb.toString();
				//remove last ';' separator
				attrKeys = attrKeys.substring(0, attrKeys.length()-1);
				//create bloom filter with new random
				String[] values = new String[attributes.size()];
				boolean[] bloom = 
						BloomFilterFactory.createBloomFilter(attributes.toArray(values), respRandom);
				AttributeResolveFilter match = 
						new AttributeResolveFilter(bloom, attrKeys, respRandom, filter.getIsStrictRequest());
				//put filter of match into collection of matches
				matchesFilterList.add(new AttributeResolveResponse(serviceInfo.getVirtualAddress(), match));
			}


			//create message from collected matches
			byte[] serializedResp = null;
			try{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(matchesFilterList);
				serializedResp = bos.toByteArray();
			} catch(IOException e) {
				LOG.warn("Cannot create response to attribute resolve.",e);
				return null;
			}

			try {
				Message respMsg = new Message(
						IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_RESP,
						networkManagerCore.getService(),
						msg.getSenderVirtualAddress(),
						serializedResp);
				//put in request id so requester knows this is response
				respMsg.setProperty(SERVICE_ATTR_RESOLVE_ID, String.valueOf(reqId));
				return respMsg;
			} catch(RemoteException e) {
				//local call
			}
		}
		return null;
	}

	protected Set<Registration> sendResolveAttributesMsg(
			Part[] attributes,
			long timeout,
			boolean returnFirst,
			boolean isStrictRequest) {
		Message msg = composeResolveAttributesMsg(attributes, isStrictRequest);
		//create message identifier
		Random rand = new Random();
		String attrReqId = String.valueOf(rand.nextLong());
		msg.setProperty(SERVICE_ATTR_RESOLVE_ID, attrReqId);
		Object lock = new Object();
		locks.put(attrReqId, lock);
		boolean found = false;
		synchronized(lock) {
			try {
				networkManagerCore.broadcastMessage(msg);
				//send message and wait for responses to arrive within timeout
				Long startTime = Calendar.getInstance().getTimeInMillis();
				//if returnFirst then stop waiting when first answer arrives
				//always stop on timeout
				while((!found && returnFirst && (startTime + timeout > Calendar.getInstance().getTimeInMillis())) 
						|| (startTime + timeout > Calendar.getInstance().getTimeInMillis())) {
					lock.wait(timeout);
					if(resolveResponses.containsKey(attrReqId)) {
						found = true;
					}
				}
			} catch (InterruptedException e) {
				LOG.warn("Interrupted thread waiting for attribute resolve!",e);
				return null;
			}
		}
		//check if resolve response is available
		if(resolveResponses.containsKey(attrReqId)) {
			List<Message> resps = resolveResponses.remove(attrReqId);
			//go over each found message
			Set<Registration> foundServices = new HashSet<Registration>();
			Map<Registration, VirtualAddress> owners = new HashMap<Registration, VirtualAddress>();
			for(Message resp : resps) {
				//open message
				List<AttributeResolveResponse> filterResponses = null;
				try{
					ByteArrayInputStream bis = new ByteArrayInputStream(resp.getData());
					ObjectInputStream ois = new ObjectInputStream(bis);
					filterResponses = 
							(List<AttributeResolveResponse>) ois.readObject();
				} catch (IOException e) {
					LOG.warn("Could not read attribute resolve response.",e);
					return null;
				} catch (ClassNotFoundException e) {
					LOG.warn(
							"Attribute resolve response contained wrong object from VirtualAddress:"
									+ msg.getSenderVirtualAddress(),e);
				}

				//check all Services in message which possibly match
				for(AttributeResolveResponse response : filterResponses) {
					AttributeResolveFilter filter = response.getFilter();
					//if it was a strict request check if all searched keys are present
					if(isStrictRequest) {
						//check if all attributes are present in key string
						boolean allPresent = true;
						for(Part part : attributes) {
							//if it is not present skip this filter
							if(!filter.getAttributeKeys().contains(part.getKey())) {
								allPresent = false;
								break; //inner loop checking attributes
							}
						}
						if(!allPresent) {
							break; //outer loop checking filters
						}
					}
					//double check bloom filter
					if(checkAttributesAgainstFilter(filter, attributes)) {
						//only include attributes in Registration which matched
						Part[] foundAttr = 
								new Part[filter.getAttributeKeys().split(";").length];
						int nrAttrs = 0;
						for(Part part : attributes) {
							if(filter.getAttributeKeys().contains(part.getKey())) {
								foundAttr[nrAttrs] = part;
								nrAttrs++;
							}
						}
						Registration reg = new Registration(response.getService(), foundAttr);
						foundServices.add(reg);
						owners.put(reg, msg.getSenderVirtualAddress());
					}
				}
			}
			//put found Services in remoteService store
			for(Registration serviceInfo : foundServices) {
				//descriptions are usually available before but may not
				//have been queried - so include them
				if(serviceInfo.getDescription() == null) {
					//check if description was available before
					Registration serviceInfoOld = remoteServices.get(serviceInfo.getVirtualAddress());
					if(serviceInfoOld != null) {
						String description = serviceInfoOld.getDescription();
						if(description != null) {
							serviceInfo.setDescription(description);
						}
					}
				}
				addRemoteService(serviceInfo.getVirtualAddress(), serviceInfo, owners.get(serviceInfo));
			}
			//return found services
			return foundServices;
		} else {
			//timeout occurred so return null
			return null;
		}
	}

	protected Message composeResolveAttributesMsg(Part[] attributes, boolean isStrictRequest) {
		//create string of attributes searched
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for(Part part : attributes) {
			i++;
			sb.append(part.getKey());
			if(i != attributes.length) {
				sb.append(";");
			}
		}
		String attrKeys = sb.toString();

		//create bloom-filter from attributes
		String[] attrValues = new String[attributes.length];
		for (int j=0; j< attributes.length; j++) {
			attrValues[j] = attributes[j].getValue();
		}
		Random rand = new Random();
		long random = rand.nextLong();
		boolean[] bloomFilter = BloomFilterFactory.createBloomFilter(attrValues, random);

		//compose the message
		AttributeResolveFilter attrRF = 
				new AttributeResolveFilter(bloomFilter, attrKeys, random, isStrictRequest);

		//serialize query
		byte[] serializedMsg = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(attrRF);
			serializedMsg = bos.toByteArray();
		} catch (IOException e1) {
			LOG.error("Cannot create attribute query object!",e1);
			return null;
		}
		//compose message
		BroadcastMessage msg = null;
		try {
			msg = new BroadcastMessage(
					IDMANAGER_SERVICE_ATTRIBUTE_RESOLVE_REQ,
					networkManagerCore.getService(),
					serializedMsg);
		} catch (RemoteException e) {
			//local access
		}
		return msg;
	}

	@SuppressWarnings("unchecked")
	protected Message processNMAdvertisement(Message msg) {
		try {
			if (!msg.getSenderVirtualAddress().equals(networkManagerCore.getService())) {
				Set<Registration> serviceInfos = (Set<Registration>) ByteArrayCodec.decodeByteArrayToObject(msg.getData());

				if (serviceInfos != null) {
					Iterator<Registration> i = serviceInfos.iterator();
					while (i.hasNext()) {
						Registration oneServiceInfo = i.next();
						addRemoteService(oneServiceInfo.getVirtualAddress(), oneServiceInfo, msg.getSenderVirtualAddress());
					}
				}
			}
		} catch (RemoteException e) {
			LOG.debug("Remote Exception " + e);
		} catch (IOException e) {
			LOG.debug("IO Exception in communication, message maybe damaged? " + e);
		} catch (ClassNotFoundException e) {
			LOG.error("Class not found in reconstructing message. Why? " + e);
		}
		//message is processed
		return null;
	}

	protected Message processNMUpdate(Message msg) {
		try {
			if (!msg.getSenderVirtualAddress().equals(networkManagerCore.getService())) {
				// this is not an echo of our own broadcast
				// otherwise we do not need to do anything with it
				// else it is a genuine update
				String updates = new String(msg.getData()); 
				for (String oneUpdate : updates.split(" ")) {
					String[] updateData = oneUpdate.split(";");
					// at this point updateData 0 is operation type A/D, [1] is
					// Service, [2] is description (only if operation=A)
					if (updateData[0].equals("A")) {
						VirtualAddress newVirtualAddress = new VirtualAddress(updateData[1]);
						Registration newInfo = new Registration(newVirtualAddress, updateData[2]);
						// Add the remoteService to the internal map of remote Services
						addRemoteService(newVirtualAddress, newInfo, msg.getSenderVirtualAddress());
					} else if (updateData[0].equals("D")) {
						VirtualAddress toRemoveVirtualAddress = new VirtualAddress(updateData[1]);
						removeRemoteService(toRemoveVirtualAddress);
					} else {
						throw new IllegalArgumentException(
								"Unexpected update type for IDManager updates: " + updateData[0]);
					}
				}
			}
		} catch (RemoteException e) {
			// local invocation
		}
		//message is processed
		return null;
	}

	/*
	 * Adds a local VirtualAddress to the IdTable
	 * 
	 * @param virtualAddress The VirtualAddress to be added
	 * 
	 * @param info the Registration
	 * 
	 * @return The previous value associated with that VirtualAddress, null otherwise
	 */
	protected Registration addLocalService(VirtualAddress virtualAddress, Registration info) {
		if (!localServices.containsKey(virtualAddress)) {
			localServices.put(virtualAddress, info);
			queue.add("A;" + virtualAddress.toString() + ";" + info.getDescription());
		}
		return localServices.get(virtualAddress);
	}

	/**
	 * Adds a remote VirtualAddress to the IdTable and updates 
	 * the time stamp of last update
	 * 
	 * @param virtualAddress The VirtualAddress to be added
	 * 
	 * @param info the Registration
	 * 
	 * @return The previous value associated with that VirtualAddress, null otherwise
	 */
	protected Registration addRemoteService(VirtualAddress virtualAddress, Registration info, VirtualAddress owner) {
		//timestamp always has to be updated
		serviceLastUpdate.put(virtualAddress, Calendar.getInstance().getTimeInMillis());
		//only update information if it is not equal to last value
		Registration prev = null;
		Registration heldRegistration = remoteServices.get(virtualAddress);
		if (shouldUpdate(heldRegistration, info)) {
			prev = remoteServices.put(virtualAddress, info);
			if(owner != null) {
				// Add the backbone route for this remote VirtualAddress
				networkManagerCore.addRemoteVirtualAddress(owner, virtualAddress);
			}
		}
		return prev;
	}

	/**
	 * Checks whether two Registrations represent the same entity and whether the
	 * current Registration holds more information
	 * @return true if services are absolutely different or current holds more information
	 * e.g. more attributes
	 */
	protected boolean shouldUpdate(Registration previous, Registration current) {
		if(previous == null) return true;
		if(previous.equals(current)) return false;
		for (Part currentAttr : current.getAttributes()) {
			//get matching attribute from previous Registration
			boolean missing = true;
			for (Part prevAttr : previous.getAttributes()) {
				if(prevAttr.getKey().equals(currentAttr.getKey())) {
					if(!prevAttr.getValue().equals(currentAttr.getValue())) {
						//an attribute has changed so update
						return true;
					} else {
						//this attribute matches so move on to next attribute
						missing = false;
						break;
					}
				}
			}
			if(missing) {
				//there is an attribute in current which was not there in previous
				return true;
			}
		}
		return false;
	}

	/*
	 * Removes a local VirtualAddress from the IdTable
	 * 
	 * @param virtualAddress The virtual address of the relevant service to be removed
	 */
	protected Registration removeLocalService(VirtualAddress virtualAddress) {
		if (localServices.containsKey(virtualAddress)) {
			queue.add("D;" + virtualAddress.toString());
		}
		return localServices.remove(virtualAddress);
	}

	/*
	 * Removes a remote VirtualAddress from the IdTable
	 * 
	 * @param virtualAddress The VirtualAddress to be removed
	 * 
	 * @return the result, null if
	 */
	protected Registration removeRemoteService(VirtualAddress virtualAddress) {
		serviceLastUpdate.remove(virtualAddress);
		Registration removal = remoteServices.remove(virtualAddress);

		//if this check were not here this would result an infinite loop 
		if(removal != null) {
			try {
				networkManagerCore.removeService(removal.getVirtualAddress());
			} catch (RemoteException e) {
				//local invocation
			}
		}
		return removal;
	}

	/*
	 * Checks inside the idTable if the deviceID has already been assigned
	 * 
	 * @param deviceID The deviceID to be checked
	 * 
	 * @return Returns true if deviceID has already been assigned. False
	 * otherwise.
	 */
	protected boolean existsDeviceID(long deviceID) {
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

	/*
	 * Thread sends broadcast message if there is an update in ServiceList
	 */
	protected class ServiceUpdaterThread implements Runnable {

		@Override
		public void run() {
			while (serviceUpdaterThreadRunning) {
				if (!queue.isEmpty()) {
					BroadcastMessage m = getServiceListUpdate();
					LOG.debug("Broadcasting Message: " + m);
					networkManagerCore.broadcastMessage(m);
				}
				try {
					Thread.sleep(broadcastSleepMillis);
				} catch (InterruptedException e) {
					LOG.info("Thread broadcasting updates stopped!", e);
					serviceUpdaterThreadRunning = false;
					break;
				}
			}
		}

	}

	/*
	 * Thread that broadcasts all localServices stored by this IdentityManager 
	 */
	protected class AdvertisingThread implements Runnable {
		@Override
		public void run() {
			while (advertisingThreadRunning) {
				if (networkManagerCore != null) {

					//#NM refactoring put list of local Services into message
					Set<Registration> localServices = getLocalServices();
					//only keep Service and description in sent data
					Set<Registration> servicesToSend = new HashSet<Registration>();
					for(Registration serviceInfo : localServices) {
						if(serviceInfo.getDescription() != null) {
							servicesToSend.add(
									new Registration(
											serviceInfo.getVirtualAddress(),
											new Part[]{new Part(
													ServiceAttribute.DESCRIPTION.name(),
													serviceInfo.getDescription())}));
						}
					}
					byte[] localServiceBytes = null;

					try {
						localServiceBytes = ByteArrayCodec.encodeObjectToBytes(servicesToSend);
					} catch (IOException e) {
						LOG.error("Cannot convert local Services set to bytearray; " + e);

					}
					BroadcastMessage m = null;

					try {
						m = new BroadcastMessage(
								IDMANAGER_NMADVERTISMENT_TOPIC, 
								networkManagerCore.getService(), localServiceBytes);
					} catch (RemoteException e) {
						// local invocation
						LOG.debug("RemoteException: " + e);
					}
					networkManagerCore.broadcastMessage(m);
				}
				try {
					Thread.sleep(advertisementSleepMillis);
				} catch (InterruptedException e) {
					advertisingThreadRunning = false;
					LOG.error("Thread advertising NetworkManager stopped!",e);
					break;
				}
			}
		}

	}

	public String getIdentifier() {
		return IDENTITY_MGR;
	}

	protected class ServiceClearer implements Runnable {
		public void run() {
			try {
				while(serviceClearerThreadRunning) {
					Thread.sleep(advertisementSleepMillis);

					List<VirtualAddress> toDelete = new ArrayList<VirtualAddress>();
					//check the Services to be deleted
					for(VirtualAddress virtualAddress : serviceLastUpdate.keySet()) {
						if(serviceLastUpdate.get(virtualAddress) + SERVICE_KEEP_ALIVE_MS <
								Calendar.getInstance().getTimeInMillis()) {
							toDelete.add(virtualAddress);
						}
					}
					//delete the Services from the local id table and last update
					for(VirtualAddress virtualAddress : toDelete) {
						if(networkManagerCore != null) {
							try {
								LOG.debug("Removing VirtualAddress " + virtualAddress.toString() + 
										"as it was not updated recently.");
								networkManagerCore.removeService(virtualAddress);
							} catch(RemoteException e) {
								//local access
							}
						}
					}
				}
			} catch(InterruptedException e) {
				LOG.error("Thread removing not advertised Services stopped!", e);
				serviceClearerThreadRunning = false;
			}
		}
	}
}
