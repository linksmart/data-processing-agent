package eu.linksmart.metadata;

/**
 * Self-descriptive graph containing a mapping of prefix and namespace URI. The
 * class exposes some default mappings for reference.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class PrefixMapping extends SemanticResource {

	/**
	 * RDF prefix mapping.
	 */
	public static final String NS_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	/**
	 * RDF Schema prefix mapping.
	 */
	public static final String NS_RDFS = "http://www.w3.org/2000/01/rdf-schema#";

	/**
	 * RDF in Attributes prefix mapping.
	 */
	public static final String NS_RDFA = "http://www.w3.org/ns/rdfa#";

	/**
	 * XML Schema prefix mapping.
	 */
	public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema#";

	/**
	 * LinkSmart prefix mapping.
	 */
	public static final String NS_LS = "http://linksmart.eu/ontology#";

	/**
	 * Semantic resource prefix mapping.
	 */
	public static final String NS_RES = "urn:res:";

	private static final long serialVersionUID = -1704900615165683659L;

	public PrefixMapping(String name) {
		super(name);
	}

	public PrefixMapping(String name, String prefix, String uri) {
		super(name);
		add("rdfa:prefix", new Literal(prefix, "xsd:string"));
		add("rdfa:uri", new Value(uri));
	}

}
