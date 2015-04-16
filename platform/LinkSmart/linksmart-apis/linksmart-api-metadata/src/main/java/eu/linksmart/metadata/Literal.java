package eu.linksmart.metadata;

/**
 * Literal predicate value optionally specifying the data type and language.
 * 
 * @author pullmann
 *
 */
public class Literal extends Value {

	/**
	 * Copy constructor to strongly type a {@link Value}.
	 * 
	 * @param value
	 */
	public Literal(Value value) {
		this(value.getValue(), value.getDatatype(), value.getLang());
	}

	/**
	 * Untyped string literal.
	 * 
	 * @param value
	 */
	public Literal(String value) {
		this(value, null, null);
	}

	/**
	 * Typed literal.
	 * 
	 * @param value
	 *            URI or CURIe of the datatype.
	 * @param datatype
	 */
	public Literal(String value, String datatype) {
		this(value, datatype, null);
	}

	/**
	 * Typed literal specifying a language tag.
	 * 
	 * @param value
	 * @param datatype
	 * @param lang
	 */
	public Literal(String value, String datatype, String lang) {
		super(value, ValueType.literal, datatype, lang);
	}

}
