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

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.SmartCity;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 *
 */
public class N3DistrictDeserializer extends N3Deserializer<District>
{
	
	private Logger logger;
	private Map<String, Object> smartCityEntities;
	
	public N3DistrictDeserializer(Map<String, Object> allSmartCityEntities)
	{
		// create the logger
		this.logger = LoggerFactory.getLogger(N3BinDeserializer.class);
		this.smartCityEntities = allSmartCityEntities;
	}
	
	@Override
	public District deserialize(OWLNamedIndividual districtIndividual, N3DeserializationHelper n3dh)
	{
		// create the city instance
		District district = new District();
		
		// set the city url
		district.setUrl(districtIndividual.getIRI().getShortForm());
		
		// set the city name
		district.setName(districtIndividual.getIRI().getShortForm());
		
		// get city annotations
		Map<String, Object> annotationValues = n3dh.getAnnotationValues(districtIndividual);
		
		// extract latitude
		
		String description = (String) annotationValues.get("description");
		
		// get the object property values of this city instance
		Map<String, Set<OWLIndividual>> allPValues = n3dh.getObjectPropertyValues(districtIndividual);
		
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
						district.setBoundary(boundary);
						
						// debug
						this.logger.debug("Found Geometry: " + boundary + ", as wkt: " + boundary.getAsWKT());
					}
				}
			}
		}
		
		// handle containment
		// get has geometry
		Set<OWLIndividual> sfWithin = allPValues.get("sfWithin");
		
		// debug
		this.logger.debug("Found " + sfWithin.size() + " geometries...");
		
		// iterate over geometries
		for (OWLIndividual sfWithinIndividual : sfWithin)
		{
			if (sfWithinIndividual.isNamed())
			{
				String withinURL = sfWithinIndividual.asOWLNamedIndividual().getIRI().getShortForm();
				String type = sfWithinIndividual.getTypes(n3dh.getOntModel()).iterator().next().asOWLClass().getIRI()
						.getShortForm();
				
				// try adding the city reference
				if (type.equalsIgnoreCase("City"))
				{
					district.setCity((SmartCity) this.smartCityEntities.get(withinURL));
					((SmartCity) this.smartCityEntities.get(withinURL)).getDistricts().add(district);
				}
			}
		}
		
		// debug log foun cities
		this.logger.info("Found district: " + district.getUrl() + "[\n in city: " + district.getCity().getUrl()
				+ "\n description:" + description + "\n]");
		
		return district;
	}
	
}
