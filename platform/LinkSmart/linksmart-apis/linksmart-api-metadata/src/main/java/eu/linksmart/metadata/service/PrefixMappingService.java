package eu.linksmart.metadata.service;

import java.util.Map;

import eu.linksmart.metadata.Graph;

/**
 * Simplified version of com.hp.hpl.jena.shared.PrefixMapping exposed as OSGi
 * service. It is used to interface the translation of URIs into compact URIs
 * (CURIEs) and vice versa based on system-wide mapping maintained by
 * {@link PrefixMappingManager}.
 * 
 * @author pullmann
 *
 */
public interface PrefixMappingService {

	/**
	 * Retrieves the prefix mapped to given namespace URI.
	 * 
	 * @param namespace
	 * @return Prefix string, or <code>null</code> if there is no mapping of
	 *         this namespace defined.
	 */
	String getPrefix(String namespace);

	/**
	 * Retrieves the namespace URI mapped to given prefix.
	 * 
	 * @param prefix
	 * @return Namespace URI string, or <code>null</code> if there is no mapping
	 *         of this prefix defined.
	 */
	String getNamespace(String prefix);

	/**
	 * Replaces all namespace URIs with their compact form (CURIE) according to
	 * predefined prefix mapping. Throws {@link IllegalArgumentException} when a
	 * namespace URI of a graph resource could not be resolved.
	 * 
	 * @param graph
	 * @return Graph with all namespace URIs replaced with compact URIs.
	 */
	Graph compact(Graph graph) throws IllegalArgumentException;

	/**
	 * The method does the opposite of {@link #compact(Graph)} by expanding
	 * CURIEs into full URIs. Throws {@link IllegalArgumentException} when a
	 * CURIe prefix of a graph resource could not be resolved to a namespace.
	 * 
	 * @param graph
	 * @return Graph with all CURIEs expanded to full URIs.
	 */
	Graph expand(Graph graph) throws IllegalArgumentException;

	/**
	 * Extracts a map of prefixes/namespaces present within given graph. Throws
	 * {@link IllegalArgumentException} when either of these could not be
	 * resolved with regards to predefined mapping.
	 * 
	 * @param graph
	 * @return Subset of managed namespaces used in input string.
	 */
	Map<String, String> extractPrefixMappings(Graph graph)
			throws IllegalArgumentException;

}
