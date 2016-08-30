package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.linksmart.api.event.ceml.data.DataDescriptor;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class ModelDeserializer extends JsonDeserializer<Model> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final CollectionType collectionType =TypeFactory.defaultInstance().constructCollectionType(List.class, TargetRequest.class);
    private static final MapType mapType =TypeFactory.defaultInstance().constructMapType(Map.class, String.class,Object.class);
    @Override
    public Model deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String name =  node.get("Name").textValue();

        List<TargetRequest> targetRequests;
        Map<String,Object> parameters = new Hashtable<>();
        if(node.hasNonNull("Targets")) {
            targetRequests = mapper.reader(collectionType).readValue(node.get("Targets"));

        }else
            throw new IOException("The field Targets is a mandatory field!");
        if(node.hasNonNull("Parameters")) {
            parameters = mapper.reader(mapType).readValue(node.get("Parameters"));

        }//else
         //   throw new IOException("The field Parameters is a mandatory field!");
        try {
            return Model.factory(name,targetRequests,parameters);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
