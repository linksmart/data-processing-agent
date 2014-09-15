/*
 * PWAL -Waste Bin Data Simulator
 * 
 * Copyright (c) 2014 Dario Bonino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package it.ismb.pertlab.pwal.wastebinsimulator.data;

import java.util.Calendar;
import java.util.Date;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Temperature;

/**
 * A class for generating a realistic temperature profile (and value) given the
 * estimated maximum, minimum and average daily temperatures. It uses the
 * temperature computation defined in the UNI10349 normative for the climatic
 * zone A, in Italy.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * 
 *         TODO: to be finished
 *
 */
public class UNI10349TemperatureGenerator
{
	// the correction factors for the timezone A in Italy defined according the
	// UNI10349 standard
	@SuppressWarnings("unused")
	private double correctionFactors[][] = {
			{ -0.23, -0.23, -0.27, -0.29, -0.34, -0.34, -0.34, -0.32, -0.28, -0.25, -0.25, -0.23 },
			{ -0.26, -0.27, -0.32, -0.34, -0.39, -0.4, -0.38, -0.36, -0.31, -0.28, -0.27, -0.25 },
			{ -0.28, -0.31, -0.35, -0.38, -0.43, -0.44, -0.43, -0.41, -0.34, -0.32, -0.3, -0.28 },
			{ -0.31, -0.34, -0.37, -0.42, -0.47, -0.49, -0.47, -0.44, -0.38, -0.35, -0.33, -0.31 },
			{ -0.33, -0.37, -0.4, -0.45, -0.49, -0.5, -0.49, -0.46, -0.41, -0.38, -0.35, -0.33 },
			{ -0.35, -0.4, -0.41, -0.46, -0.46, -0.46, -0.47, -0.46, -0.43, -0.4, -0.38, -0.35 },
			{ -0.37, -0.41, -0.43, -0.45, -0.41, -0.38, -0.41, -0.45, -0.44, -0.42, -0.39, -0.35 },
			{ -0.35, -0.38, -0.38, -0.37, -0.29, -0.25, -0.29, -0.37, -0.38, -0.38, -0.36, -0.34 },
			{ -0.28, -0.28, -0.24, -0.19, -0.11, -0.08, -0.11, -0.19, -0.21, -0.24, -0.24, -0.26 },
			{ -0.17, -0.13, -0.04, 0.06, 0.12, 0.13, 0.11, 0.06, 0.03, -0.02, -0.07, -0.14 },
			{ 0.01, 0.05, 0.16, 0.27, 0.31, 0.3, 0.29, 0.28, 0.26, -0.2, 0.13, 0.02 },
			{ 0.19, 0.22, 0.31, 0.39, 0.41, 0.39, 0.39, 0.4, 0.39, 0.35, 0.3, 0.21 },
			{ 0.43, 0.41, 0.44, 0.47, 0.47, 0.45, 0.45, 0.47, 0.49, 0.48, 0.48, 0.44 },
			{ 0.57, 0.53, 0.53, 0.52, 0.49, 0.48, 0.48, 0.5, 0.54, 0.55, 0.58, 0.58 },
			{ 0.61, 0.58, 0.55, 0.53, 0.5, 0.49, 0.49, 0.51, 0.54, 0.56, 0.6, 0.61 },
			{ 0.59, 0.57, 0.54, 0.5, 0.48, 0.47, 0.48, 0.5, 0.5, 0.53, 0.56, 0.57 },
			{ 0.5, 0.5, 0.47, 0.44, 0.43, 0.42, 0.43, 0.46, 0.42, 0.44, 0.47, 0.47 },
			{ 0.37, 0.38, 0.37, 0.35, 0.36, 0.35, 0.36, 0.38, 0.32, 0.32, 0.33, 0.34 },
			{ 0.18, 0.21, 0.23, 0.23, 0.26, 0.26, 0.27, 0.27, 0.19, 0.19, 0.14, 0.15 },
			{ 0.02, 0.07, 0.09, 0.11, 0.14, 0.15, 0.16, 0.16, 0.07, 0.07, -0.01, 0 },
			{ -0.06, -0.02, 0, 0.01, 0.02, 0.04, 0.05, 0.04, -0.02, -0.02, -0.09, -0.08 },
			{ -0.12, -0.08, -0.09, -0.1, -0.11, -0.1, -0.09, -0.09, -0.11, -0.11, -0.14, -0.12 },
			{ -0.16, -0.13, -0.16, -0.18, -0.21, -0.21, -0.19, -0.19, -0.19, -0.19, -0.18, -0.16 },
			{ -0.2, -0.18, -0.22, -0.25, -0.27, -0.28, -0.27, -0.27, -0.24, -0.24, -0.23, -0.2 } };
	
	// the temperature at a given hour Thour is computed by the following
	// formula:
	// Thour =Tavg+cF*(Tmax-Tmin)
	// where Tavg is the average temperature durig the day, Tmax is the maximum
	// temperature and Tmin is the minimum.
	
	// http://www.ilmeteo.it/portale/archivio-meteo/Torino/2014/Settembre/14
	
	// the minimum daily temperature, for the currently handled day
	@SuppressWarnings("unused")
	private double minimumTemperature;
	
	// the maximum daily temperature for the currently handled day
	@SuppressWarnings("unused")
	private double maximumTemperature;
	
	// the average temperature for the currently handled day
	@SuppressWarnings("unused")
	private double averageTemperature;
	
	// the day to emulate, the current day minus one by default
	private Date dayToEmulate;
	@SuppressWarnings("unused")
	private Date referenceDay;
	
	/**
	 * Default empty constructor, it builds the generator to "emulate" the
	 * current day as closer as possible to the actual temperature profile
	 * exploiting data from the previous day.
	 */
	public UNI10349TemperatureGenerator()
	{
		// compute the initial value for the reference day, might change if the
		// generator runs for more than one day
		this.updateCurrentDate();
		
	}
	
	public DecimalMeasure<Temperature> getCurrentTemperature()
	{
		// compute the current date
		this.updateCurrentDate();
		
		// build the url for getting the actual meteo information from the
		// referenceDay (
		
		return null;
	}
	
	private void updateCurrentDate()
	{
		// get a calendar on the current date
		Calendar calendar = Calendar.getInstance();
		
		// if a precise date has been specified, use that date.
		if (dayToEmulate != null)
			calendar.setTime(dayToEmulate);
		
		// subtract one day
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		
		// fill the day to emulate
		this.referenceDay = calendar.getTime();
	}
}
