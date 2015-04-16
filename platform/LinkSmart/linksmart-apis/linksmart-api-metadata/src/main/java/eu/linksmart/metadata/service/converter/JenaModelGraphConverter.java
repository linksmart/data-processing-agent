package eu.linksmart.metadata.service.converter;

import com.hp.hpl.jena.rdf.model.Model;

import eu.linksmart.metadata.Graph;
import eu.linksmart.resource.service.converter.RepresentationConvreter;

/**
 * Converts rather heavy-weight Jena Model into a simple {@link Graph}.
 *
 * @author pullmann
 *
 */
public interface JenaModelGraphConverter extends
		RepresentationConvreter<Model, Graph> {

}
