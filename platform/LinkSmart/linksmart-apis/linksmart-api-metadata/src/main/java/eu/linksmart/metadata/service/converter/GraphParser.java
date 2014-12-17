package eu.linksmart.metadata.service.converter;

import eu.linksmart.metadata.Graph;
import eu.linksmart.resource.service.converter.RepresenationParser;

/**
 * Parses one of the supported textual RDF representations into a Graph
 * instance.
 * 
 * @see <a href="https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax">Jena RDF-Syntax</a>

 * @author pullmann
 *
 */
public interface GraphParser extends RepresenationParser<Graph> {

}
