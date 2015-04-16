package eu.linksmart.gc.network.backbone.zmq;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import eu.linksmart.gc.api.network.Message;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.utils.Base64;

public class ZmqHandler {
	
	private static Logger LOG = Logger.getLogger(ZmqHandler.class.getName());

	private String peerID = null;
	private String xsubUri = "tcp://gando.fit.fraunhofer.de:7000";
	private String xpubUri = "tcp://gando.fit.fraunhofer.de:7001";
	
	private int HEART_BEAT_INTERVAL = 5000;
	
	private HeartBeat heartBeat = null;
	private ZmqReceiver receiver = null;
	private ZmqPublisher publisher = null;
	
	private BackboneZMQImpl zmqBackbone = null;
	
	private final Map<VirtualAddress, String> remoteServices = Collections.synchronizedMap(new HashMap<VirtualAddress, String>());
	
	private final Map<Integer, MessageSender> requestIdSenders = Collections.synchronizedMap(new HashMap<Integer, MessageSender>());
	
	private Integer counter = 0;
	
	private int MAX_RESPONSE_TIME = 60000;
	
	private ExecutorService executor = Executors.newCachedThreadPool();
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	
	public ZmqHandler(BackboneZMQImpl zmqBackbone) {
		this.zmqBackbone = zmqBackbone;
		this.peerID = UUID.randomUUID().toString();
	}
	
	public ZmqHandler(BackboneZMQImpl zmqBackbone, String peerID) {
		this.zmqBackbone = zmqBackbone;
		this.peerID = peerID;
	}
	
	public ZmqHandler(BackboneZMQImpl zmqBackbone, String xpubUri, String xsubUri) {
		this.zmqBackbone = zmqBackbone;
		this.peerID = UUID.randomUUID().toString();
		this.xpubUri = xpubUri;
		this.xsubUri = xsubUri;
	}
	
	public ZmqHandler(BackboneZMQImpl zmqBackbone, String peerID, String xpubUri, String xsubUri) {
		this.zmqBackbone = zmqBackbone;
		this.peerID = peerID;
		this.xpubUri = xpubUri;
		this.xsubUri = xsubUri;
	}
	
	public void start() {
		heartBeat = new HeartBeat(this);
		heartBeat.start();
		receiver = new ZmqReceiver(this);
		receiver.start();
		publisher = new ZmqPublisher(this);
		publisher.startPublisher();
		LOG.info("ZmqPeer [" + this.peerID + "] is started");
	}
	
	public void stop() {
		heartBeat.setIsRunning(false);
		receiver.stopReceiver();
		publisher.stopPublisher();
		LOG.info("ZmqPeer [" + this.peerID + "] is stopping");
		shutdownExecutor();
		LOG.info("ZmqPeer [" + this.peerID + "] is stopped");
	}
	
	private void shutdownExecutor() {
		try {
			executor.shutdown();
			// Wait a while for existing tasks to terminate
		    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
		    	executor.shutdownNow(); // Cancel currently executing tasks
		      // Wait a while for tasks to respond to being cancelled
		      if (!executor.awaitTermination(10, TimeUnit.SECONDS))
		    	  LOG.error("Zmq-Executor did not terminate");
		   }
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			executor.shutdownNow();
		    // Preserve interrupt status
		    Thread.currentThread().interrupt();
		}
	}
	
	public void notify(ZmqMessage zmqMessage) {
		//new MessageProcessor(zmqMessage).start();
		executor.execute(new MessageProcessor(zmqMessage));
	}
	
	public NMResponse broadcast(BackboneMessage bbMessage) {
		
		LOG.info("sending broadcast message from virtual address: " + bbMessage.getSenderVirtualAddress());
		
		publisher.publish(ZmqUtil.createBroadcastMessage(this.peerID, ZmqUtil.addSenderVADToPayload(bbMessage)));
		
		NMResponse response = new NMResponse();
		response.setStatus(NMResponse.STATUS_SUCCESS);
		response.setMessage("Broadcast successful");
		
		return response;
	}
	
	public NMResponse sendData(BackboneMessage bbMessage) {
		
		NMResponse response = new NMResponse();
		
		String receiverPeerID = this.remoteServices.get(bbMessage.getReceiverVirtualAddress());
		
		if(receiverPeerID == null) {
			LOG.error("unable to find PeerID for receiver virtual address: " + bbMessage.getReceiverVirtualAddress());
			response.setStatus(NMResponse.STATUS_ERROR);
			response.setMessage("unable to find PeerID for receiver virtual address: " + bbMessage.getReceiverVirtualAddress());
			return response;
		} 
		
		Integer requestID = nextRequestIdCounter();
		
		MessageSender messageSender = new MessageSender(requestID);
		
		//
		// store requestid to identify response
		//
		if(bbMessage.isSync()) {
			requestIdSenders.put(requestID, messageSender);
		}
		
		response = messageSender.send(bbMessage, receiverPeerID);

		//
		//block until response comes and then remove id
		//
		if(bbMessage.isSync()) {
			response = messageSender.waitForResponseFromPeer();
			requestIdSenders.remove(requestID);
		}
		
		return response;
	}
	
	private synchronized Integer nextRequestIdCounter() {
		counter = counter + 1;
		return counter;
	}
	
	public String getPeerID() {
		return this.peerID;
	}
	
	public String getXPubUri() {
		return this.xpubUri;
	}
	
	public String getXSubUri() {
		return this.xsubUri;
	}
	
	public int getHeartBeatInterval() {
		return this.HEART_BEAT_INTERVAL;
	}
	
	public void setHeartBeatInterval(int heartBeatTimer) {
		this.HEART_BEAT_INTERVAL = heartBeatTimer;
	}
	
	public Map<VirtualAddress, String> getRemoteServices() {
		return this.remoteServices;
	}

	public void addServiceIfMissing(VirtualAddress addr, String peerID) {
		try {
			lock.writeLock().lock();
			this.remoteServices.put(addr, peerID);
			LOG.info("added remote service VAD: " + addr + " to peer [" + peerID + "]");
		} finally {
			lock.writeLock().unlock();
		}
//		synchronized (this.remoteServices) {
//			if (!(this.remoteServices.containsKey(addr))) {
//				this.remoteServices.put(addr, peerID);
//				LOG.info("added remote service VAD: " + addr + " to peer [" + peerID + "]");		
//			}
//		}
	}

	public void removePeerServices(String peerID) {
		LOG.info("removing zmqPeer [" + peerID + "] and associated remote services");
		Iterator<String> services = null;
		try {
			lock.readLock().lock();
			services = this.remoteServices.values().iterator();
			LOG.info("removing remote service for peer [" + peerID + "] from list");
		} finally {
			lock.readLock().unlock();
		}
		while(services.hasNext()) {
			String value = services.next();
			if(value.equals(peerID)) {
				try {
					lock.writeLock().lock();
					services.remove();
					LOG.info("removing remote service for peer [" + peerID + "] from list");
				} finally {
					lock.writeLock().unlock();
				}
			}
		}
//		synchronized (this.remoteServices) {
//			LOG.info("removing zmqPeer [" + peerID + "] and associated remote services");
//			Iterator<String> services = this.remoteServices.values().iterator();
//			while(services.hasNext()) {
//				String value = services.next();
//				if(value.equals(peerID)) {
//					services.remove();
//					LOG.info("removing remote service for peer [" + peerID + "] from list");
//				}
//			}
//		}
	}
	
	public void removePeerService(VirtualAddress removeVirtualAddress) {
		try {
			lock.writeLock().lock();
			if(this.remoteServices.remove(removeVirtualAddress) != null) {
				LOG.info("removing remoteService [" + removeVirtualAddress + "] from catalog");
			}
		} finally {
			lock.writeLock().unlock();
		}
//		synchronized (this.remoteServices) {
//			if(this.remoteServices.remove(removeVirtualAddress) != null) {
//				LOG.info("removing remoteService [" + removeVirtualAddress + "] from catalog");
//			}
//		}
	}
	
	////////////////////////////////////////////////////////

	public class MessageProcessor extends Thread {

		private ZmqMessage zmqMessage = null;

		public MessageProcessor(ZmqMessage zmqMessage) {
			this.zmqMessage = zmqMessage;
		}

		public void run() {
			if(zmqMessage.getTopic().equals(ZmqConstants.BROADCAST_TOPIC)) {
				if(zmqMessage.getType() == ZmqConstants.MESSAGE_TYPE_SERVICE_DISCOVERY) {
					processDiscovery();
				} else if(zmqMessage.getType() == ZmqConstants.MESSAGE_TYPE_PEER_DOWN) {
					processPeerDown();
				}
			} else if(zmqMessage.getTopic().equals(getPeerID())) {
				processUnicast();
			} else {
	            LOG.warn("unknown topic [" + zmqMessage.getTopic() + "] detected. ignoring");
	        }
		}

		private void processDiscovery() {
			// Ignore own service broadcasts
			if(zmqMessage.getSender().equals(getPeerID()))
				return;

			VirtualAddress senderVirtualAddress = ZmqUtil.getSenderVAD(zmqMessage.getPayload());
			LOG.info("received service broadcast from zmqPeer [" + zmqMessage.getSender() + "] - virtual address: " + senderVirtualAddress.toString());

			addServiceIfMissing(senderVirtualAddress, zmqMessage.getSender());
			// TODO check if new virtual address is received for already added peer <- ???
			// TODO currently we never remove a Virtual Address until a peer goes down (then we remove all of them)
			byte[] payload = ZmqUtil.removeSenderVAD(zmqMessage.getPayload());
			//
			// read remote services information from payload after deserializing it
			//
			processBroadcastPayload(payload, senderVirtualAddress); 
			zmqBackbone.receiveDataAsynch(senderVirtualAddress, null, payload);
		}

		@SuppressWarnings("unchecked")
		private void processBroadcastPayload(byte[] payload, VirtualAddress senderVirtualAddress) {
			// Deserialize the payload to get service registrations
			Properties properties = new Properties();
			try {
				properties.loadFromXML(new ByteArrayInputStream(payload));
			} catch (InvalidPropertiesFormatException e) {
				LOG.error("processBroadcast: unable to load properties from XML data. Data is not valid XML: " + new String(payload));
			} catch (IOException e) {
				LOG.error("processBroadcast: unable to load properties from XML data: " + new String(payload));
			}
			Message message = null;
			try {
				// create real message
				message = new Message((String) properties.remove("topic"), senderVirtualAddress, null, (Base64.decode((String) properties.remove("applicationData"))));
				// go through the properties and add them to the message
				boolean includeProps = true; 
				if(includeProps) {
					Iterator<Object> i = properties.keySet().iterator();
					while (i.hasNext()) {
						String key = (String) i.next();
						message.setProperty(key, properties.getProperty(key));
					}
				}
			} catch (Exception e) {
				LOG.error("processBroadcast: error in deserializing broadcast payload: " + e.getMessage());
			}
			if(message == null)
				return;
			if(message.getTopic().equals("NMAdvertisement")) {
				try {
					ByteArrayInputStream bis = new ByteArrayInputStream(message.getData());
					ObjectInput in = new ObjectInputStream(bis);
					Object payloadObject = in.readObject();
					bis.close();
					in.close();
					Set<Registration> serviceRegistrations = null;
					serviceRegistrations = (Set<Registration>) payloadObject;
					if (serviceRegistrations != null) {
						for (Registration serviceInfo : serviceRegistrations) {
							addServiceIfMissing(serviceInfo.getVirtualAddress(), zmqMessage.getSender());
						}
					}
				} catch (Exception e) {
					LOG.error("processBroadcast: error in decoding payload's registration[] object: " + e.getMessage());
				}		
			} else if(message.getTopic().equals("IDManagerServiceListUpdate")) {
				String updates = new String(message.getData()); 
				for (String oneUpdate : updates.split(" ")) {
					String[] updateData = oneUpdate.split(";");
					// at this point updateData 0 is operation type A/D, [1] is	Service, [2] is description (only if operation=A)
					if (updateData[0].equals("A")) {
						VirtualAddress newVirtualAddress = new VirtualAddress(updateData[1]);
						Registration newServiceInfo = new Registration(newVirtualAddress, updateData[2]);
						addServiceIfMissing(newServiceInfo.getVirtualAddress(), zmqMessage.getSender());
					} else if (updateData[0].equals("D")) {
						VirtualAddress removeVirtualAddress = new VirtualAddress(updateData[1]);
						removePeerService(removeVirtualAddress);
					} else {
						throw new IllegalArgumentException("Unexpected update type for IDManager updates: " + updateData[0]);
					}
				}
			} else {
				LOG.warn("processBroadcast: unable to parse topic: " + message.getTopic());
			}
		}

		private void processPeerDown() {
			LOG.info("received peerdown message for peer [" + zmqMessage.getSender() + "]");
			removePeerServices(zmqMessage.getSender());
		}

		private void processUnicast() {
			byte[] originalPayload = ZmqUtil.removeVADsFromPayload(zmqMessage.getPayload());
			VirtualAddress senderVA = ZmqUtil.getSenderVAD(zmqMessage.getPayload());
			VirtualAddress receiverVA = ZmqUtil.getReceiverVAD(zmqMessage.getPayload());
			String requestID = zmqMessage.getRequestID();

			LOG.debug("recieved message from [" + zmqMessage.getSender() + "] - virtualaddress: " + senderVA.toString() + " - request-ID : " + zmqMessage.getRequestID());

			if(zmqMessage.getType() == ZmqConstants.MESSAGE_TYPE_UNICAST_REQUEST) {

				/*
				 * MESSAGE request arrived. Call the zmqBackbone.receiveData(). Once it has received the status send it as a response
				 */
				LOG.info("REQ: " + " sender-VAD: " + senderVA.toString() + " - receiver-VAD: " + receiverVA.toString() + " - requestID: " + requestID);

				// add VAD of the sender to services
				addServiceIfMissing(senderVA, zmqMessage.getSender());

				NMResponse response = zmqBackbone.receiveDataSynch(senderVA, receiverVA, originalPayload);
				LOG.info("REQ: received response from nm/router for requestID: " + requestID);

				//
				// reverse source and destination because we (dest) send response back to source
				//
				BackboneMessage bbMessage = new BackboneMessage(receiverVA, senderVA, response.getMessageBytes(), true);

				String receiverPeerID = remoteServices.get(bbMessage.getReceiverVirtualAddress());

				if(receiverPeerID == null) {
					LOG.error("REQ: unable to find PeerID for receiver-VAD: " + bbMessage.getReceiverVirtualAddress());
					return;
				}

				if(bbMessage.isSync()) {
					if(response.getMessage() == null)
						response.setMessage("");
					//
					// sending response to incoming request
					//
					ZmqUtil.createResponseMessage(peerID, receiverPeerID, requestID, ZmqUtil.addVADsToPayload(bbMessage));
					boolean success = publisher.publish(ZmqUtil.createResponseMessage(peerID, receiverPeerID, requestID, ZmqUtil.addVADsToPayload(bbMessage)));

					if(!success) {
						LOG.error("REQ: Unable to send response to requestID: " + requestID + " from " + receiverVA.toString());
					}
				}

			} else if(zmqMessage.getType() == ZmqConstants.MESSAGE_TYPE_UNICAST_RESPONSE) {
				/*
				 * RESPONSE MESSAGE. NOTIFY the lock (in MessageSender.send(..)).
				 */
				LOG.info("RES: response message from peer [" + zmqMessage.getSender() + "] - sender-VAD: " + senderVA.toString() + " - requestID: " + Integer.valueOf(requestID));
				MessageSender sender = requestIdSenders.get(Integer.valueOf(requestID));
				if (sender != null) {
					sender.notification(originalPayload);
				}
			} else {
				LOG.error("Received incompatible zmq message with type: ");
			}
		}
	}

	/////////////////////////////////////////////////////////////////////
	
	public class MessageSender {
		
		private NMResponse resp = null;
		private final Integer requestID;
		private boolean responseReceived = false;

		public MessageSender(int requestID) {
			this.requestID = requestID;
			resp = new NMResponse();
		}

		public void notification(byte[] payload) {
			responseReceived = true;
			LOG.debug("received notification for requestID: " + requestID);
			resp.setBytesPrimary(true);
			resp.setMessageBytes(payload);
			synchronized (requestID) {
				requestID.notify();
			}
		}

		public NMResponse waitForResponseFromPeer() {
			synchronized (requestID) {
				try {
					LOG.info("waiting for response to receive for requestID: " + requestID);
					requestID.wait(MAX_RESPONSE_TIME);
				} catch (InterruptedException e) {
					LOG.error("request timeout for response from remote peer for requestID: " + requestID, e);
				}
			}
			if(!responseReceived) {
				resp.setStatus(NMResponse.STATUS_ERROR);
				resp.setMessage("request timeout for response from remote peer for requestID: " + requestID);
			}
			return resp;
		}

		public NMResponse send(BackboneMessage bbMessage, String receiverPeerID) {

			LOG.info("sending request-message to peer [" + receiverPeerID + "] - receiver-VAD: " + bbMessage.getReceiverVirtualAddress().toString() + " - requestID: " + requestID);
			
			//ZmqMessage zmqMessage = new ZmqMessage(receiverPeerID, ZmqConstants.MESSAGE_TYPE_UNICAST_REQUEST, System.currentTimeMillis(), peerID, requestID.toString(), ZmqUtil.addVADsToPayload(bbMessage));
			
			if(publisher.publish(ZmqUtil.createRequestMessage(peerID, receiverPeerID, requestID.toString(), ZmqUtil.addVADsToPayload(bbMessage)))) {
				resp.setStatus(NMResponse.STATUS_SUCCESS);
				resp.setMessage("<Response>Success sending data to VirtualAddress = " + bbMessage.getReceiverVirtualAddress().toString() + "</Response>");
				LOG.info("message send successfully for requestID: " + requestID);
			} else {
				resp.setStatus(NMResponse.STATUS_ERROR);
				resp.setMessage("<Response>Error in MessageSender</Response>");
				LOG.error("unable to send message for requestID: " + requestID);
			} 
			return resp;
		}

	}
}
