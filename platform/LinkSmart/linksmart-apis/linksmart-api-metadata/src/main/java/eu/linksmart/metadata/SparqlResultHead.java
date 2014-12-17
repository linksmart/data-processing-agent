package eu.linksmart.metadata;

import java.util.Arrays;
import java.util.List;

/**
 * <a href="http://www.w3.org/TR/sparql11-results-json/#select-head">Head</a>
 * part of the SPARQL Query result.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlResultHead {

	private List<String> vars = null;

	// Binding variables in order of appearance.
	public List<String> getVars() {
		return vars;
	}

	public SparqlResultHead(List<String> vars) {
		this.vars = vars;
	}

	public SparqlResultHead(String... vars) {
		if (vars != null && vars.length > 0)
			this.vars = Arrays.asList(vars);
	}

}
