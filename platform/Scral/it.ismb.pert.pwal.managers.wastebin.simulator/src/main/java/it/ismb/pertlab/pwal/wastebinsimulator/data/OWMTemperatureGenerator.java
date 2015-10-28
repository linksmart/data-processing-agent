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

import java.util.List;

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Temperature;
import javax.measure.unit.SI;

import org.bitpipeline.lib.owm.OwmClient;
import org.bitpipeline.lib.owm.StatusWeatherData;
import org.bitpipeline.lib.owm.WeatherStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A temperature generator using Open Weather Map to deliver real-time,
 * realistic temperature estimations, given a sensor latitude and longitude
 * information
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class OWMTemperatureGenerator implements TemperatureGenerator
{
	// the logger
	protected static final Logger logger = LoggerFactory.getLogger(OWMTemperatureGenerator.class);
	
	// the client to Open Weather Maps
	private OwmClient openWeatherMapClient;
	
	/**
	 * The class constructor (empty), initializes the Open Weather Map client needed to retrieve temperature data..
	 */
	public OWMTemperatureGenerator()
	{
		// initialize inner datastructures
		this.openWeatherMapClient = new OwmClient();
	}
	
	/*
	 * (non-Javadoc)
	 * @see it.ismb.pertlab.pwal.wastebinsimulator.data.TemperatureGenerator#getCurrentTemperature(float, float)
	 * 
	 * 
	 * Provides the estimated temperature as a measure
	 *
	 */
	@Override
	public DecimalMeasure<Temperature> getCurrentTemperature(float latitude, float longitude)
	{
		//the temperature in Celsius degrees
		DecimalMeasure<Temperature> tCelsius = null;
		
		// get the current temperature at the given lat / long
		try
		{
			//get the response from the Open Weather Map service
			WeatherStatusResponse response = this.openWeatherMapClient.currentWeatherAtCity(latitude, longitude, 1);
			
			// if the weather data is available
			if (response.hasWeatherStatus())
			{
				//get all the matching stations
				List<StatusWeatherData> dataFromAllStations = response.getWeatherStatus();
				
				//if at least one station provided data
				if (!dataFromAllStations.isEmpty())
				{
					//get the temperature  from the first station
					float temperature = dataFromAllStations.get(0).getTemp();
					
					//if the temperature is actually a number
					if (temperature != Float.NaN)
					{
						// OWMTemperatureGenerator.logger.info("Temperature: "+temperature);
						// create the temperature measure in Kelvin and convert to Celsius
						// degrees
						DecimalMeasure<Temperature> tKelvin = DecimalMeasure.valueOf(temperature + " "
								+ SI.KELVIN.toString());
						tCelsius = tKelvin.to(SI.CELSIUS);
					}
				}
			}
		}
		catch (Exception e)
		{
			// log the error
			OWMTemperatureGenerator.logger.warn("Error while retrieving real-data", e);
		}
		
		return tCelsius;
	}
	
}
