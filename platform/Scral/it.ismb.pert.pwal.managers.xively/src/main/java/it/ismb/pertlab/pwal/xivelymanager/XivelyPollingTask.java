package it.ismb.pertlab.pwal.xivelymanager;

import it.ismb.pertlab.pwal.api.devices.interfaces.Device;
import it.ismb.pertlab.pwal.api.devices.polling.DataUpdateSubscription;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

import com.xively.client.model.Datastream;

/**
 * The polling task handling actual polling for Xively sensors. It
 * takes care of querying the network-level end point and to deliver updates to
 * the subscribing devices, with the required timing properties.
 * 
 */
public class XivelyPollingTask implements Runnable {
	
	// the device manager running this polling task
	private XivelyManager manager;
	private Logger log;
	
	public XivelyPollingTask(XivelyManager manager, Logger log) {
		// store a reference to the device manager
		this.manager = manager;
		
		// store the reference to the logger
		this.log = log;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		log.debug("Polling " + this.manager.getActiveSubscriptionsSize() + "subscription...");
		
		// if there is at least one subscription
		if (this.manager.getActiveSubscriptionsSize() > 0 && !manager.isSearching()) {
			// ask for updated data
			Map<String, Datastream> measureUpdates = manager.getXivelyRestClient().getMeasures();
						
			// iterate over the list and dispatch updates
			for (String lUID : measureUpdates.keySet()) {
				Datastream stream = measureUpdates.get(lUID);
				
				// get all active subscriptions for the give lUID
				// debug
				log.debug("lUID: " + lUID);
				
				// dispatch the new measure if a subscription is registered for
				// the given lUID
				Set<DataUpdateSubscription<Datastream>> subscriptionBucket = this.manager.getSubscriptions(lUID);
				if (subscriptionBucket != null) {
					for (DataUpdateSubscription<Datastream> subscription : subscriptionBucket) {
						if (subscription != null) {
							long currentTime = System.currentTimeMillis();
							
							if (currentTime - subscription.getTimestamp() >= (subscription.getDeliveryTimeMillis())) {
								DateTime updatedAt = DateTime.now(DateTimeZone.UTC); 
								String expiresAt = updatedAt
										.plusMillis(
												subscription.getDeliveryTimeMillis())
												.toString();
								((Device) subscription.getSubscriber())
								.setUpdatedAt(updatedAt
										.toString());
								((Device) subscription.getSubscriber())
								.setExpiresAt(expiresAt);
								subscription.getSubscriber().handleUpdate(stream);
								log.debug("Updating device: {} type: {}.",((Device) subscription.getSubscriber()).getPwalId(),((Device) subscription.getSubscriber()).getType());
							}
						}
					}
				}
			}
		}
		
	}
}
