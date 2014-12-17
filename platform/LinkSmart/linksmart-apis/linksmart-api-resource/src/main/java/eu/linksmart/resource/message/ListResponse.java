package eu.linksmart.resource.message;

import java.util.List;

public class ListResponse<T> extends ResourceResponse {

	private static final long serialVersionUID = -6318039912441374072L;

	private List<T> resources;

	public ListResponse(List<T> resources) {
		this.resources = resources;
	}

	public List<T> getResources() {
		return resources;
	}

}
