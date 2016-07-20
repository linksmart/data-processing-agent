package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class ModelDeserializer extends JsonDeserializer<Model> {
    @Override
    public Model deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name =  node.get("Name").textValue();
        try {
            return Model.factory(name);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
