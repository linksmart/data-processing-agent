package eu.linksmart.gc.api.types;

import java.io.Serializable;

public class TunnelRequest implements Serializable {
	
	private static final long serialVersionUID = 36501679078630725L;
	
	private String http_method = null;
	private String http_path = null;
	private String[] http_headers = null;
	private byte[] http_body = null;
	
	public TunnelRequest() {
	}
	
	public void setMethod(String http_method) {
		this.http_method = http_method;
	}
	
	public String getMethod() {
		return this.http_method;
	}
	
	public void setPath(String http_path) {
		this.http_path = http_path;
	}
	
	public String getPath() {
		return this.http_path;
	}
	
	public void setHeaders(String[] http_headers) {
		this.http_headers = http_headers;
	}
	
	public String[] getHeaders() {
		return this.http_headers;
	}
	
	public void setBody(byte[] http_body) {
		this.http_body = http_body;
	}
	
	public byte[] getBody() {
		return this.http_body;
	}
}
