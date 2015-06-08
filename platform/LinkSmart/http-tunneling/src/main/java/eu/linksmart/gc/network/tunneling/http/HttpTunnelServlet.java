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

package eu.linksmart.gc.network.tunneling.http;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.linksmart.gc.api.types.TunnelRequest;
import eu.linksmart.gc.api.types.TunnelResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;

/**
 * HTTP Tunnel servlet
 * 
 * @author Hassan Rasheed
 */
public class HttpTunnelServlet extends HttpServlet {

	private static final long serialVersionUID = -3819571723858557513L;

	private static final Logger LOG = Logger.getLogger(HttpTunnelServlet.class.getName());
	
	private NetworkManagerCore nmCore = null;

	/**
	 * Constructor with parameters
	 * 
	 * @param nmCore
	 *            the Network Manager application
	 * 
	 */
	public HttpTunnelServlet(NetworkManagerCore nmCore) {
		this.nmCore = nmCore;
	}

	/**
	 * Performs the HTTP GET operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);		
	}
	
	/**
	 * Performs the HTTP POST operation
	 * 
	 * @param request HttpServletRequest that encapsulates the request to the servlet
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}
	
	/**
	 * Performs the HTTP PUT operation
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}
	
	/**
	 * Performs the HTTP DELETE operation
	 * Is identical to GET
	 * @param request HttpServletRequest that encapsulates the request to the servlet 
	 * @param response HttpServletResponse that encapsulates the response from the servlet
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		process(request, response);
	}
	
	private void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		try {
			
			//
			// get path info
			//
			String pathInfo = request.getPathInfo();
			LOG.debug("pathInfo: " + pathInfo);
			
			//
			// verify & extract tokens from path 
			//
			StringTokenizer path_tokens = TunnelProcessor.getPathTokens(pathInfo);
			
			//
			// extract sender virtual address from path
			//
			String sender_vad_string = path_tokens.nextToken();
			VirtualAddress senderVAD = TunnelProcessor.getSenderVAD(sender_vad_string, this.nmCore.getVirtualAddress());
			
			//
			// extract receiver virtual address from path
			//
			String receiver_vad_string = path_tokens.nextToken();
			VirtualAddress receiverVAD = TunnelProcessor.getReceiverVAD(receiver_vad_string);
			
			//
			// add service_path, attributes (if any) and headers
			//
			// read request content
			//
			String service_path = "";
			String content = "";
			
			if(request.getMethod().equals("GET")) {
				service_path = TunnelProcessor.getServicePath(path_tokens, request, true);
			} else if(request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
				service_path = TunnelProcessor.getServicePath(path_tokens, request, false);
				content = TunnelProcessor.getContent(request);
			} else if(request.getMethod().equals("DELETE")) {
				service_path = TunnelProcessor.getServicePath(path_tokens, request, false);
			}
			
			//
			// create tunnel request
			//
			TunnelRequest tunnel_request = new TunnelRequest();
			
			tunnel_request.setMethod(request.getMethod());
			tunnel_request.setPath(service_path);
			tunnel_request.setHeaders(TunnelProcessor.getHeaders(request));
			tunnel_request.setBody(content.getBytes());
			
			LOG.debug("s-vad: " + senderVAD.toString());
			LOG.debug("r-vad: " + receiverVAD.toString());		
			LOG.debug("servicePath: " + service_path);
			String[] headers = tunnel_request.getHeaders();
			for (int i = 0; i < headers.length; i++) {
				LOG.debug("header: " + headers[i]);
			}
			
			//
			// send tunnel request & read response
			//
			TunnelResponse tunnel_response = TunnelProcessor.sendTunnelRequest(tunnel_request, nmCore, senderVAD, receiverVAD);
			LOG.debug("http-tunnel status: " + tunnel_response.getStatusCode());
			
//			if (tunnel_response.getStatusCode() != HttpServletResponse.SC_OK) {
//			//set whole response data as servlet body
//			servlet_response_body = tunnel_response.getBody();
//		} else {
//			//take headers from data and add them to response body
//			servlet_response_body = addHeadersToResponse(tunnel_response.getBody(), response);
		
			response.setStatus(tunnel_response.getStatusCode());
			byte[] servlet_response_body = tunnel_response.getBody();
		
			//
			// write servlet_response_body data
			//
			response.setContentLength(servlet_response_body.length);
			response.getOutputStream().write(servlet_response_body);
			response.getOutputStream().close();	
			
		} catch (TunnelException e) {
			LOG.error(e.getMessage(), e);
			response.sendError(e.getError(), e.getMessage());
			return;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
	}
}
