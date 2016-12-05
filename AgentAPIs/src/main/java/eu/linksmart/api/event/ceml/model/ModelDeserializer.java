package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class ModelDeserializer extends JsonDeserializer<Model> {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final CollectionType collectionType =TypeFactory.defaultInstance().constructCollectionType(List.class, TargetRequest.class);
    //private static final CollectionType learnersListType =TypeFactory.defaultInstance().constructCollectionType(List.class,MultiLayerNetwork.class);
    private static final MapType mapType =TypeFactory.defaultInstance().constructMapType(Map.class, String.class,Object.class);
    protected static final Map<String,JavaType> learners = new Hashtable<>();

    static public void registermodule(Module module){
        mapper.registerModule(module);
    }
    static public void setLearnerType(String name, JavaType type){
        learners.put(name,type);
    }

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
        Object learner = null;
        if(node.hasNonNull("Learner")) {
            //CollectionType learnersListType =TypeFactory.defaultInstance().constructCollectionType(List.class,MultiLayerNetwork.class);

         //   try {
                //Class clazz = Class.forName("eu.linksmart.services.event.ceml.models."+name);
                JavaType learnerType = learners.get(name);
                //JavaType type = TypeFactory.defaultInstance().constructType(learnerType);
                learner = mapper.reader(learnerType).readValue(node.get("Learner"));
           // } catch (ClassNotFoundException e) {
             //   e.printStackTrace();
            //} catch (NoSuchMethodException e) {
              //  e.printStackTrace();
            //}

        }
        try {
            return Model.factory(name,targetRequests,parameters,learner);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
