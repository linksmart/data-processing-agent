/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.serialization;

import it.ismb.pertlab.smartcity.api.GeoBoundary;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.WasteBin;
import it.ismb.pertlab.smartcity.data.QuarterRenderer;

import java.io.StringWriter;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * @author bonino
 *
 */
public class JsonLDQuarterRenderer extends JsonLDRenderer<Quarter> implements QuarterRenderer
{
	
	public JsonLDQuarterRenderer(String templatesFolder)
	{
		super(templatesFolder);
		
		// add the needed renderer
		JsonLDRendererFactory.addRenderer(new JsonLDBinRenderer(templatesFolder), WasteBin.class);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.smartcity.data.QuarterRenderer#render(it.ismb.pertlab
	 * .smartcity.api.Quarter)
	 */
	@Override
	public String render(Quarter quarter)
	{
		StringBuffer asJsonLD = new StringBuffer();
		
		// render the quarter
		asJsonLD.append(this.renderQuarterOnly(quarter));
		asJsonLD.append(',');
		
		// iterate over connected bins
		JsonLDBinRenderer binRenderer = (JsonLDBinRenderer) JsonLDRendererFactory.getJsonLDRenderer(WasteBin.class);
		binRenderer.setModelPrefix(this.modelPrefix);
		int i=1;
		Set<WasteBin> wBins = quarter.getBins();
		for (WasteBin bin : wBins)
		{
			asJsonLD.append(binRenderer.render(bin));
			if(i<wBins.size())
				asJsonLD.append(',');
			asJsonLD.append("\n");
			i++;
		}
		
		return asJsonLD.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.smartcity.data.QuarterRenderer#renderQuarterOnly(it.ismb
	 * .pertlab.smartcity.api.Quarter)
	 */
	@Override
	public String renderQuarterOnly(Quarter quarter)
	{
		String asJsonLD = null;
		
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
			asJsonLD = sw.toString();
		}
		
		return asJsonLD;
	}
	
}
