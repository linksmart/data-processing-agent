package eu.linksmart.metadata.service.converter;

import javax.ws.rs.core.MediaType;

import com.hp.hpl.jena.rdf.model.Model;

import eu.linksmart.resource.service.converter.RepresenationParser;

/**
 * Parses one of the supported textual RDF representations into a Jena model.
 * 
 * <a href="https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax">Jena RDF-Syntax</a>
 * 
 * @author pullmann
 *
 */
public interface JenaModelParser extends RepresenationParser<Model> {
	
	@Override	
	Model convert(String input);

	@Override	
	Model parse(String input, MediaType type);

}
