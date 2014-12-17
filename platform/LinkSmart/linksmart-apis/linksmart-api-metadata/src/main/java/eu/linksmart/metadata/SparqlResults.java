package eu.linksmart.metadata;

import java.util.LinkedList;
import java.util.List;

/**
 * <a
 * href="http://www.w3.org/TR/sparql11-results-json/#select-results">Result</a>
 * set of variable bindings.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlResults {

	List<SparqlResultBinding> bindings = new LinkedList<SparqlResultBinding>();

	public List<SparqlResultBinding> getBindings() {
		return bindings;
	}

}
