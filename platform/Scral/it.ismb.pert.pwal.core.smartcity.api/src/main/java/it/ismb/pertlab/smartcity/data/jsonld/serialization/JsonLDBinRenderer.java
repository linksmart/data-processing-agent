/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.serialization;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import it.ismb.pertlab.smartcity.api.WasteBin;
import it.ismb.pertlab.smartcity.data.WasteBinRenderer;

/**
 * @author bonino
 *
 */
public class JsonLDBinRenderer extends JsonLDRenderer<WasteBin> implements WasteBinRenderer
{
	private Template template;
	
	/**
	 * @param templatesFolder
	 */
	public JsonLDBinRenderer(String templatesFolder)
	{
		super(templatesFolder);
		
		// get the bin template
		this.template = this.vtEngine.getTemplate("bin.vm");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.smartcity.data.jsonld.serialization.JsonLDRenderer#render
	 * (java.lang.Object)
	 */
	@Override
	public String render(WasteBin bin)
	{
		String asJsonLD = null;
		
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
			asJsonLD = sw.toString();
		}
		
		return asJsonLD;
	}
	
}
