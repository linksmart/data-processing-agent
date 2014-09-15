package it.ismb.pertlab.pwal.api.shared;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * This class provides utilities to map json file into object and viceversa.
 * 
 * @author GBo
 * 
 */
public class PWALJsonMapper
{
    private static ObjectMapper mapper = new ObjectMapper();

    public static <T> T json2obj(Class<T> objClass, InputStream is)
            throws JsonParseException, JsonMappingException, IOException
    {
        return mapper.readValue(is, objClass);
    }
    
    public static <T> T json2obj(Class<T> objClass, String is)
            throws JsonParseException, JsonMappingException, IOException
    {
        return mapper.readValue(is, objClass);
    }

    public static <T> String obj2json(T obj) throws JsonGenerationException,
            JsonMappingException, IOException
    {
        return mapper.writeValueAsString(obj);
    }
}
