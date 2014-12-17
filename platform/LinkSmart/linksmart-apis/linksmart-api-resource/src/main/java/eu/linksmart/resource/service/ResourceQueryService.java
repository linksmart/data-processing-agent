package eu.linksmart.resource.service;

import java.util.Map;

/**
 * Generic service for execution of supplied ad-hoc queries and calling
 * persistent, parametrized queries. The query management is out of scope and
 * should be implemented by an appropriate {@link ResourceManager}.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 *
 * @param <Q>
 * @param <R>
 * 
 */
public interface ResourceQueryService<Q, R> {

	/**
	 * Applies the supplied query on an implicit data set.
	 * 
	 * @param query
	 * @return
	 */
	R execute(Q query);

	/**
	 * Resolves, optionally parametrizes and applies the persistent query on an
	 * implicit data set.
	 * 
	 * @param queryIdentifier
	 * @param params
	 * @return
	 */
	R call(String queryIdentifier, Map<String, ?> params);

}
