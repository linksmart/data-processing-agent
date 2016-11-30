package eu.linksmart.services.event.ceml.models;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created by devasya on 29.11.2016.
 */
public class NNSerialier extends JsonSerializer<MultiLayerNetwork> {
    @Override
    public void serialize(MultiLayerNetwork nnet, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        MultiLayerConfiguration layerwiseConfiguration = nnet.getLayerWiseConfigurations();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("MultiLayerConfiguration",layerwiseConfiguration.toJson());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject( nnet.params());
        byte[] params = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        new ObjectOutputStream(baos).writeObject( nnet.getUpdater());
        byte[] updater = baos.toByteArray();
        jsonGenerator.writeBinaryField("params",params);
        jsonGenerator.writeBinaryField("updater",updater);
    }
}
