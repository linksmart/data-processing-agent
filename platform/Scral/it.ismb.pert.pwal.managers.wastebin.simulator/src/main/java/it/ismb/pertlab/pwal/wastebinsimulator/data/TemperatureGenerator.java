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

import javax.measure.DecimalMeasure;
import javax.measure.quantity.Temperature;

/**
 * The generic set of methods offered by a temperature generator used by the waste bin simulator.
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public interface TemperatureGenerator
{
	/**
	 * get the temperature at the given location, at the current time
	 * @param latitude The latitude of the sensor
	 * @param longitude The longitude of the sensor
	 * @return The "best" estimate of the current temperature at the given location.
	 */
	public DecimalMeasure<Temperature> getCurrentTemperature(float latitude, float longitude);
}
