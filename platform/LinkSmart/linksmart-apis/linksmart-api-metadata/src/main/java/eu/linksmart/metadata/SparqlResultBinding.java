package eu.linksmart.metadata;

import java.util.HashMap;

/**
 * Mapping of variable name to {@link Value}s. In contrast to
 * {@link Predicates} the keys represent names of bound query variables and
 * not predicate CURIEs.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlResultBinding extends HashMap<String, Value> {

	private static final long serialVersionUID = 6511730844979028400L;

}
