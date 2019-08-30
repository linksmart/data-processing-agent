package eu.linksmart.services.event.ceml.evaluation.evaluators;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.evaluation.metrics.ModelEvaluationMetric;
import eu.linksmart.api.event.ceml.model.EvaluatorDeserializer;
import eu.linksmart.services.event.ceml.evaluation.metrics.InitialSamples;
import eu.linksmart.services.event.ceml.evaluation.metrics.base.EvaluationMetricBase;
import eu.linksmart.services.utils.serialization.DeserializerMode;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 27.02.2019 a researcher of Fraunhofer FIT.
 */
public class EvaluatorBaseDeserializer extends EvaluatorDeserializer {

    protected static EvaluatorBase process(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        ObjectCodec oc = p.getCodec();
        JsonNode node = oc.readTree(p);

        return process(mapper,node);
    }
    protected static EvaluatorBase process(ObjectMapper mapper, JsonNode node) throws IOException, JsonProcessingException {

        if(node.hasNonNull("type")){
            try {
                Class<EvaluatorBase> evaluatorClass = ( Class<EvaluatorBase>) Class.forName(node.get("type").asText());
                Constructor<EvaluatorBase> constructor = evaluatorClass.getConstructor();
                final EvaluatorBase evaluator = constructor.newInstance();

                if(node.hasNonNull("targets") )
                    evaluator.setTargets(mapper.readValue(node.get("targets").toString(),new TypeReference<List<TargetRequest>>(){}));

                if(node.hasNonNull("parameters") )
                    evaluator.setParameters(mapper.readValue(node.get("parameters").toString(),new TypeReference<Map<String, Object>>(){}));

                if(evaluator instanceof WindowEvaluator){
                    WindowEvaluator windowEvaluator = (WindowEvaluator)evaluator;
                    if(node.hasNonNull("classes") )
                        windowEvaluator.setClasses(mapper.readValue(node.get("classes").toString(),List.class));

                    if(node.hasNonNull("sequentialConfusionMatrix") )
                        windowEvaluator.setSequentialConfusionMatrix(mapper.readValue(node.get("sequentialConfusionMatrix").toString(),long[][].class));

                    if(node.hasNonNull("initialConfusionMatrix") )
                        windowEvaluator.setInitialConfusionMatrix(mapper.readValue(node.get("initialConfusionMatrix").toString(),long[][].class));

                    if(node.hasNonNull("confusionMatrix") )
                        windowEvaluator.setConfusionMatrix(mapper.readValue(node.get("confusionMatrix").toString(),double[][].class));


                } else if( evaluator instanceof RegressionEvaluator){
                    RegressionEvaluator regressionEvaluator = (RegressionEvaluator)evaluator;
                    if(node.hasNonNull("fixedSizeList") )
                        regressionEvaluator.setFixedSizeList(mapper.readValue(node.get("fixedSizeList").toString(),new TypeReference<LinkedList<Map.Entry<Number, Number>>>(){}));

                    if(node.hasNonNull("latestEntries") )
                        regressionEvaluator.setLatestEntries(mapper.readValue(node.get("latestEntries").toString(),new TypeReference<List<Map.Entry<Number, Number>>>(){}));

                    if(node.hasNonNull("maxQueueSize") )
                        regressionEvaluator.setMaxQueueSize(mapper.readValue(node.get("maxQueueSize").toString(),int.class));
                } else if( evaluator instanceof DoubleTumbleWindowEvaluator){

                    DoubleTumbleWindowEvaluator doubleTumbleWindowEvaluator = (DoubleTumbleWindowEvaluator)evaluator;
                    if(node.hasNonNull("classes") )
                        doubleTumbleWindowEvaluator.setClasses(mapper.readValue(node.get("classes").toString(),List.class));

                    if(node.hasNonNull("windowEvaluators") ) {
                        WindowEvaluator[] ww = new WindowEvaluator[2];
                        ww[0] = (WindowEvaluator) process(mapper,node.get("windowEvaluators").get(0));
                        ww[1] = (WindowEvaluator) process(mapper,node.get("windowEvaluators").get(1));
                        doubleTumbleWindowEvaluator.windowEvaluators = ww;
                        //doubleTumbleWindowEvaluator.windowEvaluators = mapper.readValue(node.get("windowEvaluators").toString(), WindowEvaluator[].class);

                    }if(node.hasNonNull("learning") )
                        doubleTumbleWindowEvaluator.learning  = mapper.readValue(node.get("learning").toString(),int.class);

                    if(node.hasNonNull("learnt") )
                        doubleTumbleWindowEvaluator.learnt  = mapper.readValue(node.get("learnt").toString(),int.class);

                    if(node.hasNonNull("initialSamples") )
                        doubleTumbleWindowEvaluator.initialSamples  = mapper.readValue(node.get("initialSamples").toString(),InitialSamples.class);


                    if(node.hasNonNull("initialSamplesMatrix") )
                        doubleTumbleWindowEvaluator.initialSamplesMatrix  = mapper.readValue(node.get("initialSamplesMatrix").toString(),long[][].class);

                }
                if(evaluator instanceof GenericEvaluator) {
                    final GenericEvaluator genericEvaluator = (GenericEvaluator) evaluator;
                    if (node.hasNonNull("evaluationAlgorithms")) {
                        genericEvaluator.evaluationAlgorithms = new Hashtable();
                        node.get("evaluationAlgorithms").fieldNames().forEachRemaining(i -> genericEvaluator.evaluationAlgorithms.put(i, genericEvaluator.instanceEvaluationAlgorithm(i)));
                        ((Map<String, EvaluationMetricBase>) genericEvaluator.evaluationAlgorithms).forEach((k, v) -> {
                            if (node.get("evaluationAlgorithms").hasNonNull(k)) {
                                v.setCurrentValue(node.get("evaluationAlgorithms").get(k).get("currentValue"));
                                v.setCurrentValue(node.get("evaluationAlgorithms").get(k).get("target"));

                            }
                        });
                    }
                }

                return evaluator;

            } catch (Exception e) {
                throw new IOException(e);
            }
        }else
            throw new IOException("Evaluator has no type");
    }
    @Override
    public Evaluator deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return process(p,ctxt);
    }

    @Override
    public Evaluator deserialize(ObjectMapper mapper, JsonNode node) throws IOException, JsonProcessingException {
        return process(mapper,node);
    }
}
