package eu.linksmart.gc.network.tunneling.http;

public class TunnelException extends Exception {
	
	private static final long serialVersionUID = -8596970060015274531L;

	private int code = 0;
	
	public TunnelException(int code, String message) {
		super(message);
		this.setError(code);
	}

	public int getError() {
		return code;
	}

	public void setError(int code) {
		this.code = code;
	}
}
