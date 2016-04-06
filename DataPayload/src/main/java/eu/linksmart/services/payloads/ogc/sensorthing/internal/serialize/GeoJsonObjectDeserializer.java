package eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.geojson.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 05.04.2016 a researcher of Fraunhofer FIT.
 */
public class GeoJsonObjectDeserializer  extends JsonDeserializer<GeoJsonObject> {
    public GeoJsonObjectDeserializer() {
    }

    static ObjectMapper mapper = new ObjectMapper();
    @Override
    public GeoJsonObject deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        Map<String, Object> map = mapper.readValue(jsonParser.getText(), HashMap.class);
        GeoJsonObject geoJsonObject;
        if(map.get("type").equals("Feature")){

            geoJsonObject = new Feature();
            geoJsonObject.setBbox((double[])map.get("coordinates"));

        }else if(map.get("type").equals("Polygon")){
            //*todo*/
            throw  new IOException("Not implemented geoJson");

        }else if(map.get("type").equals("MultiPolygon")){

            //*todo*/
            throw  new IOException("Not implemented geoJson");
        }else if(map.get("type").equals("FeatureCollection")){

            throw  new IOException("Not implemented geoJson");
        }else if(map.get("type").equals("Point")){
            geoJsonObject = new Point();
            geoJsonObject.setBbox((double[])map.get("coordinates"));
        }else if(map.get("type").equals("MultiLineString")){

            //*todo*/
            throw  new IOException("Not implemented geoJson");
        }else if(map.get("type").equals("LineString")){
            //*todo*/
            throw  new IOException("Not implemented geoJson");
        }else {
            throw  new IOException("Unknown geoJson");
        }

        return geoJsonObject;
    }
}
