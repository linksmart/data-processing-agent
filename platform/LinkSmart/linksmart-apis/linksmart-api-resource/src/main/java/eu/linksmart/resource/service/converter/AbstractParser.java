package eu.linksmart.resource.service.converter;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses textual resource representation into a binary according to a media
 * type.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public abstract class AbstractParser<T> implements StringConvreter<String, T> {

	protected Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

	/**
	 * Parses the string input according to the default media type.
	 */
	@Override
	public abstract T convert(String input);

	/**
	 * Parses the string input according to the specified media type.
	 */
	public abstract T parse(String input, MediaType type);

	@Override
	public Class<String> getInputType() {
		return String.class;
	}

}
