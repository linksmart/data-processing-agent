package it.ismb.pertlab.pwal.api.shared;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;


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
    {    }

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
        {
            mapper = new ObjectMapper();
            AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();
            mapper.getSerializationConfig().with(
                    jacksonIntrospector);
            mapper.setSerializationInclusion(Include.NON_EMPTY);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            mapper.setDateFormat(df);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
        return mapper;
    }
}
