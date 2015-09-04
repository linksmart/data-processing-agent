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

import it.ismb.pertlab.smartcity.api.WasteBin;
import it.ismb.pertlab.smartcity.data.WasteBinRenderer;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 *
 */
public class N3BinRenderer extends N3Renderer<WasteBin> implements WasteBinRenderer
{
	private Template template;
	
	/**
	 * @param templatesFolder
	 */
	public N3BinRenderer(String templatesFolder)
	{
		super(templatesFolder);
		// get the bin template
		this.template = this.vtEngine.getTemplate("bin.vm");
	}
	
	@Override
	public String render(WasteBin bin)
	{
		String asN3 = null;
		
		// check not null
		if (this.template != null)
		{
			// create the context
			VelocityContext context = new VelocityContext();
			
			// put free parameter data in the velocity context
			context.put("name", bin.getName());
			context.put("class", bin.getClass().getSimpleName());
			context.put("url", bin.getUrl());
			context.put("prefix", this.modelPrefix);
			context.put("description", bin.getDescription());
			context.put("address", bin.getAddress());
			context.put("latitude", bin.getLocation().getLatitude());
			context.put("longitude", bin.getLocation().getLongitude());
			context.put("coordinates", bin.getLocation().getAsWKT());
			if (bin.getQuarter() != null)
			{
				context.put("quarter", bin.getQuarter().getUrl());
				context.put("city", bin.getQuarter().getCity().getName());
				context.put("district", bin.getQuarter().getDistrict().getUrl());
			}
			
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
