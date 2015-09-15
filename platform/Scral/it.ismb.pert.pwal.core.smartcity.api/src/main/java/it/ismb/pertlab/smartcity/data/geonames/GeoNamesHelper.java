/*
 * SmartCityAPI - GeoNames helper
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
package it.ismb.pertlab.smartcity.data.geonames;

import org.geonames.FeatureClass;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import it.ismb.pertlab.smartcity.api.GeoPoint;
import it.ismb.pertlab.smartcity.api.SmartCity;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class GeoNamesHelper
{
	
	/**
	 * 
	 */
	public GeoNamesHelper()
	{
		// TODO Auto-generated constructor stub
	}
	
	public void fillSmartCityData(SmartCity city, String countryCode)
	{
		// if the name is defined, search geonames for the corresponding id
		Toponym cityToponym = this.getToponym(city.getName(), countryCode);
		
		// if not null
		if (cityToponym != null)
		{
			// fill missing data
			if (city.getLocation() == null)
			{
				city.setLocation(new GeoPoint(cityToponym.getLatitude(), cityToponym.getLongitude()));
			}
			
			if (city.getGeoNameId() == 0)
				city.setGeoNameId(cityToponym.getGeoNameId());
			
		}
	}
	
	private Toponym getToponym(String cityName, String countryCode)
	{
		Toponym toponym = null;
		
		try
		{
			WebService.setUserName("kml2city");
			
			ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
			searchCriteria.setName(cityName);
			searchCriteria.setCountryCode(countryCode);
			searchCriteria.setFeatureClass(FeatureClass.P);
			searchCriteria.setFeatureCodes(new String[] { "PPLA", "PPLA2" });
			ToponymSearchResult searchResult;
			
			searchResult = WebService.search(searchCriteria);
			
			// The first is the best.... (very simplistic)
			toponym = searchResult.getToponyms().get(0);
		}
		catch (Exception e)
		{
			//TODO: log the error
			e.printStackTrace();
		}
		
		return toponym;
	}
	
}
