package eu.linksmart.resource.service.converter;

/**
 * Converts an input representation into an output representation. Clients may
 * either search the service registry for {@link RepresentationConvreter} and
 * filter according to the registered properties {@link #PROPERTY_INPUT_TYPE}
 * and {@link #PROPERTY_OUTPUT_TYPE} or query via the service interface itself.
 * 
 * @author pullmann
 *
 * @param <IN>
 * @param <OUT>
 */
public interface RepresentationConvreter<IN, OUT> {

	/**
	 * Fully qualified name of the input class.
	 */
	static final String PROPERTY_INPUT_TYPE = "convreter.input.type";

	/**
	 * Fully qualified name of the output class.
	 */
	static final String PROPERTY_OUTPUT_TYPE = "convreter.output.type";

	OUT convert(IN input);

	Class<IN> getInputType();

	Class<OUT> getOutputType();

}
