/*
 * SmartCityAPI - KML to JSON-LD conversion
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
package it.ismb.pertlab.smartcity.data.jsonld.serialization;

import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;

/**
 * @author bonino
 *
 */
public abstract class JsonLDRenderer<T>
{
	// the velocity engine used to generate the JSON-LD file
	protected VelocityEngine vtEngine;
	
	protected HashMap<String, String> ontologyPrefixes;
	
	protected String modelUri;
	protected String modelPrefix;
	
	public JsonLDRenderer(String templatesFolder)
	{
		// get the templates folder using the class loader
		URL templateFolder = this.getClass().getClassLoader().getResource(templatesFolder);
		
		// prepare the velocity generator to work by using a url-based resource
		// loader
		Properties p = new Properties();
		p.put(RuntimeConstants.RESOURCE_LOADER, "url");
		p.put("url.resource.loader.class", URLResourceLoader.class.getName());
		p.put("url.resource.loader.root", templateFolder.toString());
		p.put(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, NullLogChute.class.getName());
		
		// create the engine
		this.vtEngine = new VelocityEngine(p);
		
		// initialize the engine
		this.vtEngine.init();
		
		// create the prefix map
		this.ontologyPrefixes = new HashMap<String, String>();
		
		// create default model uri
		this.modelUri = "http://www.example.org/ontologies/ontology";
		this.modelPrefix = "example";
	}
	
	public abstract String render(T objectToRender);
	
	// provides the set of prefixes relevant to define th N3 representation of
	// information stored in the KML files.
	public String getPrefixesAsJsonLD()
	{
		StringBuffer prefixes = new StringBuffer();
		
		for (String prefix : this.ontologyPrefixes.keySet())
		{
			prefixes.append("\""+prefix + "\" : \"");
			prefixes.append(this.ontologyPrefixes.get(prefix));
			prefixes.append("\",\n");
		}
		
		return prefixes.toString();
	}
	
	public void addPrefix(String prefix, String iri)
	{
		this.ontologyPrefixes.put(prefix, iri);
	}
	
	/**
	 * @return the ontologyPrefixes
	 */
	public HashMap<String, String> getOntologyPrefixes()
	{
		return ontologyPrefixes;
	}
	
	/**
	 * @return the modelUri
	 */
	public String getModelUri()
	{
		return modelUri;
	}
	
	/**
	 * @param modelUri
	 *            the modelUri to set
	 */
	public void setModelUri(String modelUri)
	{
		this.modelUri = modelUri;
	}
	
	/**
	 * @return the modelPrefix
	 */
	public String getModelPrefix()
	{
		return modelPrefix;
	}
	
	/**
	 * @param modelPrefix
	 *            the modelPrefix to set
	 */
	public void setModelPrefix(String modelPrefix)
	{
		this.modelPrefix = modelPrefix;
	}
}
