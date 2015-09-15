/*
 * SmartCityAPI - core
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
package it.ismb.pertlab.smartcity.api;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * A class representing the geographical boundary of a given feature, be it a
 * city, a district or a boundary. Boundaries are represented as sets of
 * {@link GeoPoint} instances, where the first and the last point shall be equal
 * (closed polygon).
 * 
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class GeoBoundary
{
	
	/**
	 * The set of vertices composing the polygon
	 */
	private ArrayList<GeoPoint> vertices;
	
	/**
	 * Class constructor, creates an empty {@link GeoBoundary} instance.
	 */
	public GeoBoundary()
	{
		// build the inner data-structures
		this.vertices = new ArrayList<GeoPoint>();
	}
	
	/**
	 * Provides all the vertices defining this GeoBoundary instance, as a
	 * polygon.
	 * 
	 * @return the vertices ({@link GeoPoint}s.
	 */
	public ArrayList<GeoPoint> getVertices()
	{
		return vertices;
	}
	
	/**
	 * Sets the vertices defining the polygon represented by this boundary.
	 * 
	 * @param vertices
	 *            the vertices of this boundary objects.
	 */
	public void setVertices(ArrayList<GeoPoint> vertices)
	{
		this.vertices = vertices;
	}
	
	/**
	 * Adds a vertex to this boundary.
	 * 
	 * @param vertex
	 *            The vertex to add as a {@link GeoPoint} instance.
	 */
	public void addVertex(GeoPoint vertex)
	{
		this.vertices.add(vertex);
	}
	
	/**
	 * Get the WKT representation of this {@link GeoBoundary} instance.
	 * 
	 * @return the WKT representation, as {@link String}.
	 */
	public String getAsWKT()
	{
		// the string buffer for building the result
		StringBuffer boundaryAsWKTString = new StringBuffer();
		
		// the first flag
		boolean first = true;
		
		// preamble
		boundaryAsWKTString.append("Polygon((");
		
		// harvest all coordinates
		for (int i = 0; i < vertices.size(); i++)
		{
			if (!first)
				boundaryAsWKTString.append(",");
			else
				first = false;
			GeoPoint currentVertex = vertices.get(i);
			
			boundaryAsWKTString.append(currentVertex.getLongitude());
			boundaryAsWKTString.append(" ");
			boundaryAsWKTString.append(currentVertex.getLatitude());
		}
		
		// closing quotes
		boundaryAsWKTString.append("))");
		
		// return the WKT representation of the given coordinates
		return boundaryAsWKTString.toString();
	}
	
	public void setAsWKT(String wktGeometry)
	{
		//remove ending braces and starting Polygon((
		
		//trim
		String actualGeometry = wktGeometry.trim();
		actualGeometry = actualGeometry.substring(9,actualGeometry.length()-2);
		
		//get points
		StringTokenizer strTok = new StringTokenizer(actualGeometry, ",");
		while(strTok.hasMoreTokens())
		{
			String pointAsString = strTok.nextToken();
			
			//parse the point
			GeoPoint point = new GeoPoint(pointAsString, true);
			
			//if not null
			if(point!=null)
				this.vertices.add(point);
		}
	}
	
	/**
	 * Ray-casting (Ray-crossing) algorithm to check if the given
	 * {@link GeoPoint} is contained inside this {@link GeoBoundary}.
	 * 
	 * @param point
	 *            The {@link GeoPoint} to check.
	 * @return true if the point is inside the boundary, false otherwise.
	 */
	public boolean contains(GeoPoint point)
	{
		// the number of vertices = number of polygon coordinates minus one as
		// the last point is equal to the first.
		int nVertices = vertices.size() - 1;
		
		boolean contains = false;
		
		for (int i = 0, j = nVertices - 1; i < nVertices; j = i++)
		{
			if (((vertices.get(i).getLatitude() > point.getLatitude()) != (vertices.get(j).getLatitude() > point
					.getLatitude()))
					&& (point.getLongitude() < (vertices.get(j).getLongitude() - vertices.get(i).getLongitude())
							* (point.getLatitude() - vertices.get(i).getLatitude())
							/ (vertices.get(j).getLatitude() - vertices.get(i).getLatitude())
							+ vertices.get(i).getLongitude()))
			{
				contains = !contains;
			}
		}
		
		return contains;
		/*
		 * C algorithm as reported in
		 * http://www.ecse.rpi.edu/Homepages/wrf/Research
		 * /Short_Notes/pnpoly.html int pnpoly(int nvert, float *vertx, float
		 * *verty, float testx, float testy) { int i, j, c = 0; for (i = 0, j =
		 * nvert-1; i < nvert; j = i++) { if ( ((verty[i]>testy) !=
		 * (verty[j]>testy)) && (testx < (vertx[j]-vertx[i]) * (testy-verty[i])
		 * / (verty[j]-verty[i]) + vertx[i]) ) c = !c; } return c;
		 */
		
	}
	
	/**
	 * Gets the centroid of this boundary instance as a {@link GeoPoint} instance.
	 * @return the centroid of this boundary.
	 */
	public GeoPoint getCentroid()
	{
		
		// the number of vertices = number of polygon coordinates minus one as
		// the last point is equal to the first.
		int nVertices = vertices.size() - 1;
		
		/* calculate the signed area of the polygon */
		double signedArea = this.getSignedArea();
		
		/* Now calculate the centroid coordinates longitude and latitude */
		
		double longitude = 0.0;
		double latitude = 0.0;
		double t = 0;
		for (int i = 0, j = nVertices - 1; i < nVertices; j = i++)
		{
			t = vertices.get(j).getLongitude() * vertices.get(i).getLatitude() - vertices.get(i).getLongitude()
					* vertices.get(j).getLatitude();
			longitude += (vertices.get(j).getLongitude() + vertices.get(i).getLongitude()) * t;
			latitude += (vertices.get(j).getLatitude() + vertices.get(i).getLatitude()) * t;
		}
		longitude = longitude / (6.0 * signedArea);
		latitude = latitude / (6.0 * signedArea);
		
		return new GeoPoint(latitude, longitude);
	}
	
	/**
	 * Gets the signed area of this boundary.
	 * @return the signed area of this boundary.
	 */
	public double getSignedArea()
	{
		// the number of vertices = number of polygon coordinates minus one as
		// the last point is equal to the first.
		int nVertices = vertices.size() - 1;
		
		/* First calculate the polygon's signed area A */
		double signedArea = 0.0;
		for (int i = 0, j = nVertices - 1; i < nVertices; j = i++)
		{
			signedArea += vertices.get(j).getLongitude() * vertices.get(i).getLatitude()
					- vertices.get(i).getLongitude() * vertices.get(j).getLatitude();
		}
		signedArea *= 0.5;
		
		return signedArea;
	}
}