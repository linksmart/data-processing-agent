/*
 * In case of German law being applicable to this license agreement, the following warranty and liability terms shall apply:
 *
 * 1. Licensor shall be liable for any damages caused by wilful intent or malicious concealment of defects.
 * 2. Licensor's liability for gross negligence is limited to foreseeable, contractually typical damages.
 * 3. Licensor shall not be liable for damages caused by slight negligence, except in cases 
 *    of violation of essential contractual obligations (cardinal obligations). Licensee's claims for 
 *    such damages shall be statute barred within 12 months subsequent to the delivery of the software.
 * 4. As the Software is licensed on a royalty free basis, any liability of the Licensor for indirect damages 
 *    and consequential damages - except in cases of intent - is excluded.
 *
 * This limitation of liability shall also apply if this license agreement shall be subject to law 
 * stipulating liability clauses corresponding to German law.
 */
/**
 * Copyright (C) 2006-2010 [Telefonica I+D]
 *                         the HYDRA consortium, EU project IST-2005-034891
 *
 * This file is part of LinkSmart.
 *
 * LinkSmart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU LESSER GENERAL PUBLIC LICENSE
 * version 3 as published by the Free Software Foundation.
 *
 * LinkSmart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with LinkSmart.  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.linksmart.gc.api.utils;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Tools needed by the Configurator class
 */
public class ConfigurationTools {
	
	/**
	 * This method analyzes the old configuration and the new updated one and 
	 * returns the updates (Only added or modified properties, not removed
	 * configurations yet)
	 * 
	 * @param oldConfig The old configuration dictionary
	 * @param newConfig The new configuration dictionary
	 * @return Dictionary containing the new or modified properties 
	 * (and their values)
	 */
	public static Hashtable getConfigurationChanges(Dictionary oldConfig,
			Dictionary newConfig) {

		// We have detected that the configuration has changed.
		// We will see what are the changes in order to identify if we need to 
		// change some of manager data, such as virtual address or description.
		Hashtable updates = new Hashtable();
		if (newConfig == null) {
			return updates;
		}
		
		Enumeration en = newConfig.keys();
		if (oldConfig == null) {
			while (en.hasMoreElements()) {
				Object next = (Object) en.nextElement();
				updates.put(next, newConfig.get(next));
			}
			return updates;
		}
		
		while (en.hasMoreElements()) {
			Object next = (Object) en.nextElement();
			// Check if the property is new or was removed.
			if (oldConfig.get(next) == null) {
				updates.put(next, newConfig.get(next));
			}
			else if (!(oldConfig.get(next).equals(newConfig.get(next)))) {
				updates.put(next, newConfig.get(next));
			}
		}	
		
		return updates;
	}

}
