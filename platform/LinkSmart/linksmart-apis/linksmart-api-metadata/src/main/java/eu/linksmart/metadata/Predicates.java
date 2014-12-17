package eu.linksmart.metadata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import static eu.linksmart.metadata.PrefixMapping.*;

/**
 * Mapping of predicate identifiers (URI or CURIE) to a set of typed
 * {@link Value}s. Lists URIs of frequent predicates.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class Predicates extends HashMap<String, Set<Value>> {

	private static final long serialVersionUID = 3335975266020120959L;

	/**
	 * Reference to the class the resource is an instance of.
	 */
	public static final String RDF_TYPE = NS_RDF + "type";

	/**
	 * Default value predicate of structured value objects.
	 */
	public static final String RDF_VALUE = NS_RDF + "value";

	/**
	 * Readable name of the resource.
	 */
	public static final String RDFS_LABEL = NS_RDFS + "label";

	/**
	 * Description of the resource.
	 */
	public static final String RDFS_COMMENT = NS_RDFS + "comment";

	/**
	 * Reference to a resource containing further information.
	 */
	public static final String RDFS_SEEALSO = NS_RDFS + "seeAlso";

	/**
	 * Reference to a resource definition (vocabulary, schema).
	 */
	public static final String RDFS_ISDEFINEDBY = NS_RDFS + "isDefinedBy";

	/**
	 * Adds a new value mapping for given predicate.
	 * 
	 * @param key
	 * @param value
	 */
	public void add(String predicate, Value value) {
		put(predicate, value, false);
	}

	/**
	 * Replaces any existing value mapping for given predicate by supplied
	 * value.
	 * 
	 * @param predicate
	 * @param value
	 */
	public void set(String predicate, Value value) {
		put(predicate, value, true);
	}

	protected void put(String predicate, Value value, boolean replace) {
		Set<Value> values = get(predicate);
		if (values == null) {
			values = new HashSet<>();
			put(predicate, values);
		}
		if (replace)
			values.clear();
		values.add(value);
	}

}
