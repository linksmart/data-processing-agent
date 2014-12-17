package eu.linksmart.metadata.service.converter;

import com.hp.hpl.jena.rdf.model.Model;

import eu.linksmart.resource.service.converter.RepresentationSerializer;

/**
 * Serializes a Jena model into one of the supported textual RDF
 * representations.
 * 
 * @see <a href="https://jena.apache.org/documentation/io/rdf-input.html#determining-the-rdf-syntax">Jena RDF-Syntax</a>
 * 
 * @author pullmann
 *
 */
public interface JenaModelSerializer extends RepresentationSerializer<Model> {

}
