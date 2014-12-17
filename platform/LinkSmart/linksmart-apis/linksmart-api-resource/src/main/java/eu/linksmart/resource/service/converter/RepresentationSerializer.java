package eu.linksmart.resource.service.converter;

import javax.ws.rs.core.MediaType;

/**
 * Serializes binary representation into a textual according to the media type.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public interface RepresentationSerializer<T> extends StringConvreter<T, String> {

	/**
	 * Serializes the representation according to the default media type.
	 */
	@Override
	String convert(T input);

	/**
	 * Serializes the representation according to the supplied media type.
	 */
	String serialize(T input, MediaType type);

}
