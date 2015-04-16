package eu.linksmart.resource.service.converter;

import javax.ws.rs.core.MediaType;

/**
 * Seeks and resolves a parser for given media type within the service registry.
 * 
 * @author pullmann
 */
public interface RepresenationParserResolver {

	/**
	 * Resolves the parser according to media type and fully qualified name of
	 * the target type.
	 * 
	 * @param mediaType
	 * @param outputType
	 * @return
	 */
	<T> RepresenationParser<T> resolveParser(MediaType mediaType,
			Class<T> outputType);
}
