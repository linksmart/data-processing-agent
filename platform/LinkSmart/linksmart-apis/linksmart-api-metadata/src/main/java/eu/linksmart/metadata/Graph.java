package eu.linksmart.metadata;

import static eu.linksmart.metadata.PrefixMapping.NS_RDFS;
import static eu.linksmart.metadata.Value.ValueType.literal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tiny RDF graph model inspired by the <a
 * href="http://www.w3.org/TR/rdf-json/">RDF 1.1 JSON Alternate Serialization
 * (RDF/JSON)</a> specification. Map of subject identifiers (URIs or CURIEs) to
 * maps of predicate values ({@link Predicates}). TODO: extend accessor API
 * (e.g. path access).
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class Graph extends HashMap<String, Predicates> {

	private static final long serialVersionUID = 8827055788530891294L;

	protected static Logger LOG = LoggerFactory
			.getLogger(Graph.class.getName());

	/**
	 * Retrieves the predicate map for given subject, alias for generic
	 * {@link #get(Object)}.
	 * 
	 * @param subject
	 * @return
	 */
	public Predicates getPredicates(String subject) {
		return get(subject);
	}

	/**
	 * Adds a new value mapping for given subject and predicate.
	 * 
	 * @param subject
	 * @param predicate
	 * @param value
	 */
	public void add(String subject, String predicate, Value value) {
		put(subject, predicate, value, false);
	}

	/**
	 * Replaces any existing value mapping for given subject and predicate with
	 * supplied value.
	 * 
	 * @param subject
	 * @param predicate
	 * @param value
	 */
	public void set(String subject, String predicate, Value value) {
		put(subject, predicate, value, true);
	}

	/**
	 * Retrieves all values of the annotation predicate <code>rdfs:label</code>.
	 * 
	 * @return
	 */
	public List<Literal> getLabels(String subject) {
		List<Literal> result = getLiterals(subject, "rdfs:label");
		if (result == null || result.isEmpty()) {
			result = getLiterals(subject, NS_RDFS + "label");
		}
		return result;
	}

	/**
	 * Adds a label annotation to specified resource.
	 * 
	 * @param subject
	 * @param label
	 */
	public void addLabel(String subject, Literal label) {
		add(subject, "rdfs:label", label);
	}

	/**
	 * Retrieves all values of the annotation predicate
	 * <code>rdfs:comment</code>.
	 * 
	 * @return
	 */
	public List<Literal> getComments(String subject) {
		List<Literal> result = getLiterals(subject, "rdfs:comment");
		if (result == null || result.isEmpty()) {
			result = getLiterals(subject, NS_RDFS + "comment");
		}
		return result;
	}

	/**
	 * Adds a comment annotation to specified resource.
	 * 
	 * @param subject
	 * @param label
	 */
	public void addComment(String subject, Literal comment) {
		add(subject, "rdfs:comment", comment);
	}

	/**
	 * Retrieves a list of literal values for given predicate.
	 * 
	 * @param prediacte
	 *            The compact or extended URI of the predicate.
	 * @return List of associated {@link Literal} values.
	 */
	public List<Literal> getLiterals(String subject, String prediacte) {
		List<Literal> result = null;
		// Test whether there are any statements about subject
		if (get(subject) != null) {
			Set<Value> values = get(subject).get(prediacte);
			if (values != null) {
				result = new LinkedList<Literal>();
				for (Value value : values) {
					if (value instanceof Literal) {
						result.add((Literal) value);
					} else if (literal.equals(value.getType())) {
						// Turn into explicit Literal
						result.add(new Literal(value));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Generates a Turtle representation of this graph.
	 * 
	 * @return
	 */
	// TODO: Move to a RepresentationSerializer?
	public String toTurtle() {
		// object separator: ,
		// predicate separator: ;
		// statement separator: .
		StringBuffer sb = new StringBuffer();
		for (String subject : keySet()) {
			// Subject URI
			sb.append("<").append(subject).append("> ");
			int pCount = 0;
			Predicates rGraph = get(subject);
			for (String predicate : rGraph.keySet()) {
				// Delimit predicate
				if (pCount > 0)
					sb.append("; ");
				sb.append("<").append(predicate).append("> ");
				++pCount;
				int vCount = 0;
				for (Value value : rGraph.get(predicate)) {
					// Delimit value
					if (vCount > 0)
						sb.append(", ");
					sb.append(value.toTurtle());
					++vCount;
				}
			}
			// Close overall statement
			sb.append(". ");
		}
		return sb.toString();
	}

	protected void put(String subject, String predicate, Value value,
			boolean replace) {
		Predicates p = get(subject);
		if (p == null) {
			p = new Predicates();
			put(subject, p);
		}
		if (replace) {
			p.set(predicate, value);
		} else {
			p.add(predicate, value);
		}
	}
}
