package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by José Ángel Carvajal on 19.07.2016 a researcher of Fraunhofer FIT.
 */
public class DataDescriptorsDeserializer extends JsonDeserializer<DataDescriptors> {

    @Override
    public DataDescriptors deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);
        String name = "";
        int input, target, total;

        DataDescriptor.DescriptorTypes type = DataDescriptor.DescriptorTypes.NUMBER;
        if(node.hasNonNull("Name")||node.hasNonNull("InputSize")||node.hasNonNull("TargetSize")||node.hasNonNull("TotalInputSize")){
            if(node.hasNonNull("Name"))
                name =node.get("Name").textValue();
            else
                name = UUID.randomUUID().toString();



            if(node.hasNonNull("TargetSize")){
                target= node.get("TargetSize").asInt();
            }else
                throw new IOException("TargetSize in DataDescriptors is a mandatory property!");

            if(node.hasNonNull("InputSize")){
                input= node.get("InputSize").asInt();
            }else if(node.hasNonNull("TotalInputSize")){
                total = node.get("TotalInputSize").asInt();
                input = total - target;
            }else
                throw new IOException("Either InputSize or TotalInputSize must be defined in DataDescriptors!");

            if(node.hasNonNull("Type"))
                type= DataDescriptor.DescriptorTypes.valueOf(node.get("Type").textValue().toUpperCase());
            else
                throw new IOException("Type in DataDescriptors is a mandatory property!");

            return DataDescriptors.factory(name,input,target,type);

        }


        try {
            if(node.isArray()) {
                DataDescriptor[] descriptors = new DataDescriptorInstance[node.size()];
                for (int i=0; i<node.size();i++)
                    descriptors[i]= (oc.treeToValue(node.get(i), DataDescriptor.class));
                return DataDescriptors.factory(descriptors);
            }
            throw new IOException("Invalid JSON for Data Descriptors!");
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
