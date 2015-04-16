package eu.linksmart.metadata;

/**
 * Result structure of a SELECT query aligned with the specifications <a
 * href="http://www.w3.org/TR/rdf-sparql-XMLres/">SPARQL Query Results XML
 * Format</a> and <a href="http://www.w3.org/TR/sparql11-results-json/">SPARQL
 * 1.1 Query Results JSON Format</a>.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class SparqlQueryResult {

	private SparqlResultHead head;

	private SparqlResults results;

	public SparqlQueryResult(SparqlResultHead head, SparqlResults results) {
		this.head = head;
		this.results = results;
	}

	public SparqlResultHead getHead() {
		return head;
	}

	public SparqlResults getResults() {
		return results;
	}

}
