package eu.linksmart.resource.service.converter;

import javax.ws.rs.core.MediaType;

/**
 * Parses textual resource representation into a binary according to a media
 * type.
 * 
 * @author pullmann
 *
 * @param <T>
 */
public interface RepresenationParser<T> extends StringConvreter<String, T> {

	/**
	 * Parses the string input according to the default media type.
	 */
	@Override
	T convert(String input);

	/**
	 * Parses the string input according to the specified media type.
	 */
	T parse(String input, MediaType type);

}
