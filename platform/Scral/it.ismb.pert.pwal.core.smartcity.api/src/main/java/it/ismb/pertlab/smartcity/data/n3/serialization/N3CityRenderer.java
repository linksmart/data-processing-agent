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
package it.ismb.pertlab.smartcity.data.n3.serialization;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.data.CityRenderer;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class N3CityRenderer extends N3Renderer<SmartCity> implements CityRenderer
{
	
	public N3CityRenderer(String templatesFolder)
	{
		super(templatesFolder);
		
		// add the needed renderer
		N3RenderFactory.addRenderer(new N3QuarterRenderer(templatesFolder), Quarter.class);
		N3RenderFactory.addRenderer(new N3DistrictRenderer(templatesFolder), District.class);
	}
	
	@Override
	public String render(SmartCity city)
	{
		StringBuffer asN3 = new StringBuffer();
		
		// get the prefixes
		String prefixes = this.getPrefixesAsN3();
		if ((prefixes != null) && (!prefixes.isEmpty()))
			asN3.append(prefixes);
		asN3.append("\n");
		
		// the ontology name
		asN3.append("<" + this.modelUri + "> rdf:type owl:Ontology ;\n");
		
		// get the imports
		String imports = this.getImportsAsN3();
		if ((imports != null) && (!imports.isEmpty()))
			asN3.append(imports);
		
		// render city only
		asN3.append(this.renderCityOnly(city, this.modelPrefix));
		asN3.append("\n");
		
		// render districts, if any available
		N3DistrictRenderer districtRenderer = (N3DistrictRenderer) N3RenderFactory.getN3Renderer(District.class);
		districtRenderer.setModelPrefix(this.modelPrefix);
		for (District district : city.getDistricts())
		{
			asN3.append(districtRenderer.render(district));
			asN3.append("\n");
		}
		
		// render the quarters
		N3QuarterRenderer quarterRenderer = (N3QuarterRenderer)N3RenderFactory.getN3Renderer(Quarter.class);
		quarterRenderer.setModelPrefix(this.modelPrefix);
		for (Quarter quarter : city.getQuarters())
		{
			asN3.append(quarterRenderer.render(quarter));
			asN3.append("\n");
		}
		
		return asN3.toString();
	}
	
	public String renderCityOnly(SmartCity city, String prefix)
	{
		String asN3 = null;
		// get the city template
		// TODO: handle if exists conditions in the template
		Template template = this.vtEngine.getTemplate("city.vm");
		
		// check not null
		if (template != null)
		{
			// create the context
			VelocityContext context = new VelocityContext();
			
			// put free parameter data in the velocity context
			context.put("name", city.getName());
			context.put("url", city.getUrl());
			context.put("description", "The city of " + city.getName());
			context.put("coordinates", city.getBoundary().getAsWKT());
			context.put("latitude", city.getLocation().getLatitude());
			context.put("longitude", city.getLocation().getLongitude());
			context.put("geoNameId", city.getGeoNameId());
			context.put("prefix", prefix);
			
			StringWriter sw = new StringWriter();
			
			template.merge(context, sw);
			
			asN3 = sw.toString();
		}
		
		return asN3;
	}
}
