package eu.linksmart.metadata.service;

import static eu.linksmart.metadata.SparqlMediaType.APPLICATION_SPARQL_UPDATE;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

/**
 * Service contract of the <a
 * href="www.w3.org/TR/sparql11-protocol#update-operation">SPARQL 1.1 Protocol
 * <em>update</em></a> operation. It accepts a mandatory SPARQL 1.1 Update
 * string along with optional enumeration of default and named graphs to be used
 * as the target RDF Dataset, if not already defined within the update itself
 * via the <code>USING, USING NAMED</code> and/or <code>WITH</code> keywords. In
 * contrast to the <em>query</em> operation both alternatives to specify the RDF
 * Dataset are exclusive and their simultaneous usage will throw an error.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlUpdateProtocolService {

	public static final String PARAMETER_UPDATE = "update";

	public static final String PARAMETER_USING_GRAPH_URI = "using-graph-uri";

	public static final String PARAMETER_USING_NAMED_GRAPH_URI = "using-named-graph-uri";

	@POST
	@Consumes(APPLICATION_FORM_URLENCODED)
	void executeUrlEncodedUpdate(
			// @NotNull(message =
			// "The parameter 'update' containing a SPARQL 1.1 Update string is mandatory.")
			@FormParam(PARAMETER_UPDATE) String update,
			@FormParam(PARAMETER_USING_GRAPH_URI) List<String> defaultGraphUri,
			@FormParam(PARAMETER_USING_NAMED_GRAPH_URI) List<String> namedGraphUri);

	@POST
	@Consumes(APPLICATION_SPARQL_UPDATE)
	void executePostUpdate(
			// @NotNull(message = "The SPARQL 1.1 Update string is mandatory.")
			// Plain text payload
			String update,
			@QueryParam(PARAMETER_USING_GRAPH_URI) List<String> defaultGraphUri,
			@QueryParam(PARAMETER_USING_NAMED_GRAPH_URI) List<String> namedGraphUri);

}
