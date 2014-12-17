package eu.linksmart.metadata;

import javax.ws.rs.core.MediaType;

/**
 * Common SPARQL media types.
 * 
 * @author pullmann
 *
 */
public class SparqlMediaType extends MediaType {

	/**
	 * <a href="http://www.w3.org/TR/sparql11-query/">SPARQL 1.1 Query
	 * Language</a>, extension ".rq"
	 */
	public static final String APPLICATION_SPARQL_QUERY = "application/sparql-query";

	public static final MediaType APPLICATION_SPARQL_QUERY_TYPE = MediaType
			.valueOf(APPLICATION_SPARQL_QUERY);

	/**
	 * <a href="http://www.w3.org/TR/sparql11-update/">SPARQL 1.1 Update</a>,
	 * extension ".ru"
	 */
	public static final String APPLICATION_SPARQL_UPDATE = "application/sparql-update";

	public static final MediaType APPLICATION_SPARQL_UPDATE_TYPE = MediaType
			.valueOf(APPLICATION_SPARQL_UPDATE);

	/**
	 * <a href="http://www.w3.org/TR/rdf-sparql-XMLres/">SPARQL Query Results
	 * XML Format</a>, extension ".srx"
	 */
	public static final String APPLICATION_SPARQL_RESULTS_XML = "application/sparql-results+xml";

	public static final MediaType APPLICATION_SPARQL_RESULTS_XML_TYPE = MediaType
			.valueOf(APPLICATION_SPARQL_RESULTS_XML);

	/**
	 * <a href="http://www.w3.org/TR/sparql11-results-json/">SPARQL 1.1 Query
	 * Results JSON Format</a>, extension ".srj"
	 */
	public static final String APPLICATION_SPARQL_RESULTS_JSON = "application/sparql-results+json";

	public static final MediaType APPLICATION_SPARQL_RESULTS_JSON_TYPE = MediaType
			.valueOf(APPLICATION_SPARQL_RESULTS_JSON);

	/**
	 * <a href="http://www.w3.org/TR/sparql11-results-csv-tsv/">SPARQL 1.1 Query
	 * Results CSV and TSV Formats</a>, extension ".csv"
	 */
	public static final String APPLICATION_SPARQL_RESULTS_CSV = "text/csv";

	public static final MediaType APPLICATION_SPARQL_RESULTS_CSV_TYPE = MediaType
			.valueOf(APPLICATION_SPARQL_RESULTS_CSV);

	/**
	 * <a href="http://www.w3.org/TR/sparql11-results-csv-tsv/">SPARQL 1.1 Query
	 * Results CSV and TSV Formats</a>, extension ".tsv"
	 */
	public static final String APPLICATION_SPARQL_RESULTS_TSV = "text/tab-separated-values";

	public static final MediaType APPLICATION_SPARQL_RESULTS_TSV_TYPE = MediaType
			.valueOf(APPLICATION_SPARQL_RESULTS_TSV);

}
