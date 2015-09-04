/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel.JsonLDEntry;

import java.util.Map;

/**
 * @author bonino
 *
 */
public class JsonLDDistrictDeserializer extends JsonLDDeserializer<District>
{
	private Map<String, Object> allSmartCityEntities;
	
	public JsonLDDistrictDeserializer(Map<String, Object> allSmartCityEntities)
	{
		// store the currently parsed entities
		this.allSmartCityEntities = allSmartCityEntities;
	}
	
	@Override
	public District deserialize(JsonLDEntry entry)
	{
		// create the district instance
		District district = new District();
		
		district.setName(entry.getName());
		district.setDescription(entry.getDescription());
		district.setUrl(entry.getId());
		String cityId = entry.getWithin()[0]; 
		district.setCity((SmartCity)this.allSmartCityEntities.get(cityId));
		((SmartCity) this.allSmartCityEntities.get(cityId)).getDistricts().add(district);
		
		GeoBoundary districtBoundary = new GeoBoundary();
		districtBoundary.setAsWKT(entry.getGeometry().getAsWkt());
		district.setBoundary(districtBoundary);
		
		return district;
	}
	
}
