package eu.linksmart.metadata;

/**
 * Generic type of a predicate value: resource (URI or CURIE), blank node or a
 * literal.
 * 
 * @author jaroslav.pullmann@fit.fraunhofer.de
 * 
 */
public class Value {

	public static enum ValueType {
		// Lower-case letters used in conformance with the RDF/JSON spec.
		uri, literal, bnode
	}

	private String value = null;

	private ValueType type = null;

	private String datatype = null;

	private String lang = null;

	/**
	 * Empty constructor required by serialization code.
	 */
	public Value() {

	}

	/**
	 * Simple URI- value (does not require further specification).
	 * 
	 * @param uri
	 */
	public Value(String uri) {
		this.value = uri;
		this.type = ValueType.uri;
	}

	/**
	 * Resource object - value of type "uri".
	 * 
	 * @param value
	 * @param type
	 */
	public Value(String value, ValueType type) {
		this(value, type, null, null);
	}

	public Value(String value, ValueType type, String datatype) {
		this(value, type, datatype, null);
	}

	/**
	 * Literal value.
	 * 
	 * @param value
	 * @param type
	 * @param datatype
	 * @param lang
	 */
	public Value(String value, ValueType type, String datatype,
			String lang) {
		setValue(value);
		setType(type);
		// Optional, applies only for literals
		if (ValueType.literal.equals(type)) {
			if (datatype != null)
				setDatatype(datatype);
			if (lang != null)
				setLang(lang);
		}
	}

	public String getValue() {
		return value;
	}

	public ValueType getType() {
		return type;
	}

	public void setType(ValueType type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String toTurtle() {
		if (type.equals(ValueType.uri)) {
			return "<" + value + ">";
		}
		if (type.equals(ValueType.literal)) {
			if (datatype != null)
				return "'" + value + "'^^" + datatype;
			if (getLang() != null)
				return "'" + value + "'@" + lang;
			return "'" + value + "'";
		}
		return value;
	}

	@Override
	public String toString() {
		return type + ":" + value + (datatype != null ? "^^" + datatype : "")
				+ (lang != null ? "@" + lang : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datatype == null) ? 0 : datatype.hashCode());
		result = prime * result + ((lang == null) ? 0 : lang.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * Compares two {@link Value}s for equality considering <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#dfn-iri-equality">IRI
	 * equality</a> and <a
	 * href="http://www.w3.org/TR/rdf11-concepts/#dfn-literal-term-equality"
	 * >literal equality</a> rules.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Value other = (Value) obj;
		if (datatype == null) {
			if (other.datatype != null)
				return false;
		} else if (!datatype.equals(other.datatype))
			return false;
		if (lang == null) {
			if (other.lang != null)
				return false;
		} else if (!lang.equals(other.lang))
			return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
