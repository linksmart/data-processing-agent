package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jdk.nashorn.internal.runtime.regexp.joni.ast.StringNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class DataDescriptorDeserializer extends JsonDeserializer<DataDescriptor> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final CollectionType collectionType =
            TypeFactory
                    .defaultInstance()
                    .constructCollectionType(List.class, String.class);
    @Override
    public DataDescriptor deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name =  node.get("Name").textValue();
        DataDescriptor.DescriptorTypes type = DataDescriptor.DescriptorTypes.NUMBER;
        if(node.hasNonNull("Type"))
            type= DataDescriptor.DescriptorTypes.valueOf(node.get("Type").textValue());

        boolean isTarget =  node.hasNonNull("isTarget");
        if(isTarget)
            isTarget =  node.get("isTarget").asBoolean();
        List<String> classes= null;
        if(node.hasNonNull("Classes")) {
            classes = mapper.reader(collectionType).readValue(node.get("Classes"));
            type = DataDescriptor.DescriptorTypes.NOMINAL_CLASSES;
        }

        try {
            return DataDescriptor.factory(type,name,classes,isTarget);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
