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
import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.data.DistrictRenderer;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class N3DistrictRenderer extends N3Renderer<District> implements DistrictRenderer
{
	public N3DistrictRenderer(String templatesFolder)
	{
		super(templatesFolder);
		
	}
	
	@Override
	public String render(District district)
	{
		// TODO Auto-generated method stub
		return this.renderDistrictOnly(district);
	}
	
	public String renderDistrictOnly(District district)
	{
		String asN3 = null;
		
		// get the city template
		// TODO: handle if exists conditions in the template
		Template template = this.vtEngine.getTemplate("district.vm");
		
		// check not null
		if (template != null)
		{
			// create the context
			VelocityContext context = new VelocityContext();
			
			// put free parameter data in the velocity context
			context.put("name", district.getName());
			context.put("url", district.getUrl());
			context.put("description", district.getDescription());
			context.put("prefix", this.modelPrefix);
			GeoBoundary boundary = district.getBoundary();
			if (boundary != null)
				context.put("coordinates", boundary.getAsWKT());
			if (district.getCity() != null)
				context.put("city", district.getCity().getUrl());
			
			// a string writer for holding the template merge results
			StringWriter sw = new StringWriter();
			
			// merge the template with the quarter data
			template.merge(context, sw);
			
			// serialize as string
			asN3 = sw.toString();
		}
		
		return asN3;
	}
	
}
