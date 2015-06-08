package eu.linksmart.gc.network.tunneling.http;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.linksmart.gc.api.types.TunnelRequest;
import eu.linksmart.gc.api.types.TunnelResponse;
import eu.linksmart.gc.api.types.utils.SerializationUtil;
import eu.linksmart.gc.api.network.NMResponse;
import eu.linksmart.gc.api.network.VirtualAddress;
import eu.linksmart.gc.api.network.networkmanager.core.NetworkManagerCore;

public class TunnelProcessor {
	
	public static StringTokenizer getPathTokens(String pathInfo) throws TunnelException, Exception {
		
		if(pathInfo == null || pathInfo.length() == 0) {
			throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "pathInfo is empty, no virtual-address is given");
		}
		
		StringTokenizer path_tokens = new StringTokenizer(pathInfo, "/");
		
		if(path_tokens.countTokens() < 2) {
			throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "sender/receiver virtual-address(s) are missing");
		}
		
		return path_tokens;
	}
	
	public static VirtualAddress getSenderVAD(String sender_vad_string, VirtualAddress defaultVAD) throws TunnelException, Exception {
		
		VirtualAddress senderVAD = null;
		
		if(sender_vad_string.contentEquals("0")) {
			senderVAD = defaultVAD;
		} else {
			Pattern s_pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
			Matcher s_matcher = s_pattern.matcher(sender_vad_string);
			if(s_matcher.find()) {
				senderVAD = new VirtualAddress(s_matcher.group());
			}
		}
		
		if(senderVAD == null) {
			throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "unable to retrieve sender virtual-address from path token: " + sender_vad_string);
		}
		
		if(senderVAD != null) {
			if(senderVAD.getBytes().length != VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH) {
				throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "provided sender virtual-address doesn't conform to its format: " + senderVAD.toString());
			}
			if(senderVAD.toString() == "0.0.0.0") {
				throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "computed sender virtual-address doesn't conform to its format: " + senderVAD.toString());
			}
		}
		
		return senderVAD;
	}
	
	public static VirtualAddress getReceiverVAD(String token) throws TunnelException, Exception {
		
		VirtualAddress receiverVAD = null;
		
		Pattern r_pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
		Matcher r_matcher = r_pattern.matcher(token);
		if(r_matcher.find()) {
			receiverVAD = new VirtualAddress(r_matcher.group());
		} 
		
		if(receiverVAD == null) {
			throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "unable to retrieve reciever virtual-address from path token: " + token);
		}
		
		if(receiverVAD != null) {
			if(receiverVAD.getBytes().length != VirtualAddress.VIRTUAL_ADDRESS_BYTE_LENGTH) {
				throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "provided reciever virtual-address doesn't conform to its format: " + receiverVAD.toString());
			}
			if(receiverVAD.toString() == "0.0.0.0") {
				throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "computed reciever virtual-address doesn't conform to its format: " + receiverVAD.toString());
			}
		}
		
		return receiverVAD;
	}
	
	public static String getServicePath(StringTokenizer path_tokens, HttpServletRequest request, boolean queryString) throws TunnelException, Exception {
		
		String service_path = "";
		
		StringBuilder servicePathBuilder = new StringBuilder();
		
		while(path_tokens.hasMoreTokens()) {
			service_path = service_path + "/" + path_tokens.nextToken();
		}
		
		if(queryString) {
			StringBuilder queryBuilder = new StringBuilder();
			String query_string = request.getQueryString();
			if (query_string != null && query_string.length() != 0) {
				queryBuilder.append("?");
				queryBuilder.append(query_string);
			}
			servicePathBuilder.append(service_path);
			servicePathBuilder.append(queryBuilder.toString());
		} else
			servicePathBuilder.append(service_path);
		
		return servicePathBuilder.toString();
	}
	
	public static String getContent(HttpServletRequest request) throws TunnelException, Exception {
		
		StringBuilder contentBuilder = new StringBuilder();
		
		if (request.getContentLength() > 0) {
			try {
				BufferedReader reader = request.getReader();
				for (String line = null; (line = reader.readLine()) != null;)
					contentBuilder.append(line);
				reader.close();
			} catch (Exception e) {
				throw new TunnelException(HttpServletResponse.SC_BAD_REQUEST, "unable to read from request stream");
			}
		} else {
			throw new TunnelException(HttpServletResponse.SC_NO_CONTENT, "request content is empty");
		}
		return contentBuilder.toString();
	}
	
	public static TunnelResponse sendTunnelRequest(TunnelRequest tunnel_request, NetworkManagerCore nmCore, VirtualAddress senderVAD, VirtualAddress receiverVAD) throws TunnelException, Exception {
		
		NMResponse nm_response = nmCore.sendData(senderVAD, receiverVAD,  SerializationUtil.serialize(tunnel_request), true);

		TunnelResponse tunnel_response = (TunnelResponse) SerializationUtil.deserialize(nm_response.getMessageBytes(), TunnelResponse.class);
		
		return tunnel_response;
	}
	
	public static String[] getHeaders(HttpServletRequest request) throws TunnelException, Exception {
		//StringBuilder builder = new StringBuilder();
		List<String> headers = new ArrayList<String>();
		Enumeration<?> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String header = (String) headerNames.nextElement();
			String value = request.getHeader(header);
			headers.add(header + ": " + value);
			//builder.append(header + ": " + value + "\r\n");
		}
		return headers.toArray(new String[headers.size()]);
	}
	
	public static byte[] addHeadersToResponse(byte[] byteData, HttpServletResponse response) throws TunnelException, Exception {
		int bodyStartIndex = 0;
		byte[] headerEnd = new String("\r\n\r\n").getBytes();
		//find end of header
		for(;bodyStartIndex < byteData.length; bodyStartIndex++) {
			if(bodyStartIndex + headerEnd.length < byteData.length) {
				if(Arrays.equals(Arrays.copyOfRange(byteData, bodyStartIndex, bodyStartIndex + headerEnd.length), headerEnd)) {
					bodyStartIndex = bodyStartIndex + headerEnd.length;
					break;
				}
			} else {
				bodyStartIndex = byteData.length;
				break;
			}
		}
		byte[] headersBytes = Arrays.copyOf(byteData, bodyStartIndex);
		String[] headers = new String(headersBytes).split("(?<=\r\n)");
		//go through headers and put them to response until empty line is reached
		for (String header : headers) {	
			if(header.contentEquals("\r\n")) {
				break;
			}
			if(!header.toLowerCase().startsWith("http")) {
				header = header.replace("\r\n", "");
				String[] headerParts = header.split(":");
				if(headerParts.length == 2) {
					response.setHeader(headerParts[0], headerParts[1].trim());
				}
			}
		}
		return Arrays.copyOfRange(byteData, bodyStartIndex, byteData.length);
	}

}
