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

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public interface DataUpdateSubscriber <T>
{
	/**
	 * Called when updated data is available at the network level, the data
	 * payload might change depending on the specific network and therefore is
	 * handled by means of generic specification. Data handling should ideally
	 * be performed in 0 time, therefore any "heavy" or time-consuming
	 * operations on received data must be possibly moved to a separate thread.
	 * 
	 * @param <T>
	 **/
	public void handleUpdate(T updatedData);
	
	/**
	 * Provides the network-level id that allows identifying the device data
	 * among the set of information handled at the network-level
	 * 
	 * @return The network-level unique identifier (opaque) or null if not available
	 */
	public String getNetworkLevelId();
}
