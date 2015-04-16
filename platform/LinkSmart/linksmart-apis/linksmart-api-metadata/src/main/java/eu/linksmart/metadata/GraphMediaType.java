package eu.linksmart.metadata;

import javax.ws.rs.core.MediaType;

/**
 * Media types of RDF concrete syntaxes/representations.
 * 
 * @author pullmann
 *
 */
public class GraphMediaType extends MediaType {

	/**
	 * <a href="http://www.w3.org/TR/rdf-syntax-grammar/">RDF 1.1 XML Syntax
	 * (RDF/XML)</a>, extension ".rdf" or ".xml
	 */
	public static final String APPLICATION_RDF_XML = "application/rdf+xml";

	public static final MediaType APPLICATION_RDF_XML_TYPE = MediaType
			.valueOf(APPLICATION_RDF_XML);

	/**
	 * <a href="http://www.w3.org/TR/rdf-json/">RDF 1.1 JSON Alternate
	 * Serialization (RDF/JSON)</a>, extension ".rj"
	 */
	public static final String APPLICATION_RDF_JSON = "application/rdf+json";

	public static final MediaType APPLICATION_RDF_JSON_TYPE = MediaType
			.valueOf(APPLICATION_RDF_JSON);

	/**
	 * Proprietary format equivalent to RDF/JSON but using compact identifiers
	 * (QNames/CURIEs) instead of full URIs, extension .rjc
	 */
	public static final String APPLICATION_RDF_COMPACT_JSON = "application/rdf-compact+json";

	public static final MediaType APPLICATION_RDF_COMPACT_JSON_TYPE = MediaType
			.valueOf(APPLICATION_RDF_COMPACT_JSON);

	/**
	 * <a href="http://www.w3.org/TR/json-ld/">JSON-LD 1.0, JSON-based
	 * Serialization for Linked Data</a>.
	 */
	public static final String APPLICATION_LD_JSON = "application/ld+json";

	public static final MediaType APPLICATION_LD_JSON_TYPE = MediaType
			.valueOf(APPLICATION_LD_JSON);

	/**
	 * <a
	 * href="http://www.w3.org/TeamSubmission/n3/#sec-mediaReg">Notation3</a>,
	 * extension ".n3"
	 */
	public static final String TEXT_N3 = "text/n3";

	public static final MediaType TEXT_N3_TYPE = MediaType.valueOf(TEXT_N3);

	/**
	 * <a href="http://www.w3.org/TR/turtle/">Terse RDF Triple Language</a>,
	 * extension ".ttl"
	 */
	public static final String APPLICATION_TURTLE = "application/x-turtle";

	public static final MediaType APPLICATION_TURTLE_TYPE = MediaType
			.valueOf(APPLICATION_TURTLE);

}
