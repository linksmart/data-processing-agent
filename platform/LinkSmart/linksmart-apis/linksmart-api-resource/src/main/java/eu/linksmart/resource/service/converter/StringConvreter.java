package eu.linksmart.resource.service.converter;

import java.util.Set;

import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.MediaType;

/**
 * Generic interface for symmetric conversion of resource's string
 * representations. Implementations should throw {@link NotSupportedException},
 * when supplied media type is not a member of {@link #getSupportedMediaTypes()}
 * set.
 * 
 * @author pullmann
 *
 */
public interface StringConvreter<IN, OUT> extends
		RepresentationConvreter<IN, OUT> {

	/**
	 * Comma separated list of supported resource media types.
	 */
	static final String PROPERTY_SUPPORTED_MEDIATYPES = "converter.supported.mediatypes";

	Set<MediaType> getSupportedMediaTypes();

	/**
	 * Default media type applied for parsing or serialization.
	 * 
	 * @return
	 */
	MediaType getDefault();
}
