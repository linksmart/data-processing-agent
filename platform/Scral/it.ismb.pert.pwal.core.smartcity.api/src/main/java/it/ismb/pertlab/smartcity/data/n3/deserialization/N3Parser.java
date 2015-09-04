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
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.api.WasteBin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 *
 */
public class N3Parser
{
	N3DeserializationHelper n3dh;
	
	private Logger logger;
	
	private Map<String, Object> entities;
	
	// the city de-serializer
	private N3CityDeserializer cityDeserializer;
	
	// the district deserializer
	private N3DistrictDeserializer districtDeserializer;
	
	// the quarter deserialiser
	private N3QuarterDeserializer quarterDeserializer;
	
	// the bine deserializer
	private N3BinDeserializer binDeserializer;
	
	/**
	 * 
	 */
	public N3Parser()
	{
		// get the class logger
		this.logger = LoggerFactory.getLogger(N3Parser.class);
		
		// create the N3Deserialization helper
		this.n3dh = new N3DeserializationHelper();
		
		// create the entities map
		this.entities = new HashMap<String, Object>();
		
		// create the city de-serializer
		this.cityDeserializer = new N3CityDeserializer();
		
		// create the district deserializer
		this.districtDeserializer = new N3DistrictDeserializer(this.entities);
		
		// create the quarter deserializer
		this.quarterDeserializer = new N3QuarterDeserializer(this.entities);
		
		// create the bin deserializer
		this.binDeserializer = new N3BinDeserializer(WasteBin.class.getPackage().getName(), this.entities);
	}
	
	/**
	 * @return the n3dh
	 */
	public N3DeserializationHelper getN3dh()
	{
		return n3dh;
	}
	
	public void getSmarCity()
	{
		SmartCity city = null;
		
		// get all the individuals
		Set<OWLNamedIndividual> cityIndividuals = this.n3dh.getAllIndividuals("City");
		for (OWLNamedIndividual cityIndividual : cityIndividuals)
		{
			
			city = this.cityDeserializer.deserialize(cityIndividual, this.n3dh);
			// store the city
			this.entities.put(city.getUrl(), city);
			
		}
		
	}
	
	public void getDistricts()
	{
		District district = null;
		Set<OWLNamedIndividual> districtIndividuals = this.n3dh.getAllIndividuals(District.class.getSimpleName());
		for (OWLNamedIndividual districtIndividual : districtIndividuals)
		{
			district = this.districtDeserializer.deserialize(districtIndividual, this.n3dh);
			
			// store the district
			this.entities.put(district.getUrl(), district);
		}
	}
	
	public void getQuarters()
	{
		Quarter quarter = null;
		Set<OWLNamedIndividual> quarterIndividuals = this.n3dh.getAllIndividuals(Quarter.class.getSimpleName());
		for (OWLNamedIndividual quarterIndividual : quarterIndividuals)
		{
			quarter = this.quarterDeserializer.deserialize(quarterIndividual, this.n3dh);
			
			// store the quarter
			this.entities.put(quarter.getUrl(), quarter);
		}
	}
	
	public void getBins()
	{
		WasteBin bin = null;
		int nBins = 0;
		Set<OWLNamedIndividual> allIndividuals = this.n3dh.getAllIndividuals(null);
		for (OWLNamedIndividual individual : allIndividuals)
		{
			// get the indivudual type, laziy, only the first type is considered
			String type = individual.getTypes(this.n3dh.getOntModel()).iterator().next().asOWLClass().getIRI()
					.getShortForm();
			
			// lazy check, TODO: find a way to improve this
			if (type.contains("Bin"))
			{
				// this is a bin individual
				bin = this.binDeserializer.deserialize(individual, this.n3dh);
				
				// store the bin
				this.entities.put(bin.getUrl(), bin);
				
				// increment the bin counter
				nBins++;
			}
			
		}
		
		this.logger.info("Extracted " + nBins + " bins...");
	}
	
	public Set<SmartCity> getCities(String cityModel, String cityModelFile, String cityModelPrefix, String ontologyDir)
	{
		//String ontologyDir = "/home/bonino/Temp/IOT360/ontologies/";
		N3Parser n3d = new N3Parser();
		n3d.getN3dh().addLocalOntology("wbin", "http://www.ismb.it/ontologies/wastebin",
				ontologyDir+"wastebin.n3");
		//n3d.getN3dh().addLocalOntology("altow", "http://www.almanac-project.eu/ontologies/smartcity/turin_waste.owl",
		//		"/home/bonino/Temp/IOT360/turin_waste.n3");
		n3d.getN3dh().addLocalOntology(cityModelPrefix, cityModel, cityModelFile);
		n3d.getN3dh().addLocalOntology("s", "http://schema.org/", ontologyDir + "schemaorg.owl");
		n3d.getN3dh().addLocalOntology("geo", "http://www.opengis.net/ont/geosparql",
				ontologyDir + "geosparql_vocab_all.rdf");
		n3d.getN3dh().addLocalOntology("vcard", "http://www.w3.org/2006/vcard/ns", ontologyDir + "ns.n3");
		n3d.getN3dh().addLocalOntology("places", "http://purl.org/ontology/places", ontologyDir + "places.rdf");
		n3d.getN3dh().addLocalOntology("gr", "http://purl.org/goodrelations/v1", ontologyDir + "goodrelations.rdf");
		n3d.getN3dh().addLocalOntology("muo", "http://purl.oclc.org/NET/muo/muo", ontologyDir + "muo-vocab.owl");
		n3d.getN3dh().loadOntology(cityModel);
		//n3d.getN3dh().loadOntology("http://www.almanac-project.eu/ontologies/smartcity/turin_waste.owl");
		
		n3d.getSmarCity();
		n3d.getDistricts();
		n3d.getQuarters();
		n3d.getBins();
		
		Set<SmartCity> cities = new HashSet<SmartCity>();
		
		for (Object value : n3d.entities.values())
		{
			if (value instanceof SmartCity)
			{
				cities.add((SmartCity)value);
			}
		}
		
		return cities;
	}
	
	/**
	 * Test main
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{

		N3Parser n3d = new N3Parser();
		Set<SmartCity> allCities = n3d.getCities("http://www.almanac-project.eu/ontologies/smartcity/turin_waste.owl", "/home/bonino/Temp/IOT360/turin_waste.n3", "altow", "/home/bonino/Temp/IOT360/ontologies/");
		
		// get the cities
		for (Object value : allCities)
		{
			if (value instanceof SmartCity)
			{
				System.out.println("Smart City: " + ((SmartCity) value).getUrl());
				System.out.println("\tDistricts:");
				for (District district : ((SmartCity) value).getDistricts())
					System.out.println("\t\t" + district.getUrl());
				System.out.println("\tQuarters:");
				for (Quarter quarter : ((SmartCity) value).getQuarters())
				{
					System.out.println("\t\t" + quarter.getUrl() + " in district: " + quarter.getDistrict().getUrl());
					System.out.println("\t\t\t bins:");
					for (WasteBin bin : quarter.getBins())
					{
						System.out.println(bin.getUrl() + "@{" + bin.getLocation().getLongitude() + ","
								+ bin.getLocation().getLatitude() + "} of type: " + bin.getClass().getSimpleName());
					}
				}
			}
		}
		
	}
}
