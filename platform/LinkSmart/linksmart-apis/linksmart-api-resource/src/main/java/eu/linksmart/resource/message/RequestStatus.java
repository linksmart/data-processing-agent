package eu.linksmart.resource.message;

/**
 * Status of a resource request processing.
 * 
 * @author pullmann
 *
 */
public class RequestStatus {

	public enum StatusType {
		/**
		 * The request has been received.
		 */
		INIT,
		/**
		 * The processing of the request has started.
		 */
		ACTIVE,
		/**
		 * The processing of the request finished with a failure.
		 */
		FAILED,
		/**
		 * The processing of the request finished successfully.
		 */
		DONE
	}

	protected long requestTime;

	protected long responseTime;

	protected StatusType statusType;

	protected ResourceResponse response;

	public RequestStatus(long requestTime, long responseTime,
			StatusType statusType, ResourceResponse response) {
		super();
		this.requestTime = requestTime;
		this.responseTime = responseTime;
		this.statusType = statusType;
		this.response = response;
	}

	/**
	 * Returns the instant when the request processing started.
	 * 
	 * @return The time stamp in milliseconds (UTZ).
	 */
	public long getRequestTime() {
		return requestTime;
	}

	/**
	 * Returns the instant when the request processing ended.
	 * 
	 * @return The time stamp in milliseconds (UTZ), or <code>0</code> if the
	 *         processing has not finished yet.
	 */
	public long getResponseTime() {
		return responseTime;
	}

	/**
	 * Indicates the processing status of the request.
	 * 
	 * @return
	 */
	public StatusType getStatus() {
		return statusType;
	}

}
