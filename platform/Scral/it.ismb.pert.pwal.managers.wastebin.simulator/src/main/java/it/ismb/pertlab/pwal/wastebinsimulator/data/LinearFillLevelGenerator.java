/**
 * 
 */
package it.ismb.pertlab.pwal.wastebinsimulator.data;

import java.util.Date;

/**
 * @author bonino
 *
 */
public class LinearFillLevelGenerator
{
	/**
	 * Empty constructor
	 */
	public LinearFillLevelGenerator()
	{
		
	}
	
	public double getCurrentFillLevel(double oldFillLevel, Date lastUpdate, double nDaysToFull)
	{
		//delta x hour = full/nDaysToFull/24
		
		//last update time
		long lastUpdateMillis = lastUpdate.getTime();
		
		//now
		long nowMillis = (new Date()).getTime();
		
		//difference
		long diffMillis = nowMillis - lastUpdateMillis;
		
		//delta
		double deltaFill = ((100.0 * diffMillis)/(nDaysToFull*24.0*3600.0*1000.0));
		
		return Math.min(100.0, (oldFillLevel+deltaFill));
	}
	
}
