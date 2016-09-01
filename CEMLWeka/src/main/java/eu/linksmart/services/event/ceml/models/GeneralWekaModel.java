package eu.linksmart.services.event.ceml.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import eu.linksmart.api.event.ceml.evaluation.TargetRequest;
import eu.linksmart.api.event.ceml.prediction.Prediction;
import eu.linksmart.api.event.ceml.prediction.PredictionInstance;
import eu.linksmart.api.event.datafusion.exceptions.InternalException;
import eu.linksmart.api.event.datafusion.exceptions.TraceableException;
import eu.linksmart.api.event.datafusion.exceptions.UnknownException;
import eu.linksmart.api.event.datafusion.exceptions.UntraceableException;
import eu.linksmart.services.event.ceml.evaluation.evaluators.DoubleTumbleWindowEvaluator;
import eu.linksmart.services.event.ceml.models.data.DataStructure;
import eu.almanac.ogc.sensorthing.api.datamodel.Observation;
import eu.linksmart.api.event.ceml.data.DataDescriptors;
import eu.linksmart.api.event.ceml.model.ModelInstance;
import eu.linksmart.services.utils.configuration.Configurator;
import eu.linksmart.services.utils.function.Utils;
import org.slf4j.Logger;
import org.apache.commons.lang3.math.NumberUtils;
import weka.classifiers.Classifier;
import weka.classifiers.UpdateableClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by angel on 26/11/15.
 */
public class GeneralWekaModel extends ModelInstance<Map,Integer,UpdateableClassifier> {

    @JsonPropertyDescription("Attributes for the model")
    @JsonProperty(value = "Parameters")
    protected String parameters;
    @JsonPropertyDescription("Evaluator definition and current evaluation status")
    @JsonProperty(value = "Evaluation")
   // public Evaluator evaluation;
    private transient Configurator conf = Configurator.getDefaultConfig();
    static transient private Logger loggerService = Utils.initLoggingConf(GeneralWekaModel.class);
    public GeneralWekaModel(ArrayList<TargetRequest> targets,Map<String,Object> parameters) {
        super(targets,parameters,new DoubleTumbleWindowEvaluator(targets));
    }


    private void initialize() throws ClassNotFoundException, IllegalAccessException, InstantiationException {

        lerner = (UpdateableClassifier) Class.forName(type).newInstance();


    }
    public GeneralWekaModel build() throws TraceableException, UntraceableException {

        // TODO: do this with new API
        try {

            initialize();
            descriptors =new DataStructure( descriptors);
            descriptors.build();

            ((Classifier)lerner).buildClassifier(((DataStructure) descriptors).getInstances());

            return this;
        }catch (TraceableException|UntraceableException e){
            throw  e;
        }catch (ClassNotFoundException| IllegalAccessException| InstantiationException e){
            throw new InternalException(this.getName(),this.getClass().getName(),e.getMessage(),e);
        }catch (Exception e){
            throw new UnknownException(this.getName(),this.getClass().getName(),e.getMessage(),e);
        }
    }
    static public Instance createInstance(int n, Instances instances){
        Instance inst = new DenseInstance(n);

        inst.setDataset(instances);
        return inst;
    }
    private static Object getObject(String key, Map map){
        Object value=null;
        if (map.containsKey(key))
            value = map.get(key);
        if(map.containsKey(key.toLowerCase()))
            value = map.get(key.toLowerCase());
        if(map.containsKey(key.toUpperCase()))
            value = map.get(key.toUpperCase());
        if(key.length()>1)
            if(map.containsKey(key.substring(0,1).toUpperCase()+key.substring(1).toLowerCase()))
                value =map.get(key.substring(0,1).toUpperCase()+key.substring(1).toLowerCase());

        return value;

    }
    static public Double getNumeric(Object numeric){
        if(numeric== null)
            loggerService.error("the given learning attribute is null");
        if(numeric instanceof Double)
            return (Double) numeric;
        else if(numeric instanceof Integer)
            return Double.valueOf((Integer)numeric);
        else if(numeric instanceof Float)
            return Double.valueOf((Float)numeric);
        else if(numeric instanceof Long)
            return Double.valueOf((Long)numeric);
        else if(numeric instanceof Short)
            return Double.valueOf((Short)numeric);
        else if(numeric instanceof String && NumberUtils.isNumber((String) numeric))
            return NumberUtils.createDouble((String) numeric);

        loggerService.error("the object "+numeric.toString()+" is not a number");


        return null;


    }
    static public Instance populateInstance(Map<String, Object> events, DataDescriptors descriptors1){
        if(events!=null&&!(descriptors1 instanceof DataStructure))
            return null;
        DataStructure descriptors = (DataStructure) descriptors1;
                    // TODO: check what makes more sense originalRequest.getData().getAttributes().size() or eventMap.size()
        Instance instance = createInstance(descriptors.getAttributesStructures().size(), descriptors.getInstances());

        for(String key: events.keySet()){
            Object aux;
            String toCompare =  key;
            if(key.toLowerCase().equals("target")|| key.equals(descriptors.getLearningTarget().getName()))
                toCompare = descriptors.getLearningTarget().getName();

            if((aux=getObject(toCompare,descriptors.getAttributes()))!=null ){
                Attribute attribute = (Attribute)aux;
                if(attribute.isNumeric()){
                    Double input = getNumeric(events.get(key));
                    instance.setValue(attribute,input);

                }else if(attribute.isDate()&& events.get(key) instanceof Date){
                    String input = Utils.getDateFormat().format(events.get(key));
                    instance.setValue(attribute,input);

                }else {
                    try {

                        String input = events.get(key).toString();
                        instance.setValue(attribute,input);
                    }catch (Exception e){
                        loggerService.error(key);
                        loggerService.error( events.keySet().toString());
                    }
                }

            }else if(events.get(key) instanceof Object[]){
                popularVectorInstance(events.get(key),descriptors,key,instance);
            }


        }
        return instance;
    }
    private static Instance popularVectorInstance(Object vector, DataDescriptors descriptors1, String key, Instance instance){
        if(!(descriptors1 instanceof DataStructure))
            return null;
        DataStructure descriptors = (DataStructure) descriptors1;
        String toCompare;
        if(!(vector instanceof Observation[]) ) {
            Object aux;
            Object[] auxs = (Object[]) vector;
            for (int i = 0; i < auxs.length; i++) {
                toCompare = key + String.valueOf(i);
                if ((aux = getObject(toCompare, descriptors.getAttributes())) != null) {
                    Attribute attribute = (Attribute) aux;
                    if (attribute.isNumeric()) {
                        Double input = getNumeric(auxs[i]);
                        instance.setValue(attribute, input);

                    } else if (attribute.isDate() && auxs[i] instanceof Date) {
                        String input = Utils.getDateFormat().format(auxs[i]);
                        instance.setValue(attribute, input);

                    } else {
                        String input = auxs[i].toString();
                        instance.setValue(attribute, input);
                    }
                }
            }
        }else {
            Object aux;
            Observation[] auxs = (Observation[]) vector;
            for (int i = 0; i < auxs.length; i++) {
                toCompare = key + String.valueOf(i);
                if ((aux = getObject(auxs[i].getId(), descriptors.getAttributes())) != null) {
                    Attribute attribute = (Attribute) aux;
                    if (attribute.isNumeric()) {
                        Double input = getNumeric(auxs[i]);
                        instance.setValue(attribute, input);

                    } else {
                        String input = auxs[i].getResultValue().toString();
                        instance.setValue(attribute, input);
                    }
                }
            }

        }
        return instance;
    }
    public String report(){
        return "\n (R) Model> name:"+name+" Type: "+nativeType.getCanonicalName()+ " learner id: "+System.identityHashCode(lerner);
    }

    @Override
    public boolean learn(Map input) throws Exception {
        loggerService.info("Evaluating "+nativeType.getCanonicalName()+ " learner object "+System.identityHashCode(lerner));

        return learn(lerner, populateInstance(input,descriptors));

    }
    static public int predict(Object lerner,Instance inst){
        try {
            int index =-1;

            loggerService.info("\n(D) classifying with " + lerner.getClass().getCanonicalName() + " id " + System.identityHashCode(lerner));
            index = (int)((Classifier)lerner).classifyInstance(inst);

            return index;
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return -1;
        }
    }
    @Override
    public Prediction<Integer> predict(Map input) throws Exception {

        int i=predict(lerner,populateInstance(input,descriptors));


        return new PredictionInstance<>(i, input,this.getClass().getName(),new ArrayList<>(evaluator.getEvaluationAlgorithms().values()));



    }
    static public boolean learn(Object lerner,Instance inst){
        try {

            loggerService.info("\n(D) learning with "+lerner.getClass().getCanonicalName()+" id "+ System.identityHashCode(lerner));
            ((UpdateableClassifier)lerner).updateClassifier(inst);
        } catch (Exception e) {
            loggerService.error(e.getMessage(),e);
            return false;
        }
        return true;
    }
}
