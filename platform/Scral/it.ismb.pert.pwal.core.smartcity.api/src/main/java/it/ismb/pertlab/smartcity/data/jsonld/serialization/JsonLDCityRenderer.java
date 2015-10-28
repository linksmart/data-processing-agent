/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.serialization;

import it.ismb.pertlab.smartcity.api.District;
import it.ismb.pertlab.smartcity.api.Quarter;
import it.ismb.pertlab.smartcity.api.SmartCity;
import it.ismb.pertlab.smartcity.data.CityRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

/**
 * @author bonino
 *
 */
public class JsonLDCityRenderer extends JsonLDRenderer<SmartCity> implements CityRenderer
{
	private String simpleNameDefinitions;
	
	/**
	 * @param templatesFolder
	 */
	public JsonLDCityRenderer(String templatesFolder, String simpleNamesDefinitionFileName)
	{
		super(templatesFolder);
		
		// store the simple name definitions
		this.simpleNameDefinitions = this.readSimpleNamesDefinitions(simpleNamesDefinitionFileName);
		
		// add the needed renderer
		JsonLDRendererFactory.addRenderer(new JsonLDQuarterRenderer(templatesFolder), Quarter.class);
		JsonLDRendererFactory.addRenderer(new JsonLDDistrictRenderer(templatesFolder), District.class);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.smartcity.data.CityRenderer#render(it.ismb.pertlab.smartcity
	 * .api.SmartCity)
	 */
	@Override
	public String render(SmartCity city)
	{
		StringBuffer asJsonLD = new StringBuffer();
		
		// append the initial part
		asJsonLD.append("{\n\t\"@context\":{\n");
		
		// get the prefixes
		String prefixes = this.getPrefixesAsJsonLD();
		if ((prefixes != null) && (!prefixes.isEmpty()))
			asJsonLD.append(prefixes);
		
		// append fixed simple names
		asJsonLD.append("\n" + this.simpleNameDefinitions + "\n");
		
		// close the context
		asJsonLD.append("},\n\"@graph\":[\n");
		
		// render city only
		asJsonLD.append(this.renderCityOnly(city, this.modelPrefix));
		asJsonLD.append(",\n");
		
		// render districts, if any available
		JsonLDDistrictRenderer districtRenderer = (JsonLDDistrictRenderer) JsonLDRendererFactory.getJsonLDRenderer(District.class);
		districtRenderer.setModelPrefix(this.modelPrefix);
		for (District district : city.getDistricts())
		{
			asJsonLD.append(districtRenderer.render(district));
			asJsonLD.append(",\n");
		}
		
		// render the quarters
		JsonLDQuarterRenderer quarterRenderer = (JsonLDQuarterRenderer) JsonLDRendererFactory.getJsonLDRenderer(Quarter.class);
		quarterRenderer.setModelPrefix(this.modelPrefix);
		Set<Quarter> quarters = city.getQuarters();
		int i=1;
		for (Quarter quarter : quarters)
		{
			asJsonLD.append(quarterRenderer.render(quarter));
			if(i<quarters.size())
				asJsonLD.append(',');
			asJsonLD.append("\n");
			i++;
		}
		
		//close bracket
		asJsonLD.append("]\n");
		// close the starting brace
		asJsonLD.append("}\n");
		return asJsonLD.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.ismb.pertlab.smartcity.data.CityRenderer#renderCityOnly(it.ismb.pertlab
	 * .smartcity.api.SmartCity, java.lang.String)
	 */
	@Override
	public String renderCityOnly(SmartCity city, String prefix)
	{
		String asJsonLD = null;
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
			
			asJsonLD = sw.toString();
		}
		
		return asJsonLD;
	}
	
	/**
	 * Reads the file containing the simple name definitions
	 * 
	 * @param simpleNamesDefinitionFileName
	 * @return
	 */
	private String readSimpleNamesDefinitions(String simpleNamesDefinitionFileName)
	{
		StringBuffer simpleNamesDefinitionBuffer = new StringBuffer();
		URL simpleNamesDefinitionFile = this.getClass().getClassLoader().getResource(simpleNamesDefinitionFileName);
		FileReader fr;
		try
		{
			fr = new FileReader(new File(simpleNamesDefinitionFile.toURI()));
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			
			while ((line = br.readLine()) != null)
			{
				simpleNamesDefinitionBuffer.append(line);
			}
			
			br.close();
			fr.close();
		}
		catch (IOException | URISyntaxException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return simpleNamesDefinitionBuffer.toString();
	}
}
