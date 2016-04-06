package eu.linksmart.services.payloads.ogc.sensorthing.internal.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import eu.linksmart.services.payloads.ogc.sensorthing.internal.Interval;
import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.geojson.Point;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 05.04.2016 a researcher of Fraunhofer FIT.
 */
public class GeoJsonObjectSerializer extends JsonSerializer<GeoJsonObject> {

    static ObjectMapper mapper = new ObjectMapper();
    public GeoJsonObjectSerializer() {
        super();
    }

    @Override
    public void serialize(GeoJsonObject geoJsonObject, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("type",geoJsonObject.getClass().getSimpleName());
        map.put("coordinates",mapper.writeValueAsString(geoJsonObject.getBbox()));
        jsonGenerator.writeString( mapper.writeValueAsString(map));
    }
}
