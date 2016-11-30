package eu.linksmart.services.event.ceml.models;

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
import org.nd4j.linalg.factory.Nd4j;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Created by devasya on 29.11.2016.
 */
public class NNDeserialier extends JsonDeserializer<MultiLayerNetwork> {
    @Override
    public MultiLayerNetwork deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode node = oc.readTree(jsonParser);

        byte[] paramsByteArr = node.get("param").binaryValue();
        ByteArrayInputStream  bis = new ByteArrayInputStream(paramsByteArr);
        MultiLayerNetwork nn = new MultiLayerNetwork(node.get("MultiLayerConfiguration").toString(),Nd4j.read(new DataInputStream(bis)));
        byte[] updateByteArr = node.get("updater").binaryValue();
        bis = new ByteArrayInputStream(updateByteArr);

        try {
            Updater updater = (Updater) new ObjectInputStream(bis).readObject();
            nn.setUpdater(updater);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  nn;
    }
}
