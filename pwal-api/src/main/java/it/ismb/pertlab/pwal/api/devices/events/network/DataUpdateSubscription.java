/*
 * PWAL -Network-level Data Publisher
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.pwal.api.devices.events.network;

/**
 * A class representing a device subscription to low-level network events. It
 * specifies the device responsible for the subscription, the desired event
 * delivery time, the low-level identifier of the device. Last subscription
 * activation is tracked and can be queried to check timing compliance.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class DataUpdateSubscription
{
	// the required delivery time in milliseconds
	private int deliveryTimeMillis;
	
	// the subscriber which requested this subscription
	private DataUpdateSubscriber subscriber;
	
	// the low-level identifier for the subscriber to handle data delivery
	private String lUID;
	
	// the last updated instant (in system time) used to check if data for this
	// subscription must be delivered or not
	// TODO: improve this using a calendar object?
	// TODO: check if it is better to handle updates outside to keep the model
	// cleaner
	private long timestamp;
	
	/**
	 * Builds a new DataUpdateSubscription, which represents a subscription
	 * request from a given {@link DataUpdateSubscriber} wishing to receive data
	 * updates every x specified milliseconds.
	 * 
	 * @param deliveryTimeMillis
	 *            The required delivery time, in milliseconds.
	 * @param subscriber
	 *            The subscriber object.
	 */
	public DataUpdateSubscription(int deliveryTimeMillis, DataUpdateSubscriber subscriber, String lUID)
	{
		super();
		this.deliveryTimeMillis = deliveryTimeMillis;
		this.subscriber = subscriber;
		this.lUID = lUID;
		this.timestamp = 0;
	}
	
	/**
	 * @return the deliveryTimeMillis
	 */
	public int getDeliveryTimeMillis()
	{
		return deliveryTimeMillis;
	}
	
	/**
	 * @param deliveryTimeMillis
	 *            the deliveryTimeMillis to set
	 */
	public void setDeliveryTimeMillis(int deliveryTimeMillis)
	{
		this.deliveryTimeMillis = deliveryTimeMillis;
	}
	
	/**
	 * @return the subscriber
	 */
	public DataUpdateSubscriber getSubscriber()
	{
		return subscriber;
	}
	
	/**
	 * @param subscriber
	 *            the subscriber to set
	 */
	public void setSubscriber(DataUpdateSubscriber subscriber)
	{
		this.subscriber = subscriber;
	}
	
	/**
	 * @return the lUID
	 */
	public String getlUID()
	{
		return lUID;
	}
	
	/**
	 * @param lUID
	 *            the lUID to set
	 */
	public void setlUID(String lUID)
	{
		this.lUID = lUID;
	}
	
	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		// the equality checking result
		boolean equals = false;
		
		// compare the subscribers (only one subscription per subscriber is
		// allowed)
		if (obj instanceof DataUpdateSubscriber)
			equals = ((DataUpdateSubscriber) obj).equals(this.subscriber);
		else
			equals = super.equals(obj);
		
		return equals;
	}
	
}
