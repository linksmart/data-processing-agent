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
package it.ismb.pertlab.smartcity.data.kml;

import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.GeoPoint;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class KMLHelper
{
	
	/**
	 * 
	 */
	public KMLHelper()
	{
		// TODO Auto-generated constructor stub
	}
	
	public Collection<Placemark> getAllPlacemarks(String kmlFileAsString)
	{
		// the placemark collection
		Vector<Placemark> allPlacemarks = new Vector<>();
		
		// check if the KML file exists
		File kmlFile = new File(kmlFileAsString);
		
		// check existance
		if (kmlFile.exists())
		{
			// parse the KML file
			Kml kml = Kml.unmarshal(kmlFile, false);
			
			if (kml != null)
			{
				// get the document root
				Feature documentRoot = kml.getFeature();
				
				// handle the root type
				if (documentRoot instanceof Document)
				{
					List<Feature> allFirstLevelFeatures = ((Document) documentRoot).getFeature();
					
					// iterate over features to find folders
					for (Feature currentFirstLevelFeature : allFirstLevelFeatures)
					{
						if (currentFirstLevelFeature instanceof Folder)
						{
							// handle placemarks inside the folder
							List<Feature> allFolderFeatures = ((Folder) currentFirstLevelFeature).getFeature();
							
							// search for placemarks
							for (Feature currentFolderFeature : allFolderFeatures)
							{
								// get placemarks
								if (currentFolderFeature instanceof Placemark)
								{
									allPlacemarks.add((Placemark) currentFolderFeature);
								}
							}
						}
						else if (currentFirstLevelFeature instanceof Placemark)
						{
							allPlacemarks.add((Placemark) currentFirstLevelFeature);
						}
					}
				}
				
			}
			
		}
		
		return allPlacemarks;
	}
	
	public GeoBoundary getKMLPolygonAsGeoBoundary(List<Coordinate> coordinates)
	{
		GeoBoundary boundary = new GeoBoundary();
		
		for (Coordinate coordinate : coordinates)
		{
			boundary.addVertex(new GeoPoint(coordinate.getLatitude(), coordinate.getLongitude(), coordinate
					.getAltitude()));
		}
		
		return boundary;
	}
	
}
