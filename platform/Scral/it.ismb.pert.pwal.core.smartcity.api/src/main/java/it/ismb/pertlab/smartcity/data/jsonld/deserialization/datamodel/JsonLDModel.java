/**
 * 
 */
package it.ismb.pertlab.smartcity.data.jsonld.deserialization.datamodel;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author bonino
 *
 */
public class JsonLDModel
{
	@JsonProperty("@graph")
	private JsonLDEntry graph[];
	
	@JsonProperty("@context")
	private Map<String,Object> context;
	/**
	 * 
	 */
	public JsonLDModel()
	{
		// TODO Auto-generated constructor stub
		this.context = new HashMap<String, Object>();
	}
	/**
	 * @return the graph
	 */
	public JsonLDEntry[] getGraph()
	{
		return graph;
	}
	/**
	 * @param graph the graph to set
	 */
	public void setGraph(JsonLDEntry[] graph)
	{
		this.graph = graph;
	}
	/**
	 * @return the context
	 */
	public Map<String, Object> getContext()
	{
		return context;
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(Map<String, Object> context)
	{
		this.context = context;
	}
	
	
	
}
