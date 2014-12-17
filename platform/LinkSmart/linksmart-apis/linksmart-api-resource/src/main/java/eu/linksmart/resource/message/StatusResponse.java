package eu.linksmart.resource.message;



/**
 * Responses indicating the status of handling the request.
 * 
 * @author pullmann
 *
 */
public class StatusResponse extends ResourceResponse {

	private static final long serialVersionUID = -1397804379329007547L;

	private int status;

	private String description;

	public StatusResponse(int status, String description) {
		this.status = status;
		this.description = description;
	}

	public StatusResponse(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

}
