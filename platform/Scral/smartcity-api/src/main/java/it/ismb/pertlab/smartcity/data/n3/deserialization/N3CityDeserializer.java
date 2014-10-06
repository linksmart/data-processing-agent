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

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.GeoPoint;
import it.ismb.pertlab.smartcity.api.SmartCity;

/**
 * @author bonino
 *
 */
public class N3CityDeserializer extends N3Deserializer<SmartCity>
{
	private Logger logger;
	
	public N3CityDeserializer()
	{
		// create the logger
		this.logger = LoggerFactory.getLogger(N3BinDeserializer.class);
	}
	
	@Override
	public SmartCity deserialize(OWLNamedIndividual cityIndividual, N3DeserializationHelper n3dh)
	{
		// create the city instance
		SmartCity city = new SmartCity();
		
		// set the city url
		city.setUrl(cityIndividual.getIRI().getShortForm());
		
		// set the city name
		city.setName(cityIndividual.getIRI().getShortForm());
		
		// get city annotations
		Map<String, Object> annotationValues = n3dh.getAnnotationValues(cityIndividual);
		
		// extract latitude
		Double latitude = (Double) annotationValues.get("latitude");
		Double longitude = (Double) annotationValues.get("longitude");
		String description = (String) annotationValues.get("description");
		
		if ((latitude != null) && (longitude != null))
		{
			GeoPoint cityLocation = new GeoPoint(latitude, longitude);
			city.setLocation(cityLocation);
		}
		
		// get the object property values of this city instance
		Map<String, Set<OWLIndividual>> allPValues = n3dh.getObjectPropertyValues(cityIndividual);
		
		// get has geometry
		Set<OWLIndividual> hasGeometry = allPValues.get("hasGeometry");
		
		// debug
		this.logger.debug("Found " + hasGeometry.size() + " geometries...");
		
		// iterate over geometries
		for (OWLIndividual geometryIndividual : hasGeometry)
		{
			if (geometryIndividual.isAnonymous())
			{
				Map<OWLDataPropertyExpression, Set<OWLLiteral>> values = ((OWLAnonymousIndividual) geometryIndividual)
						.getDataPropertyValues(n3dh.getOntModel());
				
				// debug
				this.logger.debug("Found " + values.size() + " valued dataproperties for hasGeometry individuals");
				
				for (OWLDataPropertyExpression dp : values.keySet())
				{
					if (dp.asOWLDataProperty().getIRI().getShortForm().equals("asWKT"))
					{
						// get the hasGeometry[ asWKT] property value
						String wktPolygon = values.get(dp).iterator().next().getLiteral();
						
						// build the boundary corresponding to the extracted
						// polygon
						GeoBoundary boundary = new GeoBoundary();
						boundary.setAsWKT(wktPolygon);
						
						// set the city geometry
						city.setBoundary(boundary);
						
						// debug
						this.logger.debug("Found Geometry: " + boundary + ", as wkt: " + boundary.getAsWKT());
					}
				}
			}
		}
		
		// debug log foun cities
		this.logger.info("Found city: " + city.getUrl() + "[\n location: " + longitude + "," + latitude
				+ "\n description:" + description + "\n]");
		
		return city;
	}
	
}
