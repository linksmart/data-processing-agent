/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.GeoPoint;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.WasteBin;
import it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel.JsonLDEntry;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 *
 */
public class JsonLDBinDeserializer extends JsonLDDeserializer<WasteBin>
{
	private Logger logger;
	private String classPackage;
	private Map<String, Object> smartCityEntities;
	
	public JsonLDBinDeserializer(String classPackage, Map<String, Object> allSmartCityEntities)
	{
		// create the logger
		this.logger = LoggerFactory.getLogger(JsonLDBinDeserializer.class);
		this.classPackage = classPackage;
		this.smartCityEntities = allSmartCityEntities;
	}
	
	@Override
	public WasteBin deserialize(JsonLDEntry entry)
	{
		// prepare the waste bin instance pointer
		WasteBin bin = null;
		
		String fullType[] = entry.getType().split(":");
		if ((fullType != null) && (fullType.length > 1))
		{
			// build the right class
			try
			{
				@SuppressWarnings("unchecked")
				Class<? extends WasteBin> theBinClass = (Class<? extends WasteBin>) this.getClass().getClassLoader()
						.loadClass(classPackage + "." + fullType[1]);
				
				// build a new class instance
				bin = theBinClass.newInstance();
				
				bin.setName(entry.getName());
				bin.setUrl(entry.getId());
				bin.setAddress(entry.getAddress().getStreetAddress()+", "+entry.getAddress().getLocality());
				bin.setLocation(new GeoPoint(entry.getLatitude(), entry.getLongitude()));
				bin.setDescription(entry.getDescription());
				
				String within[] = entry.getWithin();
				for(int i=0; i<within.length; i++)
				{
					Object entity = this.smartCityEntities.get(within[i]);
					
					if(entity instanceof Quarter)
					{
						bin.setQuarter((Quarter)entity);
						((Quarter) this.smartCityEntities.get(within[i])).getBins().add(bin);
					}
					else if(entity instanceof District)
						bin.setDistrict((District)entity);
				}
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException e)
			{
				// log the error
				this.logger.error("Error while de-serializing a WasteBin instance", e);
			}
		}
		return bin;
	}
	
}
