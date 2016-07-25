package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.almanac.event.datafusion.utils.epl.EPLStatement;
import eu.linksmart.api.event.ceml.LearningStatement;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.model.ModelDeserializer;
import eu.linksmart.api.event.datafusion.Statement;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        if(node.hasNonNull("Name")){
            name =node.get("Name").textValue();



            if(node.hasNonNull("TargetSize")){
                target= node.get("TargetSize").asInt();
            }else
                throw new IOException("TargetSize is a mandatory property!");

            if(node.hasNonNull("InputSize")){
                input= node.get("InputSize").asInt();
            }else if(node.hasNonNull("TotalInputSize")){
                total = node.get("TotalInputSize").asInt();
                input = total - target;
            }else
                throw new IOException("Either InputSize or TotalInputSize must be defined!");

            if(node.hasNonNull("Type"))
                type= DataDescriptor.DescriptorTypes.valueOf(node.get("Type").textValue());
            else
                throw new IOException("Type is a mandatory property!");

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
