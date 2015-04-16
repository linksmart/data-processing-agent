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

package eu.linksmart.gc.tools;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONWriter;

import eu.linksmart.gc.api.network.Registration;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.identity.IdentityManager;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;
import eu.linksmart.gc.api.network.routing.BackboneRouter;
import eu.linksmart.gc.api.utils.Part;

/**
 * NetworkManagerStatus Servlet
 */
public class GetNetworkManagerStatus extends HttpServlet {

	Logger LOG = Logger.getLogger(GetNetworkManagerStatus.class.getName());

	IdentityManager identityManager;
	private NetworkManagerCore networkManagerCore;
	private BackboneRouter backboneRouter;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            the bundle's context
	 * @param nmServiceImpl
	 *            the Network Manager Service implementation
	 */
	public GetNetworkManagerStatus(NetworkManagerCore networkManagerCore,
			IdentityManager identityManager, BackboneRouter backboneRouter) {

		this.networkManagerCore = networkManagerCore;
		this.identityManager = identityManager;
		this.backboneRouter = backboneRouter;

	}

	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request
	 *            HttpServletRequest that encapsulates the request to the
	 *            servlet
	 * @param response
	 *            HttpServletResponse that encapsulates the response from the
	 *            servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		Map<String, String[]> params = request.getParameterMap();
		if (params.containsKey("method")) {
			String method = params.get("method")[0];
			if (method.equals("getNetworkManagers")) {
				Set<Registration> registrations = identityManager
						.getServicesByDescription("NetworkManager*");
				processServices(method, registrations, response);
			} else if (method.equals("getLocalServices")) {
				Set<Registration> registrations = identityManager.getLocalServices();
				LOG.debug("Size of LocalServices: " + registrations.size());
				processServices(method, registrations, response);
			} else if (method.equals("getRemoteServices")) {
				Set<Registration> registrations = identityManager.getRemoteServices();
				LOG.debug("Size of RemoteServices: " + registrations.size());
				processServices(method, registrations, response);
			} else if (method.equals("getNetworkManagerSearch")) {
				Set<Registration> registrations = identityManager.getAllServices();
				processServices(method, registrations, response);
				// TODO #NM should be "VirtualAddress entity not adapted to security issues"
				// instead of null for remote services; check with Mark?
			}
		}
	}

	private void processServices(String method, Set<Registration> registrations, HttpServletResponse response) {

		Iterator<Registration> it = registrations.iterator();

		StringWriter writer = new StringWriter();
		JSONWriter jsonWriter = new JSONWriter(writer);
		try {
			jsonWriter.object()
			.key("method").value(method)
			.key("VirtualAddresses").array();

			while (it.hasNext()) {
				Registration serviceInfo = it.next();
	
				// Create the description from all Attributes
				String description = "";
				Part[] attr = serviceInfo.getAttributes();
				for (Part part : attr) {
					description = description + part.getKey() + " = "
							+ part.getValue() + ";";
				}
	
				// Get the Route for this VirtualAddress
				VirtualAddress virtualAddress = serviceInfo.getVirtualAddress();
				String route = backboneRouter.getRoute(virtualAddress);
				String virtualAddressString = virtualAddress.toString();
				String path = "/SOAPTunneling/0/" + virtualAddressString;
				
				jsonWriter
				.object()
				.key("virtualAddress").value(virtualAddressString)
				.key("path").value(path)
				.key("wsdl").value(path + "?wsdl")
				.key("description").value(description)
				.key("host").value("TODO: Host")
				.key("endpoint").value(route)
				.endObject();
			} //end while
			
			jsonWriter
			.endArray()
			.endObject();

			response.setContentLength(writer.toString().getBytes().length);
			response.getWriter().write(writer.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("Can't generate HTML");
		}
	}

}
