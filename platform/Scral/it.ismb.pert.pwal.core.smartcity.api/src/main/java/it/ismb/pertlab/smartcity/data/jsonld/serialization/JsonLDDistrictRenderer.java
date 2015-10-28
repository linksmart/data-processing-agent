/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.serialization;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.data.DistrictRenderer;

/**
 * @author bonino
 *
 */
public class JsonLDDistrictRenderer extends JsonLDRenderer<District> implements DistrictRenderer
{
	
	public JsonLDDistrictRenderer(String templatesFolder)
	{
		super(templatesFolder);
	}

	/* (non-Javadoc)
	 * @see it.ismb.pertlab.smartcity.data.DistrictRenderer#render(it.ismb.pertlab.smartcity.api.District)
	 */
	@Override
	public String render(District district)
	{
		return this.renderDistrictOnly(district);
	}
	
	/* (non-Javadoc)
	 * @see it.ismb.pertlab.smartcity.data.DistrictRenderer#renderDistrictOnly(it.ismb.pertlab.smartcity.api.District)
	 */
	@Override
	public String renderDistrictOnly(District district)
	{
		String asJsonLD = null;
		
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
			asJsonLD = sw.toString();
		}
		
		return asJsonLD;
	}
	
}
