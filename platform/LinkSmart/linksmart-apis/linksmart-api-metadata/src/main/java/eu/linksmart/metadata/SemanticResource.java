package eu.linksmart.metadata;

import java.util.List;

/**
 * Graph excerpt describing a single "resource" entity. The name URI is passed
 * as external parameter in order to query triples of the underlying graph.
 * 
 * @author pullmann
 *
 */
public class SemanticResource extends Graph {

	private static final long serialVersionUID = -4681833130462364202L;

	protected String name;

	public SemanticResource(String name) {
		this.name = name;
	}

	public String getName() {
		return null;
	}

	/**
	 * Retrieves the predicates map for this resource, alias for generic
	 * {@link #get(Object)}.
	 * 
	 * @param subject
	 * @return
	 */
	public Predicates getPredicates() {
		return get(getName());
	}

	/**
	 * Adds a new value mapping for given subject and predicate.
	 * 
	 * @param subject
	 * @param predicate
	 * @param value
	 */
	public void add(String predicate, Value value) {
		put(predicate, value, false);
	}

	/**
	 * Replaces any existing value mapping for given subject and predicate by
	 * supplied value.
	 * 
	 * @param subject
	 * @param predicate
	 * @param value
	 */
	public void set(String predicate, Value value) {
		put(predicate, value, true);
	}

	/**
	 * Retrieves all values of the annotation predicate <code>rdfs:label</code>
	 * for this resource.
	 * 
	 * @return
	 */
	public List<Literal> getLabels() {
		return getLabels(getName());
	}

	/**
	 * Adds a label annotation to this resource.
	 * 
	 * @param subject
	 * @param label
	 */
	public void addLabel(Literal label) {
		addLabel(getName(), label);
	}

	/**
	 * Retrieves all values of the annotation predicate
	 * <code>rdfs:comment</code> for this resource.
	 * 
	 * @return
	 */
	public List<Literal> getComments() {
		return getComments(getName());
	}

	/**
	 * Adds a comment annotation to this resource.
	 * 
	 * @param subject
	 * @param comment
	 */
	public void addComment(Literal comment) {
		addComment(getName(), comment);
	}

	protected void put(String predicate, Value value, boolean replace) {
		Predicates predicates = get(getName());
		if (predicates == null) {
			predicates = new Predicates();
			put(getName(), predicates);
		}
		if (replace) {
			predicates.set(predicate, value);
		} else {
			predicates.add(predicate, value);
		}
	}

}
