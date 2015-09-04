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
import it.ismb.pertlab.smartcity.api.GeoPoint;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.WasteBin;

import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 *
 */
public class N3BinDeserializer extends N3Deserializer<WasteBin>
{
	private Logger logger;
	private String classPackage;
	private Map<String, Object> smartCityEntities;
	
	public N3BinDeserializer(String classPackage, Map<String, Object> allSmartCityEntities)
	{
		// create the logger
		this.logger = LoggerFactory.getLogger(N3BinDeserializer.class);
		this.classPackage = classPackage;
		this.smartCityEntities = allSmartCityEntities;
	}
	
	@Override
	public WasteBin deserialize(OWLNamedIndividual binIndividual, N3DeserializationHelper n3dh)
	{
		// prepare the waste bin instance pointer
		WasteBin bin = null;
		
		// get the IRI of the direct type of the given individual
		IRI individualClass = binIndividual.getTypes(n3dh.getOntModel()).iterator().next().asOWLClass().getIRI();
		
		// get the bin type as a string to be used for generating the right bin
		// instance
		String type = individualClass.getShortForm();
		
		// build the right class
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends WasteBin> theBinClass = (Class<? extends WasteBin>) this.getClass().getClassLoader()
					.loadClass(classPackage + "." + type);
			
			// build a new class instance
			bin = theBinClass.newInstance();
			
			// extract the bin url information (full url)
			bin.setUrl(binIndividual.getIRI().getShortForm());
			
			// extract the bin name
			// TODO: use the gr:Name property
			bin.setName(binIndividual.getIRI().getShortForm());
			
			// intialize longitude and latitude at non existing values
			double latitude = -1.0;
			double longitude = -1.0;
			
			// get all the annotation properties associated with the waste bin
			// individual
			Map<String, Object> annotations = n3dh.getAnnotationValues(binIndividual);
			
			// get the latitude
			latitude = (Double) annotations.get("latitude");
			
			// get the longitude
			longitude = (Double) annotations.get("longitude");
			
			// get the description
			bin.setDescription((String) annotations.get("description"));
			
			// if latitude and longitude are valid set their value
			if ((latitude >= 0.0) && (longitude >= 0.0))
				bin.setLocation(new GeoPoint(latitude, longitude));
			
			// get the object property values of this instance
			Map<String, Set<OWLIndividual>> allPValues = n3dh.getObjectPropertyValues(binIndividual);
			
			// handle containment
			Set<OWLIndividual> sfWithin = allPValues.get("sfWithin");
			
			// debug
			this.logger.debug("Found " + sfWithin.size() + " within clauses...");
			
			// iterate over within clauses
			for (OWLIndividual sfWithinIndividual : sfWithin)
			{
				if (sfWithinIndividual.isNamed())
				{
					String withinURL = sfWithinIndividual.asOWLNamedIndividual().getIRI().getShortForm();
					String withinType = sfWithinIndividual.getTypes(n3dh.getOntModel()).iterator().next().asOWLClass()
							.getIRI().getShortForm();
					
					// try adding the city reference
					if (withinType.equalsIgnoreCase("Quarter"))
					{
						bin.setQuarter((Quarter) this.smartCityEntities.get(withinURL));
						((Quarter) this.smartCityEntities.get(withinURL)).addBin(bin);
					}
					else if(withinType.equalsIgnoreCase("District"))
					{
						bin.setDistrict((District) this.smartCityEntities.get(withinURL));
					}
				}
			}
			
			// debug
			this.logger.info("Found bin " + bin.getName() + "[" + bin.getDescription()
					+ "] at location: " + longitude + " " + latitude);
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
			// log the error
			this.logger.error("Error while de-serializing a WasteBin instance", e);
		}
		return bin;
	}
	
}
