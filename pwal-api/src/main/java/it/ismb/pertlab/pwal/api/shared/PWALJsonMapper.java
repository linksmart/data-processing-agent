package it.ismb.pertlab.pwal.api.shared;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class PWALJsonMapper {
	private ObjectMapper mapper;

	public PWALJsonMapper() {
		this.mapper = new ObjectMapper();
	}
	
	public <T> T json2obj(Class<T> objClass, InputStream is) throws JsonParseException, JsonMappingException, IOException
	{
		return this.mapper.readValue(is, objClass);
	}
	
	public <T> String obj2json (T obj) throws JsonGenerationException, JsonMappingException, IOException
	{
		return this.mapper.writeValueAsString(obj);
	}
}
