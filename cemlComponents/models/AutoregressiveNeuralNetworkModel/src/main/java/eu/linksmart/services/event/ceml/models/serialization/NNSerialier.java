package eu.linksmart.services.event.ceml.models.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.linksmart.services.utils.serialization.SerializerMode;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

/**
 * Created by devasya on 29.11.2016.
 */
public class NNSerialier extends SerializerMode<MultiLayerNetwork> {

    @Override
    public void serialize(MultiLayerNetwork nnet, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        MultiLayerConfiguration layerwiseConfiguration = nnet.getLayerWiseConfigurations();
        jsonGenerator.writeStartObject();
        ObjectMapper mapper = new ObjectMapper();
        serializerProvider.defaultSerializeField("MultiLayerConfiguration",mapper.readValue(layerwiseConfiguration.toJson(), new TypeReference<Map<String, Object>>(){}),jsonGenerator);

        byte[] params = Nd4j.toByteArray(nnet.params());
        jsonGenerator.writeBinaryField("params",params);
        INDArray updaterstate = nnet.getUpdater().getStateViewArray();
        if(updaterstate != null) {
            byte[] updater = Nd4j.toByteArray(updaterstate);
            jsonGenerator.writeBinaryField("updater", updater);
        }
        jsonGenerator.writeEndObject();
    }
}
