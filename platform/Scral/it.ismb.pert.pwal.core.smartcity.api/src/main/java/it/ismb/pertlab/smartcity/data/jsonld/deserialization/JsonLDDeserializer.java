/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization;

import it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel.JsonLDEntry;

/**
 * @author bonino
 *
 */
public abstract class JsonLDDeserializer<T>
{
	/**
	 * 
	 */
	public JsonLDDeserializer()
	{
		// TODO Auto-generated constructor stub
	}
	
	public abstract T deserialize(JsonLDEntry entry);
	
}
