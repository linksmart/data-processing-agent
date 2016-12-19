package eu.linksmart.api.event.ceml.model;

import eu.linksmart.api.event.ceml.evaluation.Evaluator;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.exceptions.TraceableException;
import eu.linksmart.api.event.exceptions.UntraceableException;
import eu.linksmart.api.event.types.JsonSerializable;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by José Ángel Carvajal on 18.07.2016 a researcher of Fraunhofer FIT.
 */
public interface Model<Input,Output,LearningObject> extends JsonSerializable{
    final static public Map<String,Class<? extends Model>> loadedModels = new Hashtable<>();

    public static  Model factory(String name, List<TargetRequest> targetRequests, Map<String, Object> parameters, Object learner) throws Exception{
        if(!loadedModels.containsKey(name)) {
            return (Model) Class.forName("eu.linksmart.services.event.ceml.models."+name).getConstructor(List.class,Map.class,Object.class).newInstance(targetRequests,parameters,learner);
        }
        return loadedModels.get(name).getConstructor(List.class,Map.class,Object.class).newInstance(targetRequests,parameters,learner);
        //throw new Exception("No valid models had been loaded");
   }

    public  Evaluator<Output> getEvaluator();

    public void learn(Input input) throws TraceableException, UntraceableException;
    public Prediction<Output> predict(Input input) throws TraceableException, UntraceableException;


    default public void batchLearn(List<Input> input) throws TraceableException, UntraceableException{
        for (Input i : input)
            learn(i);
    }
    default public List<Prediction<Output>> batchPredict(List<Input> input) throws TraceableException, UntraceableException{
        List<Prediction<Output>> predictions = new ArrayList<>();
        for (Input i : input)
            predictions.add(predict(i));

        return predictions;
        }
    public void setDescriptors(DataDescriptors descriptors);
    public DataDescriptors getDescriptors();
    public Prediction<Output> getLastPrediction();
    public void setLastPrediction(Prediction<Output> value);

    String getName();

    public void setName(String name);

    public Class getNativeType();

    public void setNativeType(Class nativeType);


    public Map<String, Object> getParameters() ;
    public void setParameters(Map<String, Object> parameters) ;

    List<TargetRequest> getTargets();

    void setTargets(List<TargetRequest> targets);

    default boolean isClassifier() {return  false;}
    default boolean isRegressor(){return  false;}
    default boolean isClusterer(){return  false;}


}
