package de.fraunhofer.fit.event.ceml.core;

import de.fraunhofer.fit.event.ceml.intern.Const;
import de.fraunhofer.fit.event.ceml.type.requests.LearningRequest;
import de.fraunhofer.fit.event.ceml.type.requests.LearningStatement;
import de.fraunhofer.fit.event.ceml.type.requests.Model;
import de.fraunhofer.fit.event.ceml.type.requests.evaluation.prediction.Prediction;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import weka.core.Instance;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by angel on 26/11/15.
 */
public class LearningHandler extends Component implements ComplexEventHandler {

    static private Configurator conf = Configurator.getDefaultConfig();
    static private LoggerService loggerService = Utils.initDefaultLoggerService(LearningHandler.class);

    private Statement statement;
    private LearningRequest originalRequest;
    private String columnNameTime = "";
    private int leadingLerner = -1;
    private Map<String, Integer> modelByName = new Hashtable<>();


    public LearningHandler(Statement statement) {
        super(LearningHandler.class.getSimpleName(),"Learning Handler processes the data and input it to the learning objects");
        this.statement = statement;
        this.originalRequest =((LearningStatement)statement).getLearningRequest();
        if(conf.getString(Const.CEML_EngineTimeProveded)!= null ||conf.getString(Const.CEML_EngineTimeProveded)!="" )
            columnNameTime = conf.getString(Const.CEML_EngineTimeProveded);
        for(int i =0; i<originalRequest.getModel().size();i++)
            modelByName.put(originalRequest.getModel().get(i).getType(),i);

    }


    protected void report(String prefix){
        for(int i=0; i< originalRequest.getModel().size();i++)
            loggerService.info(prefix + originalRequest.getModel().get(i).report());


    }
    static protected void learn(LearningRequest originalRequest,Instance instance){
        for(Model m: originalRequest.getModel())
            CEML.learn(m.getLerner(),instance);

    }
    @SuppressWarnings("unchecked")
    @Override
    public void update(Map eventMap) {



        Instance instance = CEML.populateInstance(eventMap,originalRequest);

        Object target =null;
        if(eventMap.containsKey("target"))
            target=eventMap.get("target");
        else if (eventMap.containsKey(originalRequest.getData().getLearningTarget().name()))
            target =eventMap.get(originalRequest.getData().getLearningTarget().name());
        else {
            String elements="";
            for(Object s: eventMap.keySet())
                elements+= s.toString()+", ";
            loggerService.error("No target found in the learning rule, search "+originalRequest.getData().getLearningTarget().name()+" found: "+elements);
            return;
        }

        if(target== null) {
            loggerService.error("target cannot be unknown for learning.");
            return;
        }
        //int prediction = CEML.predict(originalRequest.getModel().getLerner(),instance);
        Prediction prediction = originalRequest.evaluate(instance);

        int itShould =  originalRequest.getData().getLearningTarget().indexOfValue(target.toString());
        loggerService.info("\n(R) Learning with rule: "+ statement.getName() +" with id: "+statement.getHash()+" learning target: "+target.getClass().getSimpleName()+ " predicted index: "+ prediction.getPredictedClass()+" sample index: "+ String.valueOf(itShould));
/*
        Instances instances =originalRequest.getData().getInstances();

       // Weka dose the evaluation in a linear way, this means that the evaluation cost by weka is linear!
     try {
            instances.add(instance);
            evaluation evaluation = new evaluation(instances);
            evaluation.evaluateModel((Classifier) originalRequest.getModel().getLerner(),instances);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       // if(originalRequest.getEvaluation().evaluate(prediction, itShould))
        if(prediction.isAcceptedPrediction())
             originalRequest.deploy();
        else
            originalRequest.undeploy();


        if(conf.getBool(Const.CEML_GenerateReports)) {
            String simulatedTime = "";
            if(eventMap.containsKey(columnNameTime)) {
                Object aux = eventMap.get(columnNameTime);
                if(aux instanceof Date)
                    simulatedTime = " --"+Utils.getIsoTimestamp((Date)aux)+"-- ";
                else if (aux instanceof String)
                    simulatedTime = " --"+((String) aux)+"-- ";
                else if (aux instanceof Long)
                    simulatedTime= " --"+Utils.getIsoTimestamp(new Date((Long)aux))+"-- ";
                else
                    loggerService.warn("Selected column as simulated time has unknown time format");
            }
           report("\n(R) SimulatedTime: " + simulatedTime);

        }
        learn(originalRequest,instance);


    }
    public static void update(Map eventMap, LearningRequest originalRequest) {
        String columnNameTime="";
        try {
            Instance instance = CEML.populateInstance(eventMap,originalRequest);

            Object target =null;
            if(eventMap.containsKey("target"))
                target=eventMap.get("target");
            else if (eventMap.containsKey(originalRequest.getData().getLearningTarget().name()))
                target =eventMap.get(originalRequest.getData().getLearningTarget().name());
            else {
                loggerService.error("No target found in the learning rule");
                return;
            }

            //int prediction = CEML.predict(originalRequest.getModel().getLerner(),instance);
            Prediction prediction = originalRequest.evaluate(instance);
            int itShould =  originalRequest.getData().getLearningTarget().indexOfValue(target.toString());

            // if(originalRequest.getEvaluation().evaluate(prediction, itShould))
            if(prediction.isAcceptedPrediction())
                originalRequest.deploy();
            else
                originalRequest.undeploy();


            if(conf.getBool(Const.CEML_GenerateReports)) {
                String simulatedTime = "";
                if(eventMap.containsKey(columnNameTime)) {
                    Object aux = eventMap.get(columnNameTime);
                    if(aux instanceof Date)
                        simulatedTime = " --"+Utils.getIsoTimestamp((Date)aux)+"-- ";
                    else if (aux instanceof String)
                        simulatedTime = " --"+((String) aux)+"-- ";
                    else if (aux instanceof Long)
                        simulatedTime= " --"+Utils.getIsoTimestamp(new Date((Long)aux))+"-- ";
                    else
                        loggerService.warn("Selected column as simulated time has unknown time format");
                }
                for(int i=0; i< originalRequest.getModel().size();i++)
                    loggerService.info("REPORT:" +simulatedTime+ originalRequest.getModel().get(i).report());

            }
            learn(originalRequest,instance);


        }catch (NullPointerException e){
            loggerService.error("Ignored instance due null values in some data points");
        }
    }



    @Override
    public void destroy() {

    }

}
