package it.ismb.pertlab.pwal.api.devices.interfaces;

import it.ismb.pertlab.pwal.api.devices.enums.DeviceManagerStatus;
import it.ismb.pertlab.pwal.api.devices.events.DeviceListener;
import it.ismb.pertlab.pwal.api.devices.events.network.DataUpdatePublisher;
import it.ismb.pertlab.pwal.api.devices.events.network.DataUpdateSubscription;
import it.ismb.pertlab.pwal.api.devices.events.network.MaximumCommonPollingTimeTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DevicesManager implements Runnable, DataUpdatePublisher
{
	
	protected List<DeviceListener> deviceListener = new LinkedList<>();
	protected Thread t;
	protected String id;
	protected static final Logger log = LoggerFactory.getLogger(DevicesManager.class);
	protected HashMap<String, Device> devicesDiscovered = new HashMap<>();
	protected DeviceManagerStatus status = DeviceManagerStatus.STOPPED;
	
	// the set of subscribers to low-level data updates, mainly for
	// polling-based technologies
	protected HashMap<String, Set<DataUpdateSubscription>> lowLevelDataSubscriptions;
	
	// the number of active subscriptions
	protected int nActiveSubscriptions;
	
	// the current polling time in milliseconds
	protected int pollingTimeMillis;
	
	// the default polling time in milliseconds
	protected int basePollingTimeMillis;
	
	// the required time tolerance
	protected int timeTolerancePercentage;
	
	// the minimum polling time in milliseconds
	protected int minimumPollingTimeMillis;
	
	/**
	 * Empty constructor to conform to the Bean instantiation pattern
	 */
	public DevicesManager()
	{
		// initialize the set of subscribers to low-level data updates
		this.lowLevelDataSubscriptions = new HashMap<String, Set<DataUpdateSubscription>>();
		
		// asks implementing classes to set the base polling time
		this.setBasePollingTimeMillis();
		
		// asks implementing classes to set the minimum allowed polling time
		this.setMinimumPollingTimeMillis();
		
		// asks implementing classes to set the minimum allowed polling time
		this.setTimeTolerancePercentage();
		
		// defaults to the base polling time milliseconds
		this.pollingTimeMillis = basePollingTimeMillis;
		
	}
	
	// force sub classes to set a base polling time in milliseconds
	protected abstract void setBasePollingTimeMillis();
	
	// force sub classes to set a base polling time in milliseconds
	protected abstract void setMinimumPollingTimeMillis();
	
	// force sub classes to set a base polling time in milliseconds
	protected abstract void setTimeTolerancePercentage();
	
	public DeviceManagerStatus getStatus()
	{
		return status;
	}
	
	public void setStatus(DeviceManagerStatus status)
	{
		this.status = status;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = this.generateId();
	}
	
	public void start()
	{
		switch (this.status)
		{
			case STARTED:
				log.info("Trying to start device manager {}, but it is already STARTED.", this.getId());
				break;
			case STOPPED:
				log.info("Starting device manager: {}", this.id);
				t = new Thread(this);
				this.status = DeviceManagerStatus.STARTED;
				t.start();
				break;
			default:
				log.error("Error! Trying to start device manager {}, but it is in an unknown state.");
				break;
		}
	}
	
	public void stop()
	{
		switch (this.status)
		{
			case STARTED:
				log.info("Stopping device manager: {}", this.id);
				this.status = DeviceManagerStatus.STOPPED;
				for (Device d : this.devicesDiscovered.values())
				{
					for (DeviceListener l : this.deviceListener)
					{
						l.notifyDeviceRemoved(d);
					}
				}
				this.devicesDiscovered.clear();
				t.interrupt();
				break;
			case STOPPED:
				log.info("Trying to stop device manager {}, but it is already STOPPED.", this.getId());
				;
				break;
			default:
				log.error("Error! Trying to stop device manager {}, but it is in an unknown state.");
				break;
		}
	}
	
	public void addDeviceListener(DeviceListener l)
	{
		deviceListener.add(l);
	}
	
	public void removeDeviceListener(DeviceListener l)
	{
		deviceListener.remove(l);
	}
	
	/***********************************************************
	 * 
	 * Handle Network Data Subscriptions
	 *
	 ***********************************************************/
	
	@Override
	public boolean addSubscription(DataUpdateSubscription subscription)
	{
		// the operation result as boolean
		boolean added = false;
		
		// synchronized write to the subscription set
		synchronized (this.lowLevelDataSubscriptions)
		{
			// debug
			log.info("Adding subscription for:" + subscription.getlUID() + " time: "
					+ subscription.getDeliveryTimeMillis());
			
			// check if the subscription lUID exists
			Set<DataUpdateSubscription> subscriptionBucket = this.lowLevelDataSubscriptions.get(subscription.getlUID());
			
			// if the bucket does not exists, create it
			if (subscriptionBucket == null)
			{
				// create the set of subscriptions associated to the lower id,
				// they can in fact be possibly more than one per low id.
				subscriptionBucket = new HashSet<DataUpdateSubscription>();
				
				// store the set
				this.lowLevelDataSubscriptions.put(subscription.getlUID(), subscriptionBucket);
			}
			
			// replace existing subscription for the same device
			
			// debug
			log.info("Valid subscription");
			
			// add the new subscription
			if (subscriptionBucket.contains(subscription))
			{
				// update the subscription
				// apparently this part seems crazy, but actually, as
				// subscriptions are considered equal if they refer to the same
				// device, without considering the desired delivery time, this
				// allows updating the delivery time quickly.
				subscriptionBucket.remove(subscription);
			}
			else
			{
				// increment the number of active subscriptions only if they
				// were not already registered.
				this.nActiveSubscriptions++;
			}
			
			// add the subscription
			subscriptionBucket.add(subscription);
			
			// debug
			log.info("More than one subscription");
			
			//re-compute the polling time
			this.computeMaximumCommonPollingTime();
			
			// successful addition
			added = true;
		}
		
		return added;
	}
	
	/**
	 * the maximum common polling time computation algorithm, to be implemented
	 * by extending classes. A common simplistic solution is provided by default
	 * and find the greatest common divisor.
	 */
	protected void computeMaximumCommonPollingTime()
	{
		// build the set of polling times
		int pollingTimesMillis[] = new int[this.nActiveSubscriptions];
		
		// extract the polling times
		int i = 0;
		for (Set<DataUpdateSubscription> subscriptions : this.lowLevelDataSubscriptions.values())
		{
			for (DataUpdateSubscription subscription : subscriptions)
			{
				// extract and store the desired polling time
				pollingTimesMillis[i] = subscription.getDeliveryTimeMillis();
				
				// increment the array index
				i++;
			}
		}
		
		// create the maximum common polling time computation task
		MaximumCommonPollingTimeTask mcpTask = new MaximumCommonPollingTimeTask(pollingTimesMillis);
		
		// actually the subsequent set of instructions perform a
		// synchronous call to the maximum polling time computation method, but
		// in a future like fashion.
		
		// prepare the task to run in a separate thread
		FutureTask<Integer> futureMCP = new FutureTask<Integer>(mcpTask);
		
		// run the task
		futureMCP.run();
		
		// wait the task end for setting up the base polling time
		try
		{
			int newPollingTime = (int) Math.round(futureMCP.get() * (this.timeTolerancePercentage / 100.0));
			log.info("Computed polling time:" + newPollingTime);
			if (newPollingTime != this.pollingTimeMillis)
			{
				// set the current polling time with a minimum value set by the
				// currently set minimum polling time
				this.pollingTimeMillis = Math.max(newPollingTime, this.minimumPollingTimeMillis);
				
				// debug
				log.info("Updating polling time to" + this.pollingTimeMillis + "and restarting the poller...");
				
				// updates the global polling time
				this.updatePollingTime();
			}
			
		}
		catch (InterruptedException | ExecutionException e)
		{
			DevicesManager.log
					.warn("Error while computing the maximum common polling time for devices, defaulting to the base value defined in the manager implementation",
							e);
			
			this.pollingTimeMillis = Math.max(
					(int) Math.round(this.basePollingTimeMillis * (this.timeTolerancePercentage / 100.0)),
					this.minimumPollingTimeMillis);
			
			// debug
			log.info("Updating polling time to" + this.pollingTimeMillis + "and restarting the poller...");
			this.updatePollingTime();
		}
	}
	
	protected abstract void updatePollingTime();
	
	@Override
	public boolean removeSubscription(DataUpdateSubscription subscription)
	{
		// the result flag
		boolean removed = false;
		
		// get the subscription bucket associated to te given lUID
		Set<DataUpdateSubscription> subscriptionBucket = this.lowLevelDataSubscriptions.get(subscription.getlUID());
		
		// remove the given subscription
		removed = subscriptionBucket.remove(subscription);
		
		if (removed)
		{
			// //decrease the number of active subscriptions
			this.nActiveSubscriptions--;
			
			// if the bucket is empty, remove also the bucket
			if (subscriptionBucket.isEmpty())
			{
				this.lowLevelDataSubscriptions.remove(subscription.getlUID());
			}
		}
		
		return removed;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.events.network.DataUpdatePublisher#
	 * listSubscriptions()
	 */
	@Override
	public Set<DataUpdateSubscription> listSubscriptions(String lUID)
	{
		// TODO Auto-generated method stub
		return this.lowLevelDataSubscriptions.get(lUID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.events.network.DataUpdatePublisher#
	 * getSubscription(java.lang.String)
	 */
	@Override
	public Set<DataUpdateSubscription> getSubscriptions(String lUID)
	{
		return this.lowLevelDataSubscriptions.get(lUID);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see it.ismb.pertlab.pwal.api.devices.events.network.DataUpdatePublisher#
	 * getActiveSubscriptionsSize()
	 */
	@Override
	public int getActiveSubscriptionsSize()
	{
		// TODO Auto-generated method stub
		return this.nActiveSubscriptions;
	}
	
	/***********************************************************/
	
	protected String generateId()
	{
		return UUID.randomUUID().toString();
	}
	
	// protected abstract void setNetworkType(DeviceNetworkType networkType);
	
	public abstract String getNetworkType();
}
