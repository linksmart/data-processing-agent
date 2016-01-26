package de.fraunhofer.fit.event.ceml.type.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import de.fraunhofer.fit.event.ceml.CEML;
import de.fraunhofer.fit.event.ceml.LearningHandler;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.Evaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.algorithms.EvaluationAlgorithm;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.impl.DoubleTumbleWindowEvaluator;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.prediction.Prediction;
import eu.linksmart.api.event.datafusion.CEPEngine;
import eu.linksmart.api.event.datafusion.CEPEngineAdvanced;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import weka.classifiers.Classifier;
import weka.core.Instance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 26/11/15.
 */
public class Model implements Serializable {
    private LearningRequest origin =null;
    private String name;
    private Class nativeType;
    private Object lerner;
    @JsonPropertyDescription("Algorithm use to build the model")
    @JsonProperty(value = "Type")
    protected String type;
    @JsonPropertyDescription("Evaluator definition and current evaluation status")
    @JsonProperty(value = "Evaluation")
    private Evaluator evaluation;
    private Configurator conf = Configurator.getDefaultConfig();
    private LoggerService loggerService = Utils.initDefaultLoggerService(LearningRequest.class);
    public Model() {
        super();
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getNativeType() {
        return nativeType;
    }

    public void setNativeType(Class nativeType) {
        this.nativeType = nativeType;
    }

    public Object getLerner() {
        return lerner;
    }

    public void setLerner(Object lerner) {
        this.lerner = lerner;
    }

    private void initialize() throws ClassNotFoundException, IllegalAccessException, InstantiationException {


        lerner = Class.forName(type).newInstance();

        nativeType = lerner.getClass();

        //Hardcode HACK to set defaults, initialization API missing
       /* if(objectType.equals( "weka.classifiers.functions.SGD"))
            ((SGD)learningObjects.get(objectName)).setLossFunction(new SelectedTag(SGD.SQUAREDLOSS, SGD.TAGS_SELECTION));
        */



    }
    public void build(LearningRequest origin) throws Exception {
        this.origin =origin;
        if(evaluation==null)
            evaluation= new DoubleTumbleWindowEvaluator();

        evaluation.build(origin.getData().getAttributes().keySet());

        initialize();
         /*           TODO: do this with reflection
          Method mth = getMethod(
                    lerner.getClass(),
                    "buildClassifier",
                     dataStructure.getInstances()
            );
            if(mth==null)
                return false;
            try {
                mth.invoke(lerner, dataStructure.getInstances());
            } catch (Exception e) {
                loggerService.error(e.getMessage(),e);
                return false;
            }*/

            ((Classifier)lerner).buildClassifier(origin.getData().getInstances());

    }

   /* public String classify(Entry... args) {

        Instance instance = null;
        Map<String, Object> aux = new Hashtable<>();
        for (int i = 0; i < args.length; i++) {
            aux.put(args[i].getName(), args[i].getValue());
            instance = CEML.populateInstance(aux, origin);

        }
        int i = (int) CEML.predict(lerner, instance);
        return origin.getData().getAttributesStructures().get(i).getAttributeName();


    }
    public String classify( Object[] args, String name) {

        Instance instance = null;
        Map<String, Object> aux = new Hashtable<>();

        aux.put(name,args);
        instance = CEML.populateInstance(aux, origin);

        int i = (int) CEML.predict(lerner, instance);
        return origin.getData().getAttributesStructures().get(i).getAttributeName();


    }
    public String classify( Entry entry) {

        Instance instance = null;
        Map<String, Object> aux = new Hashtable<>();

        aux.put(entry.getName(),entry.getValue());
        instance = CEML.populateInstance(aux, origin);

        int i = (int) CEML.predict(lerner, instance);
        return origin.getData().getAttributesStructures().get(i).getAttributeName();


    }*/

    public void reBuild(Model request){
        /*TODO: chnaging configuration of the model */
        evaluation.reBuild(request.evaluation);

    }
    public Prediction evaluate(Instance instance) {
        loggerService.info("Evaluating "+nativeType.getCanonicalName()+ " learner object "+String.valueOf(lerner.hashCode()));
        int i =CEML.predict(lerner,instance);

        return  new Prediction(i,origin.getData().getLearningTarget().value(i),type,new ArrayList<EvaluationAlgorithm>(evaluation.getEvaluationAlgorithms().values()),evaluation.evaluate(i,(int)instance.classValue()));



    }

    public Prediction prediction(Instance instance) {
        int i =CEML.predict(lerner,instance);

        return  new Prediction(i,origin.getData().getLearningTarget().value(i),type,new ArrayList<EvaluationAlgorithm>(evaluation.getEvaluationAlgorithms().values()));



    }
    public String report(){
        return "< Model Type: "+nativeType.getCanonicalName()+" >"+evaluation.report();
    }

}
