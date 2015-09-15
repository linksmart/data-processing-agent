package it.ismb.pertlab.pwal.watermetersimulator.data;

/**
 * 
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Volume;
import javax.measure.unit.SI;

/**
 * @author bonino
 *
 */
public class DayProfileGenerator
{
	// position 0 = midnight
	// retrieved from
	// https://www.yvw.com.au/yvw/groups/public/documents/document/yvw1003346.pdf
	// in liters, i.e. dm^3, m^3*10^-3
	private double avgHourlyWaterConsumption[] = { 7.5, 3.0, 1.0, 0.5, 0.5, 1.0, 7.5, 12.5, 25.0, 32.0, 27.0, 23.0,
			17.0, 13.0, 12.0, 11.0, 11.0, 14.0, 21.0, 23.0, 20.0, 16.0, 13.0, 12.5 };
	
	/**
	 * Empty constructor
	 */
	public DayProfileGenerator()
	{
		
	}
	
	public DecimalMeasure<Volume> getCurrentFlowRate(DecimalMeasure<Volume> decimalMeasure,
			Date latestUpdate)
	{
		// delta x hour = full/nDaysToFull/24
		
		//get the current time
		Calendar now = GregorianCalendar.getInstance();
		
		//get the current hour to find the right multiplier
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		
		//get the time of the last sample
		Calendar last = GregorianCalendar.getInstance();
		last.setTime(latestUpdate);
		
		//get the last hour of the day
		int lastHour = last.get(Calendar.HOUR_OF_DAY);
		
		//the time delta between 2 subequent samples
		double delta = 0;
		
		//check if samples are in the same hour
		if(nowHour==lastHour)
		{
			//easier just compute the difference in milliseconds and provide the 
			delta = ((now.getTimeInMillis() - last.getTimeInMillis())/(60*60*1000.0))*avgHourlyWaterConsumption[nowHour];
		}
		else
		{
			//compute the number of hours between samples
			int nHour = lastHour-nowHour;
			
			for(int i=0; i<nHour; i++)
			{
				//compute the delta incrementally
				Calendar targetHour = GregorianCalendar.getInstance();
				targetHour.set(last.get(Calendar.YEAR), last.getMaximum(Calendar.MONTH), last.get(Calendar.DATE), last.get(Calendar.HOUR_OF_DAY)+i,0);
				
				//easier just compute the difference in milliseconds and provide the 
				delta += ((now.getTimeInMillis() - targetHour.getTimeInMillis())/(60*60*1000.0))*avgHourlyWaterConsumption[nowHour+i];
			}
		}
		
		return DecimalMeasure.valueOf(""+delta+" "+SI.CUBIC_METRE);
	}
	
}
