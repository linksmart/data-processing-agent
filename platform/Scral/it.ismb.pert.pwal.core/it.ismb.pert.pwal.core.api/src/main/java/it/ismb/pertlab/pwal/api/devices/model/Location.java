/** 
 * Coded By Giorgio Dal Toè on 17/set/2013 
 *
 * Internet of Things Service Management Unit 
 * Pervasive Technologies Area
 * Istituto Superiore Mario Boella
 * Tel. (+39) 011 2276614
 * Email: daltoe@ismb.it
 * Email: giorgio.daltoe@gmail.com 
 * 
 * '||'  .|'''.|  '||    ||' '||''|.   
 *  ||   ||..  '   |||  |||   ||   ||  
 *  ||    ''|||.   |'|..'||   ||'''|.  
 *  ||  .     '||  | '|' ||   ||    || 
 * .||. |'....|'  .|. | .||. .||...|'
 *
 * Via Pier Carlo Boggio 61 
 * 10138 Torino, Italy
 * T 011/2276201; F 011/2276299
 * info@ismb.it
 */
package it.ismb.pertlab.pwal.api.devices.model;

/**
 * 
 * Class used to store the info about the location of the device
 * derived from the format used in xively
 *
 */
public class Location {
	
    private String name;
    private Double lat;
    private Double lon;
    private Double ele;
    private String exposure;
    private String domain;
    private String disposition;
    
    /**
     * Returns the name of the location
     * 
     * @return the name of the location
     */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the location
	 * 
	 * @param name
	 *         location to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the latitude of the device's location
	 * 
	 * @return the latitude
	 *  
	 */
	public double getLat() {
		return lat;
	}
	
	/**
	 * Sets the latitude of the device's location
	 * 
	 * @param lat
	 *       the latitude to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	/**
	 * Returns the longitude of the device's location
	 * 
	 * @return the longitude
	 */
	public double getLon() {
		return lon;
	}
	
	/**
	 * Sets the longitude of the device's location
	 * 
	 * @param lon
	 *        longitude to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	/**
	 * Returns the elevation of the device's location
	 * 
	 * @return the elevation
	 */
	public Double getEle() {
		return ele;
	}
	
	/**
	 * Sets the elevation of the device's location
	 * 
	 * @param ele
	 *          elevation to set
	 */
	public void setEle(Double ele) {
		this.ele = ele;
	}
	
	/**
	 * Returns the exposure of the device (indoor or outdoor)
	 * 
	 * @return the exposure
	 */
	public String getExposure() {
		return exposure;
	}
	
	/**
	 * Sets the exposure of the device (indoor or outdoor) 
	 * 
	 * @param exposure
	 *          exposure to set
	 */
	public void setExposure(String exposure) {
		this.exposure = exposure;
	}
	
	/**
	 * Domain of the device (physical and virtual)
	 * 
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}
	
	/**
	 * Sets the domain of the device (physical or virtual)
	 * 
	 * @param domain
	 *        domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	/**
	 * Sets the disposition of the device (fixed or mobile)
	 * 
	 * @return 
	 */
	public String getDisposition() {
		return disposition;
	}
	
	/**
	 * Sets the disposition of the device (fixed or mobile)
	 * 
	 * @param disposition
	 *        disposition to set 
	 */
	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return this.lon+" "+this.lat;
	}
	
	

}
