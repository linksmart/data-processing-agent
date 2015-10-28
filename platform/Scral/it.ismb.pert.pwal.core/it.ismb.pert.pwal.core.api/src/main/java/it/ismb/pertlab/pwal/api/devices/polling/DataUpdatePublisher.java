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
package it.ismb.pertlab.pwal.api.devices.polling;

import java.util.HashMap;
import java.util.Set;

/**
 * A data publisher interface enabling subscription to network-level events,
 * which are detected either through any native event-based communication or
 * through polling.
 * 
 * Subscriptions could specify a desired updated frequency that the implementing
 * class should try to stick to, no exact guarantee however is required.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public interface DataUpdatePublisher<T>
{
	/**
	 * Adds a subscription to network-level data updates for the Device instance
	 * identified by the given {@link DataUpdateSubscription} instance with the
	 * given preferred delivery time in milliseconds. In case the interfaced
	 * network is event-based, the preferred delivery time can be ignored,
	 * depending on the specific implementations.
	 *
	 * If a subscription already exists for the given device, it will be updated
	 * with the new subscription data.
	 *
	 * @return true if subscription was successful, false otherwise.
	 **/
	public boolean addSubscription(DataUpdateSubscription<T> subscription);
	
	/**
	 * Provides a {@link HashMap} of live references to currently active
	 * subscriptions for the device having the given low-level identifier
	 * 
	 * @param lUID the low-level device identifier
	 * @return The set of currently active subscriptions.
	 */
	public Set<DataUpdateSubscription<T>> listSubscriptions(String lUID);
	
	/**
	 * Gets the subscription registered by the device having the given lUID
	 * TODO: check if more than one subscription per low level UID is supported /
	 * needed
	 * 
	 * @param lUID
	 *            the low level identifier enabling data delivery
	 * @return The Update subscriptions associated to the given lUID
	 */
	public Set<DataUpdateSubscription<T>> getSubscriptions(String lUID);
	
	/**
	 * Removes the subscription to network-level data updates for the given
	 * {@link DataUpdateSubscription} instance
	 *
	 * @return true if removal is successful, false otherwise.
	 *
	 **/
	public boolean removeSubscription(DataUpdateSubscription<T> subscription);
	
	/**
	 * Gets the number of currently active subscriptions
	 * @return the number of currently active subscriptions
	 */
	public int getActiveSubscriptionsSize();
}
