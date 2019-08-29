package eu.linksmart.services.event.ceml.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.EvaluationMetric;
import eu.linksmart.api.event.ceml.model.Model;
import eu.linksmart.api.event.ceml.model.ModelDeserializer;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UnknownUntraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.models.serialization.NNDeserialier;
import eu.linksmart.services.event.ceml.models.serialization.NNSerialier;
import eu.linksmart.services.event.intern.SharedSettings;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.*;

/**
 * Created by José �?ngel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 * Class implementing Autoregressive neural networks.
 */

// TODO TBD
public class AutoregressiveNeuralNetworkModel extends RegressorModel<List<Double>,List<Double>,List<MultiLayerNetwork>> {

    static {
        Model.loadedModels.put(AutoregressiveNeuralNetworkModel.class.getSimpleName(),AutoregressiveNeuralNetworkModel.class);
        SimpleModule MLNdeserializermodule = new SimpleModule("DNNModel", Version.unknownVersion()).addDeserializer(MultiLayerNetwork.class, new NNDeserialier() );
        SharedSettings.getSerializer().addModule( "SNNModel",MultiLayerNetwork.class, new NNSerialier());
        SharedSettings.getDeserializer().addModule( "DNNModel",MultiLayerNetwork.class, new NNDeserialier());


        ModelDeserializer.setLearnerType(AutoregressiveNeuralNetworkModel.class.getSimpleName(), TypeFactory.defaultInstance().constructCollectionType(List.class, MultiLayerNetwork.class));
        ModelDeserializer.registerModule(MLNdeserializermodule);

    }

    private static final String MAX_INPUT_STR = "maxInputValue";
    private static final String MIN_INPUT_STR = "minInputValue";

    @JsonIgnore
    private int numInputsPerNet;
    @JsonIgnore
    private int numOutputsPerNet = 24;

    @JsonIgnore
    private int ArP;
    @JsonIgnore
    private int ArSeasonalP;

    @JsonIgnore
    private double maxInputValue = Double.POSITIVE_INFINITY;

    @JsonIgnore
    private double minInputValue = Double.NEGATIVE_INFINITY;

    @JsonIgnore
    private int seasonalityPeriod;

    public AutoregressiveNeuralNetworkModel(List<TargetRequest> targets, Map<String, Object> parameters, Object learner) {
        super(targets, parameters, learner);
    }

    // @Override
	/*
	 * Cache for holding future points. his is not exactly future points. But
	 * this is the training data for output. Acting` as the future values to be
	 * predicted for evaluation.
	 */




    /**
     * Returns the network configuration, 2 hidden DenseLayers of size 50.
     */
    private MultiLayerConfiguration getSimpleDenseLayerNetworkConfiguration(
            int numHiddenNodes) {

        double learningRate = 0.01;
        return new NeuralNetConfiguration.Builder()
                .seed((new Random()).nextInt())
                .learningRate(learningRate)
                .momentum(0.9)
                .list()
                .layer(0,
                        new DenseLayer.Builder().nIn(numInputsPerNet)
                                .nOut(numHiddenNodes).activation("tanh")
                                .build())
                .layer(1,
                        new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                                .activation("identity").nIn(numHiddenNodes)
                                .nOut(numOutputsPerNet).build()).pretrain(false)
                .backprop(true).build();
    }




    private List<Double> getPrevSeasonalPoints(
            List<Double> seasonalCache, int netIndex) {
        List<Double> prevSeasonalPoints = new LinkedList<>();

        int counter = 0;
        for (Iterator<Double> iterator = seasonalCache.iterator(); iterator
                .hasNext() && ArSeasonalP*seasonalityPeriod > counter ; ) {


            if (counter % seasonalityPeriod == ((netIndex)* numOutputsPerNet)) {
                // add numOutPut number of points
                for (int i = 0; i < numOutputsPerNet; i++) {
                    Double dataPoint =  iterator.next();
                    prevSeasonalPoints.add(dataPoint);


                }
                counter+=24;
            }else{
                iterator.next();
                counter++;
            }

        }
        return prevSeasonalPoints;
    }


    private INDArray getINDArray(List<Double> inpuList) {
        double[] doubleArr = new double[inpuList.size()];

        int index = 0;
        for (Double dataPoint :inpuList) {
            doubleArr[index++] =  dataPoint;

        }
        return Nd4j.create(doubleArr);
    }

    @Override
    public void learn(List<Double> input)  {
        List<Double> trainOutputCache ;
        // first fill the future buffer
        if (input.size() < (seasonalityPeriod * (ArSeasonalP+1))) {
            //input size is less than expected
            return ;
        } else {
            trainOutputCache = input.subList(seasonalityPeriod * ArSeasonalP,seasonalityPeriod * (ArSeasonalP+1));
        }

        List<Double> seasonalCache = input.subList(0,seasonalityPeriod * ArSeasonalP);
        List<Double> recentPointsCache = input.subList(0, ArP);



        Iterator<Double> outputIterator = trainOutputCache.iterator();

        int netIndex = 0;
        for (MultiLayerNetwork nnet : learner) {

            List<Double> inpuList = new LinkedList<>();
            inpuList.addAll(recentPointsCache);
            inpuList.addAll(getPrevSeasonalPoints(seasonalCache,netIndex++));

            LinkedList<Double> outpuList = new LinkedList<>();
            for (int i = 0; i < numOutputsPerNet; i++) {
                outpuList.add(outputIterator.next());
            }

            INDArray inputNDArray =  getINDArray(inpuList);
            INDArray outputNDArray = getINDArray(outpuList);
            DataSet dataSet = new DataSet(inputNDArray, outputNDArray);

            // iterator.reset();
            nnet.fit(dataSet);
            org.apache.commons.math3.util.Pair<Integer,Integer> test = new org.apache.commons.math3.util.Pair(1,1);

        }


    }

    //This method is just to localize the supperesswarning annotation.
    private double getBoundValue(double value){
        double result =value;
        if(maxInputValue != Double.POSITIVE_INFINITY)
            if(value>maxInputValue)
                result = maxInputValue;
        if(minInputValue != Double.NEGATIVE_INFINITY)
            if(value<minInputValue)
                result = minInputValue;
        return result;
    }
    @Override
    public PredictionInstance<List<Double>> predict(List<Double> input){
        if (input.size()>=descriptors.getInputSize()) {
            List<Double> tempSeasonalCache = input.subList(0, seasonalityPeriod * ArSeasonalP);
            List<Double> tempRecentPointersCache = input.subList(0, ArP);


            List<Double> returnList = new LinkedList<>();
            for (int netindex = 0; netindex < learner.size(); netindex++) {
                MultiLayerNetwork nnet = learner.get(netindex);
                // create the input list
                List<Double> inpuList = new LinkedList<>();
                inpuList.addAll(tempRecentPointersCache);
                inpuList.addAll(getPrevSeasonalPoints(tempSeasonalCache, netindex));

                final INDArray inputArr = getINDArray(inpuList);
                INDArray out = nnet.output(inputArr, false);

                for (int i = 0; i < numOutputsPerNet; i++) {
                    returnList.add(getBoundValue(out.getDouble(i)));
                }

            }
            Collection<EvaluationMetric> evaluationMetrics = new ArrayList<>();
            evaluationMetrics.addAll(evaluator.getEvaluationAlgorithms().values());

            return new PredictionInstance<>(returnList, input, this.getName() + ":" + this.getClass().getSimpleName(), evaluationMetrics);
        }
        return new PredictionInstance<>();
    }




    @Override
    public AutoregressiveNeuralNetworkModel build() throws UntraceableException,TraceableException {
        try {
            int seasonalityPeriod= 168;
            int P = descriptors.getInputSize()/seasonalityPeriod;
            int p = 48;



            ArP = p;
            ArSeasonalP = P;
            numInputsPerNet = p + (P * numOutputsPerNet);
            this.seasonalityPeriod = seasonalityPeriod;
            int numNodes= (numInputsPerNet+numOutputsPerNet)/2;
            int numNnets = descriptors.getTargetSize() / numOutputsPerNet;

            if(parameters.containsKey(MAX_INPUT_STR))
                maxInputValue = (Double) parameters.get(MAX_INPUT_STR);
            if(parameters.containsKey(MIN_INPUT_STR))
                minInputValue = (Double) parameters.get(MIN_INPUT_STR);

            if(learner == null) {
                learner = new ArrayList<>(numNnets);
                for (int i = 0; i < numNnets; i++) {
                    // Switch these two options to do different functions with different
                    // networks

                    final MultiLayerConfiguration conf = getSimpleDenseLayerNetworkConfiguration(numNodes);

                    // Create the network
                    MultiLayerNetwork nnet = new MultiLayerNetwork(conf);
                    nnet.init();

                    learner.add(nnet);
                }
            }
        }catch (Exception e){
            throw new UnknownUntraceableException(e.getMessage(),e);
        }

        super.build();

        return this;
    }

    @Override
    public void destroy() throws Exception {
        // todo there is something to destroy?
    }

/*    public String toJson(){
        String retVal = null;
        List<NNModelSerializer> nnModelSerializers = new ArrayList<>();
        for (MultiLayerNetwork lernerElmnt: learner) {
            nnModelSerializers.add(new NNModelSerializer(lernerElmnt));
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            retVal = mapper.writeValueAsString(nnModelSerializers);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public void fromJson(String jsonString){
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<NNModelSerializer> nnModelSerializers = mapper.readValue(jsonString,new TypeReference<List<NNModelSerializer>>(){});
            learner.clear();
            for (NNModelSerializer nnModelSerializer :
                    nnModelSerializers) {
                learner.add(nnModelSerializer.toMultiLayerNetwork());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/
}