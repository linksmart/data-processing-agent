/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bonino
 *
 */
public class JSonLDAddress
{
	@JsonProperty("street-address")
	private String streetAddress;
	
	@JsonProperty("locality")
	private String locality;
	/**
	 * 
	 */
	public JSonLDAddress()
	{
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the streetAddress
	 */
	public String getStreetAddress()
	{
		return streetAddress;
	}
	/**
	 * @param streetAddress the streetAddress to set
	 */
	public void setStreetAddress(String streetAddress)
	{
		this.streetAddress = streetAddress;
	}
	/**
	 * @return the locality
	 */
	public String getLocality()
	{
		return locality;
	}
	/**
	 * @param locality the locality to set
	 */
	public void setLocality(String locality)
	{
		this.locality = locality;
	}
	
}
