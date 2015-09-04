/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization;

import java.util.Map;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel.JsonLDEntry;

/**
 * @author bonino
 *
 */
public class JsonLDQuarterDeserializer extends JsonLDDeserializer<Quarter>
{
	
	private Map<String, Object> smartCityEntities;
	
	public JsonLDQuarterDeserializer(Map<String, Object> allSmartCityEntities)
	{
		this.smartCityEntities = allSmartCityEntities;
	}
	
	@Override
	public Quarter deserialize(JsonLDEntry entry)
	{
		Quarter quarter = new Quarter();
		
		quarter.setName(entry.getName());
		quarter.setUrl(entry.getId());
		quarter.setDescription(entry.getDescription());
		
		GeoBoundary quarterBoundary = new GeoBoundary();
		quarterBoundary.setAsWKT(entry.getGeometry().getAsWkt());
		quarter.setBoundary(quarterBoundary);
		
		String within[] = entry.getWithin();
		for (int i = 0; i < within.length; i++)
		{
			Object entity = this.smartCityEntities.get(within[i]);
			
			if (entity instanceof SmartCity)
			{
				quarter.setCity((SmartCity) entity);
				((SmartCity) this.smartCityEntities.get(within[i])).getQuarters().add(quarter);
			}
			else if (entity instanceof District)
			{
				quarter.setDistrict((District) entity);
				((District) this.smartCityEntities.get(within[i])).addQuarter(quarter);
			}
		}
		
		return quarter;
	}
	
}
