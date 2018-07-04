package eu.linksmart.api.event.ceml.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.services.utils.serialization.DefaultSerializerDeserializer;
import eu.linksmart.services.utils.serialization.DeserializerMode;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class ModelDeserializer extends DeserializerMode<Model> {
    private static final ObjectMapper mapper = (ObjectMapper) new DefaultSerializerDeserializer().getParser();
    private static final CollectionType collectionType =TypeFactory.defaultInstance().constructCollectionType(List.class, TargetRequest.class);
    //private static final CollectionType learnersListType =TypeFactory.defaultInstance().constructCollectionType(List.class,MultiLayerNetwork.class);
    private static final MapType mapType =TypeFactory.defaultInstance().constructMapType(Map.class, String.class,Object.class);
    protected static final Map<String,JavaType> learners = new Hashtable<>();

    static public void registerModule(Module module){
        mapper.registerModule(module);
    }
    static public void setLearnerType(String name, JavaType type){
        learners.put(name,type);
    }

    @Override
    public Model deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);


       String name = loadName(node);
       return constructModel(node,name,loadTargets(node),loadParameters(node),loadLerner(node,name));

       //else
         //   throw new IOException("The field Parameters is a mandatory field!");


    }
    protected String loadName(JsonNode node) throws IOException{
        String name =  node.get("Name").textValue();
        if(!loadClass("eu.linksmart.services.event.ceml.models."+name) && !loadClass(name))
            throw new IOException("Loaded class: "+name+" or "+"eu.linksmart.services.event.ceml.models."+name+ " do not exist!");
        return name;
    }
    protected  List<TargetRequest>  loadTargets(JsonNode node) throws IOException{
        List<TargetRequest> targetRequests;
        if(node.hasNonNull("Targets")) {
            targetRequests = mapper.reader(collectionType).readValue(node.get("Targets"));

        }else
            throw new IOException("The field Targets is a mandatory field!");
        return targetRequests;
    }
    protected Map<String,Object> loadParameters(JsonNode node) throws IOException{
        Map<String,Object> parameters = new Hashtable<>();
        if(node.hasNonNull("Parameters")) {
            parameters = mapper.reader(mapType).readValue(node.get("Parameters"));

        }
        return parameters;
    }
    protected Object loadLerner(JsonNode node, String name) throws IOException{
        Object learner = null;
        if(node.hasNonNull("Learner")) {
            JavaType learnerType = learners.get(name);
            learner = mapper.reader(learnerType).readValue(node.get("Learner"));
        }
        return learner;
    }
    protected Model constructModel(JsonNode node, String name, List<TargetRequest> targetRequests, Map<String,Object> parameters, Object learner)throws IOException {
        try {
            return Model.factory(name,targetRequests,parameters,learner);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
    private boolean loadClass(String className){
        try {
            Class.forName(className);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
