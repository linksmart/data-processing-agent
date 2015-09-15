/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bonino
 *
 */
public class JsonLDEntry
{
	@JsonProperty("@id")
	private String id;
	
	@JsonProperty("@type")
	private String type;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("geometry")
	private JSonLDGeometry geometry;
	
	@JsonProperty("within")
	private String within[];
	
	@JsonProperty("latitude")
	private double latitude;
	
	@JsonProperty("longitude")
	private double longitude;
	
	@JsonProperty("address")
	private JSonLDAddress address;
	
	@JsonProperty("geonames-id")
	private String geonamesid;
	
	/**
	 * 
	 */
	public JsonLDEntry()
	{
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	/**
	 * @return the geometry
	 */
	public JSonLDGeometry getGeometry()
	{
		return geometry;
	}
	/**
	 * @param geometry the geometry to set
	 */
	public void setGeometry(JSonLDGeometry geometry)
	{
		this.geometry = geometry;
	}
	/**
	 * @return the within
	 */
	public String[] getWithin()
	{
		return within;
	}
	/**
	 * @param within the within to set
	 */
	public void setWithin(String[] within)
	{
		this.within = within;
	}
	/**
	 * @return the address
	 */
	public JSonLDAddress getAddress()
	{
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(JSonLDAddress address)
	{
		this.address = address;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude()
	{
		return latitude;
	}
	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude()
	{
		return longitude;
	}
	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
	/**
	 * @return the geonamesid
	 */
	public String getGeonamesid()
	{
		return geonamesid;
	}
	/**
	 * @param geonamesid the geonamesid to set
	 */
	public void setGeonamesid(String geonamesid)
	{
		this.geonamesid = geonamesid;
	}	
	
}
