package eu.linksmart.services.event.ceml.models.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.codec.binary.Base64;
import org.deeplearning4j.nn.api.Updater;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.cpu.nativecpu.NDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

/**
 * Created by devasya on 29.11.2016.
 */
public class NNDeserialier extends JsonDeserializer<MultiLayerNetwork> {
    @Override
    public MultiLayerNetwork deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readValue(jsonParser, JsonNode.class);
        byte[] paramsByteArr = node.get("params").binaryValue();
        INDArray params = Nd4j.fromByteArray(paramsByteArr);
        MultiLayerConfiguration MLC = MultiLayerConfiguration.fromJson(node.get("MultiLayerConfiguration").toString());
        MultiLayerNetwork nn = new MultiLayerNetwork(MLC, params);
        if(node.hasNonNull("updater")) {
            byte[] updateByteArr = node.get("updater").binaryValue();
            INDArray updateIndArray = Nd4j.fromByteArray(updateByteArr);
            nn.getUpdater().setStateViewArray(nn,updateIndArray,false);
        }


        return  nn;
    }
}
