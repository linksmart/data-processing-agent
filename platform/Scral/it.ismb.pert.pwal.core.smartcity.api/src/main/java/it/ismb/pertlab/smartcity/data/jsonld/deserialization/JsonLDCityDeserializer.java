/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization;

import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.GeoPoint;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel.JsonLDEntry;

/**
 * @author bonino
 *
 */
public class JsonLDCityDeserializer extends JsonLDDeserializer<SmartCity>
{
	
	@Override
	public SmartCity deserialize(JsonLDEntry entry)
	{
		// create the city instance
		SmartCity city = new SmartCity();
		
		city.setUrl(entry.getId());
		city.setName(entry.getName());
		if (entry.getGeonamesid() != null)
		{
			String geonames[] = entry.getGeonamesid().split(":");
			city.setGeoNameId(Integer.valueOf(geonames[1]));
		}
		city.setLocation(new GeoPoint(entry.getLatitude(), entry.getLongitude()));
		GeoBoundary cityBoundary = new GeoBoundary();
		cityBoundary.setAsWKT(entry.getGeometry().getAsWkt());
		city.setBoundary(cityBoundary);
		
		return city;
	}
	
}
