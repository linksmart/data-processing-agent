package eu.linksmart.resource.service.converter;

import javax.ws.rs.core.MediaType;

/**
 * Seeks and resolves a parser for given media type within the service registry.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public interface RepresentationSerializerResolver<T> {

	RepresentationSerializer<T> resolveSerializer(MediaType mediaType,
			Class<T> inputType);
}
