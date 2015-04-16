package eu.linksmart.gc.network.identity.util;

import java.io.Serializable;

/**
 * Class containing an attribute resolve request
 * including the Bloom-filter, the random and the
 * string composed of requested keys.
 * @author Vinkovits
 *
 */
public class AttributeResolveFilter implements Serializable {
	static final long serialVersionUID = 1L;
	boolean[] bloomFilter;
	String attributeKeys;
	Long random;
	boolean isStrictRequest;

	/**
	 * 
	 * @param bloom
	 * @param attr
	 * @param rand
	 * @param isStrict true - the queried entity has to match all attributes
	 * false - attributes the queried entity does not have are ignored
	 */
	public AttributeResolveFilter(boolean[] bloom, String attr, long rand, boolean isStrict) {
		bloomFilter = bloom;
		attributeKeys = attr;
		random = new Long(rand);
		isStrictRequest = isStrict;
	}

	public boolean[] getBloomFilter() {
		return bloomFilter;
	}
	public String getAttributeKeys() {
		return attributeKeys;
	}
	public long getRandom() {
		return random.longValue();
	}
	public boolean getIsStrictRequest(){
		return isStrictRequest;
	}
}
