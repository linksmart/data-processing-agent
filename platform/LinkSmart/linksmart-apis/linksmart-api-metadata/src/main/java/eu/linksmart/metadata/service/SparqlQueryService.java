package eu.linksmart.metadata.service;

import java.util.Map;

import eu.linksmart.metadata.Graph;
import eu.linksmart.metadata.SparqlQueryResult;
import eu.linksmart.resource.service.ResourceQueryService;

/**
 * Service for execution of supplied or persistent SPARQL 1.1 Query statements
 * resolved via the {@link SparqlQueryManager}. This service is safe and does
 * not modify the RDF Dataset specified in-line.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public interface SparqlQueryService extends ResourceQueryService<String, Object> {

	/**
	 * Generic method to invoke a supplied query regardless of its type. The
	 * client has to inspect and cast the result object accordingly.
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	@Override
	Object execute(String sparqlQuery);

	/**
	 * Executes the supplied SPARQL SELECT query on the RDF Dataset specified
	 * in-line.
	 * 
	 * @param selectQuery
	 * @return
	 */
	SparqlQueryResult executeSelectQuery(String selectQuery);

	/**
	 * Executes the supplied SPARQL CONSTRUCT query on the RDF Dataset specified
	 * in-line.
	 * 
	 * @param constructQuery
	 * @return
	 */
	Graph executeConstructQuery(String constructQuery);

	/**
	 * Executes the supplied SPARQL ASK query on the RDF Dataset specified
	 * in-line.
	 * 
	 * @param askQuery
	 * @return
	 */
	Boolean executeAskQuery(String askQuery);

	/**
	 * Generic method to invoke indicated persistent query regardless of its
	 * type. The client has to inspect and cast the result object accordingly.
	 * 
	 * @param name
	 * @param params
	 * @return
	 */
	@Override
	Object call(String queryName, Map<String, ?> params);

	/**
	 * Invocation of a persistent SPARQL SELECT query optionally parametrized by
	 * given parameter map.
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	SparqlQueryResult callSelectQuery(String queryName,
			Map<String, String> params);

	/**
	 * Invocation of a persistent SPARQL CONSTRUCT query optionally parametrized
	 * by given parameter map.
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	Graph callConstructQuery(String queryName, Map<String, String> params);

	/**
	 * Invocation of a persistent SPARQL ASK query optionally parametrized by
	 * given parameter map.
	 * 
	 * @param queryName
	 * @param params
	 * @return
	 */
	Boolean callAskQuery(String queryName, Map<String, String> params);

}
