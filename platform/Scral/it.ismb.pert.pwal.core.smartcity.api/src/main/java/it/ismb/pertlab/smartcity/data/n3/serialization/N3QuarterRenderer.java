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

import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.WasteBin;
import it.ismb.pertlab.smartcity.data.QuarterRenderer;

/**
 * @author bonino <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class N3QuarterRenderer extends N3Renderer<Quarter> implements QuarterRenderer
{
	
	public N3QuarterRenderer(String templatesFolder)
	{
		super(templatesFolder);
		
		// add the needed renderer
		N3RenderFactory.addRenderer(new N3BinRenderer(templatesFolder), WasteBin.class);
	}
	
	@Override
	public String render(Quarter quarter)
	{
		StringBuffer asN3 = new StringBuffer();
		
		// render the quarter
		asN3.append(this.renderQuarterOnly(quarter));
		
		// iterate over connected bins
		N3BinRenderer binRenderer = (N3BinRenderer) N3RenderFactory.getN3Renderer(WasteBin.class);
		binRenderer.setModelPrefix(this.modelPrefix);
		for (WasteBin bin : quarter.getBins())
		{
			asN3.append(binRenderer.render(bin));
			asN3.append("\n");
		}
		
		return asN3.toString();
	}
	
	public String renderQuarterOnly(Quarter quarter)
	{
		String asN3 = null;
		
		// get the city template
		// TODO: handle if exists conditions in the template
		Template template = this.vtEngine.getTemplate("quarter.vm");
		
		// check not null
		if (template != null)
		{
			// create the context
			VelocityContext context = new VelocityContext();
			
			// put free parameter data in the velocity context
			context.put("name", quarter.getName());
			context.put("url", quarter.getUrl());
			context.put("description", quarter.getDescription());
			context.put("prefix", this.modelPrefix);
			GeoBoundary boundary = quarter.getBoundary();
			if (boundary != null)
				context.put("coordinates", boundary.getAsWKT());
			if (quarter.getCity() != null)
				context.put("city", quarter.getCity().getUrl());
			if (quarter.getDistrict() != null)
				context.put("district", quarter.getDistrict().getUrl());
			
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
