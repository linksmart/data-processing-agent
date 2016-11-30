package eu.linksmart.services.event.ceml.models;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.deeplearning4j.nn.api.Updater;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import org.nd4j.linalg.factory.Nd4j;

import java.io.*;

/**
 * Created by devasya on 28.11.2016.
 */
public class NNModelSerializer {
    @JsonSerialize
    public MultiLayerConfiguration layerwiseConfiguration;
    @JsonProperty
    public byte[] params;
    public byte[] updater;
    NNModelSerializer(MultiLayerNetwork nnet){
        layerwiseConfiguration = nnet.getLayerWiseConfigurations();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            new ObjectOutputStream(baos).writeObject( nnet.params());
            params = baos.toByteArray();
            baos = new ByteArrayOutputStream();
            new ObjectOutputStream(baos).writeObject( nnet.getUpdater());
            updater = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    MultiLayerNetwork toMultiLayerNetwork(){
        MultiLayerNetwork nn = new MultiLayerNetwork(layerwiseConfiguration);
        if(updater!=null){
            ByteArrayInputStream bis = new ByteArrayInputStream(updater);
            try {
                bis = new ByteArrayInputStream(params);
                nn.init(Nd4j.read(new DataInputStream(bis)),false);
                Updater updater = (Updater) new ObjectInputStream(bis).readObject();

                nn.setUpdater(updater);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return  nn;
    }
}
