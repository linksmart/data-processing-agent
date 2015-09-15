/*
 * SmartCityAPI - KML to N3 conversion
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
package it.ismb.pertlab.smartcity.data.n3.deserialization;

import org.semanticweb.owlapi.model.OWLNamedIndividual;

/**
 * @author bonino
 *
 */
public abstract class N3Deserializer<T>
{
	
	/**
	 * 
	 */
	public N3Deserializer()
	{
		// TODO Auto-generated constructor stub
	}
	
	public abstract T deserialize(OWLNamedIndividual individual, N3DeserializationHelper helper);
	
}
