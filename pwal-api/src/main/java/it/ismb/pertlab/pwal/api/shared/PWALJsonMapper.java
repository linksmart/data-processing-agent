package it.ismb.pertlab.pwal.api.shared;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

/**
 * This class provides utilities to map json file into object and viceversa.
 * 
 * @author GBo
 * 
 */
public class PWALJsonMapper
{
    private static ObjectMapper mapper;

    public PWALJsonMapper()
    {
        AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();
        mapper.getSerializationConfig().withAnnotationIntrospector(
                jacksonIntrospector);
        mapper.getSerializationConfig().withSerializationInclusion(
                Inclusion.NON_NULL);
    }

    public static <T> T json2obj(Class<T> objClass, InputStream is)
            throws JsonParseException, JsonMappingException, IOException
    {
        return getMapper().readValue(is, objClass);
    }

    public static <T> T json2obj(Class<T> objClass, String is)
            throws JsonParseException, JsonMappingException, IOException
    {
        return getMapper().readValue(is, objClass);
    }

    public static <T> String obj2json(T obj) throws JsonGenerationException,
            JsonMappingException, IOException
    {
        return getMapper().writeValueAsString(obj);
    }
    
    public static ObjectMapper getMapper()
    {
        if(mapper == null)
            mapper = new ObjectMapper();
        return mapper;
    }
}
