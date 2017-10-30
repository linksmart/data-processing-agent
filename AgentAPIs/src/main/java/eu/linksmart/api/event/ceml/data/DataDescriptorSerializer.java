package eu.linksmart.api.event.ceml.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.linksmart.services.utils.serialization.DeserializerMode;
import eu.linksmart.services.utils.serialization.SerializerMode;

import java.io.IOException;

/**
 * Created by José Ángel Carvajal on 26.07.2016 a researcher of Fraunhofer FIT.
 */
public class DataDescriptorSerializer extends SerializerMode<DataDescriptors> {
    @Override
    public void serialize(DataDescriptors value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        if(value.isLambdaTypeDefinition()){
            gen.writeStartObject();
            gen.writeStringField("Name", value.getName());
            gen.writeNumberField("InputSize",value.getInputSize());
            gen.writeNumberField("TargetSize",value.getTargetSize());
            gen.writeNumberField("TotalInputSize",value.getTotalInputSize());
            gen.writeStringField("Type", value.getType().toString());
            gen.writeStringField("NativeType",value.getNativeType().toString());

            gen.writeEndObject();
        }else {
            gen.writeStartArray();
            for (DataDescriptor d: value)
                serializers.defaultSerializeValue(d,gen);

            gen.writeEndArray();

        }

    }
}
