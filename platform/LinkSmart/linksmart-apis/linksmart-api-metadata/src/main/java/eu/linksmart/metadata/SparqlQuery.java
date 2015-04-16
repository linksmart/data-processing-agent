package eu.linksmart.metadata;

/**
 * Self-descriptive graph containing a SPARQL 1.1 Query expression.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlQuery extends SemanticResource {

	private static final long serialVersionUID = -321388728041950328L;

	public static enum QueryType {
		// DESCRIBE not included, since platform dependent
		SELECT, CONSTRUCT, ASK,
	}

	public SparqlQuery(String name) {
		super(name);
	}
}
