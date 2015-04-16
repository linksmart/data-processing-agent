package eu.linksmart.resource.service.converter;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serializes binary representation into a textual according to the media type.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public abstract class AbstractSerializer<T> implements
		StringConvreter<T, String> {

	protected Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

	/**
	 * Serializes the representation according to the default media type.
	 */
	@Override
	public abstract String convert(T input);

	/**
	 * Serializes the representation according to the supplied media type.
	 */
	public abstract String serialize(T input, MediaType type);

	@Override
	public Class<String> getOutputType() {
		return String.class;
	}

}
