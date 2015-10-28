/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.api.WasteBin;
import it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel.JsonLDEntry;
import it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel.JsonLDModel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author bonino
 *
 */
public class JsonLDParser
{
	private Logger logger;
	
	private Map<String, Object> entities;
	private Map<String, HashSet<JsonLDEntry>> entriesByType;
	
	// the de-serializers
	private JsonLDCityDeserializer cityDeserializer;
	private JsonLDDistrictDeserializer districtDeserializer;
	private JsonLDQuarterDeserializer quarterDeserializer;
	private JsonLDBinDeserializer binDeserializer;
	
	private ObjectMapper mapper;
	
	/**
	 * 
	 */
	public JsonLDParser()
	{
		// get the class logger
		this.logger = LoggerFactory.getLogger(JsonLDParser.class);
		
		// create the entities map
		this.entities = new HashMap<String, Object>();
		
		// create the entries by type catalog
		this.entriesByType = new HashMap<String, HashSet<JsonLDEntry>>();
		
		// create the deserializers
		this.cityDeserializer = new JsonLDCityDeserializer();
		
		// create the district deserializer
		this.districtDeserializer = new JsonLDDistrictDeserializer(this.entities);
		
		// create the quarter deserializer
		this.quarterDeserializer = new JsonLDQuarterDeserializer(this.entities);
		
		// create the bin deserializer
		this.binDeserializer = new JsonLDBinDeserializer(WasteBin.class.getPackage().getName(), this.entities);
		
		// create the JSON mapper
		this.mapper = new ObjectMapper();
	}
	
	public Set<SmartCity> getCities(String jsonFileName)
	{
		Set<SmartCity> cities = new HashSet<SmartCity>();
		try
		{
			JsonLDModel userData = mapper.readValue(new File(jsonFileName), JsonLDModel.class);
			
			// get the graph entry
			JsonLDEntry entries[] = userData.getGraph();
			
			// fill the by type catalog
			for (int i = 0; i < entries.length; i++)
			{
				String type = entries[i].getType();
				if (!this.entriesByType.containsKey(type))
					this.entriesByType.put(type, new HashSet<JsonLDEntry>());
				
				this.entriesByType.get(type).add(entries[i]);
			}
			
			// deserialize in order: city, district, quarters, bins
			
			// --- CITY ---
			for (JsonLDEntry city : this.entriesByType.get("wbin:City"))
			{
				// deserialize city
				SmartCity currentCity = cityDeserializer.deserialize(city);
				this.entities.put(currentCity.getUrl(), currentCity);
				
				// debug
				this.logger.info("Found city: " + currentCity.getName() + " @ ("
						+ currentCity.getLocation().getLatitude() + "," + currentCity.getLocation().getLongitude()
						+ ")");
				
				// store the city
				cities.add(currentCity);
			}
			
			// --- DISTRICTS ---
			for (JsonLDEntry district : this.entriesByType.get("wbin:District"))
			{
				District currentDistrict = this.districtDeserializer.deserialize(district);
				this.entities.put(currentDistrict.getUrl(), currentDistrict);
				
				// debug
				this.logger.info("Found district: " + currentDistrict.getName() + "["
						+ currentDistrict.getCity().getName() + "]");
				
			}
			
			// --- QUARTERS ---
			for (JsonLDEntry quarter : this.entriesByType.get("wbin:Quarter"))
			{
				Quarter currentQuarter = this.quarterDeserializer.deserialize(quarter);
				this.entities.put(currentQuarter.getUrl(), currentQuarter);
				
				// debug
				this.logger.info("Found quarter: " + currentQuarter.getName() + "["
						+ currentQuarter.getDistrict().getName() + "]");
				
			}
			
			// --- BINS ---
			for (String type : this.entriesByType.keySet())
			{
				// Not really nice, used to address opaque handling of JSON-LD
				if (type.contains("Bin"))
				{
					for (JsonLDEntry bin : this.entriesByType.get(type))
					{
						WasteBin currentBin = this.binDeserializer.deserialize(bin);
						this.entities.put(currentBin.getUrl(), currentBin);
						
						// debug
						this.logger.info("Found " + currentBin.getClass().getSimpleName() + ": " + currentBin.getName()
								+ "@(" + currentBin.getLocation().getLatitude() + ","
								+ currentBin.getLocation().getLongitude() + ")[" + currentBin.getQuarter().getName()
								+ "]");
						
					}
				}
			}
			
			this.logger.info("Extracted: " + entries.length);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cities;
	}
	
	public static void main(String args[])
	{
		JsonLDParser parser = new JsonLDParser();
		parser.getCities("/home/bonino/Temp/IOT360/turin_waste.jsonld");
	}
}
