package eu.linksmart.metadata.service;

import java.util.Map;

/**
 * Service for execution of supplied and persistent SPARQL 1.1 Update requests
 * resolved via the {@link SparqlUpdateManager}. This service persistently
 * modifies the RDF Dataset specified in-line and was therefore separated from
 * safe {@link SparqlQueryService}. Its provision and execution should be
 * limited by further security mechanism.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlUpdateService {

	/**
	 * Invokes the supplied update.
	 * 
	 * @param sparqlUpdate
	 */
	void execute(String sparqlUpdate);

	/**
	 * Invokes an update template parametrized with supplied parameter map.
	 * 
	 * @param name
	 * @param params
	 */
	void call(String updateName, Map<String, String> params);

}
