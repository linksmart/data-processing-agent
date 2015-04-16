package eu.linksmart.metadata.service;

import static eu.linksmart.metadata.SparqlMediaType.APPLICATION_SPARQL_QUERY;
import static javax.ws.rs.core.MediaType.APPLICATION_FORM_URLENCODED;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.QueryParam;

/**
 * Service contract of the <a
 * href="www.w3.org/TR/sparql11-protocol#query-operation">SPARQL 1.1 Protocol
 * <em>query</em></a> operation. It accepts a mandatory SPARQL 1.1 Query string
 * along with optional enumeration of default and named graphs to be used as the
 * target RDF Dataset if not already defined within the query itself via the
 * <code>FROM</code> and/or <code>FROM NAMED</code> keywords. The RDF Dataset
 * definition via request parameters takes precedence. 
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlQueryProtocolService {

	public static final String PARAMETER_DEFAULT_QUERY = "query";

	public static final String PARAMETER_DEFAULT_GRAPH_URI = "default-graph-uri";

	public static final String PARAMETER_NAMED_GRAPH_URI = "named-graph-uri";

	@GET
	Object executeGetQuery(
			// TODO: Activate after resolving validation issues with Jersey
			// @NotNull(message
			// ="The parameter 'query' containing a SPARQL 1.1. Query string is mandatory.")
			// Singular query string
			@QueryParam(PARAMETER_DEFAULT_QUERY) String query,
			// RDF Dataset: 0 - n default and named graphs
			@QueryParam(PARAMETER_DEFAULT_GRAPH_URI) List<String> defaultGraphUri,
			@QueryParam(PARAMETER_NAMED_GRAPH_URI) List<String> namedGraphUri);

	@POST
	@Consumes(APPLICATION_FORM_URLENCODED)
	Object executeUrlEncodedQuery(
			// @NotNull(message =
			// "The parameter 'query' containing a SPARQL 1.1. Query string is mandatory.")
			@FormParam(PARAMETER_DEFAULT_QUERY) String query,
			@FormParam(PARAMETER_DEFAULT_GRAPH_URI) List<String> defaultGraphUri,
			@FormParam(PARAMETER_NAMED_GRAPH_URI) List<String> namedGraphUri);

	@POST
	@Consumes(APPLICATION_SPARQL_QUERY)
	Object executePostQuery(
			// @NotNull(message = "The SPARQL 1.1. Query string is mandatory.")
			// Plain text payload
			String query,
			@QueryParam(PARAMETER_DEFAULT_GRAPH_URI) List<String> defaultGraphUri,
			@QueryParam(PARAMETER_NAMED_GRAPH_URI) List<String> namedGraphUri);

}
