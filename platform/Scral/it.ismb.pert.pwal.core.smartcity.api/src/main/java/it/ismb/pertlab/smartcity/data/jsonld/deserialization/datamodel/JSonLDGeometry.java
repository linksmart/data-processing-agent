/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bonino
 *
 */
public class JSonLDGeometry
{
	@JsonProperty("wkt")
	private String asWkt;
	/**
	 * 
	 */
	public JSonLDGeometry()
	{
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the asWkt
	 */
	public String getAsWkt()
	{
		return asWkt;
	}
	/**
	 * @param asWkt the asWkt to set
	 */
	public void setAsWkt(String asWkt)
	{
		this.asWkt = asWkt;
	}
	
	
	
}
