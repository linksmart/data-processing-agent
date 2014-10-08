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
	
	public int getCurrentFillLevel(int oldFillLevel, Date lastUpdate, int nDaysToFull)
	{
		//delta x hour = full/nDaysToFull/24
		
		//last update time
		long lastUpdateMillis = lastUpdate.getTime();
		
		//now
		long nowMillis = (new Date()).getTime();
		
		//difference
		long diffMillis = nowMillis - lastUpdateMillis;
		
		//delta
		int deltaFill = (int)Math.round((100.0/(nDaysToFull*24.0*3600.0*1000.0))*(int)diffMillis);
		
		return oldFillLevel+deltaFill;
	}
	
}
