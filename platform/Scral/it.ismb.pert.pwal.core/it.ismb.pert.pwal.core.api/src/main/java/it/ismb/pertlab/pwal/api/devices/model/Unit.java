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
 * Unit used for a value
 *
 */
public class Unit {

    private String value;
    private String symbol;
    private String type;
    
    /**
     * Returns the description of the unit (Watts, Celsius,...)
     * 
     * @return description of the unit
     */
	public String getValue() {
		return value;
	}
	
	/**
	 * Sets the description of the unit (Watts, Celsius,...)
	 * 
	 * @param value
	 *        description to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Returns the symbol of the unit
	 * 
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/**
	 * Sets the symbol of the unit
	 * 
	 * @param symbol
	 *        symbol to set
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	/**
	 * Returns the type of the unit 
	 * (basicSI, derivedSI, conversionBasedUnits, derivedUnits, contextDependentUnits)
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Sets the type of the unit
	 * (basicSI, derivedSI, conversionBasedUnits, derivedUnits, contextDependentUnits)
	 * 
	 * @param type
	 *        type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return this.value;
	}
	
	
}