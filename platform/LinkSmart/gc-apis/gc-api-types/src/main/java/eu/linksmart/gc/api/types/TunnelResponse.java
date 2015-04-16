package eu.linksmart.gc.api.types;

import java.io.Serializable;

public class TunnelResponse implements Serializable {
	
	private static final long serialVersionUID = -8671214558120003281L;
	
	private int status_code = 0;
	private String[] http_headers = null;
	private byte[] http_body = null;
	
	public TunnelResponse() {
	}
	
	public void setStatusCode(int status_code) {
		this.status_code = status_code;
	}
	
	public int getStatusCode() {
		return this.status_code;
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
