package de.fraunhofer.fit.event.ceml;

import de.fraunhofer.fit.event.ceml.intern.Const;
import de.fraunhofer.fit.event.ceml.type.requests.LearningRequest;
import de.fraunhofer.fit.event.ceml.type.requests.LearningStatement;
import eu.almanac.event.datafusion.utils.generic.Component;
import eu.linksmart.api.event.datafusion.ComplexEventHandler;
import eu.linksmart.api.event.datafusion.Statement;
import eu.linksmart.gc.utils.configuration.Configurator;
import eu.linksmart.gc.utils.function.Utils;
import eu.linksmart.gc.utils.logging.LoggerService;
import weka.core.Instance;

import java.util.Date;
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


    public LearningHandler(Statement statement) {
        super(LearningHandler.class.getSimpleName(),"Learning Handler processes the data and input it to the learning objects");
        this.statement = statement;
        this.originalRequest =((LearningStatement)statement).getLearningRequest();
        if(conf.getString(Const.EngineTimeProveded)!= null ||conf.getString(Const.EngineTimeProveded)!="" )
            columnNameTime = conf.getString(Const.EngineTimeProveded);

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
            loggerService.error("No target found in the learning rule");
            return;
        }
        int prediction = CEML.predict(originalRequest.getModel().getLerner(),instance);
        int itShould =  originalRequest.getData().getLearningTarget().indexOfValue(target.toString());
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
        if(originalRequest.getEvaluation().evaluate(prediction, itShould))
             originalRequest.deploy();
        else
            originalRequest.undeploy();
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

        if(conf.getBool(Const.GenerateReports))
            loggerService.info(simulatedTime+originalRequest.getEvaluation().report());

        CEML.learn(originalRequest.getModel().getLerner(),instance);


    }
    public static void update(Map eventMap, LearningRequest originalRequest) {

        try {
            Instance instance = CEML.populateInstance(eventMap, originalRequest);

            Object target = null;
            if (eventMap.containsKey("target"))
                target = eventMap.get("target");
            else if (eventMap.containsKey(originalRequest.getData().getLearningTarget()))
                target = eventMap.get(originalRequest.getData().getLearningTarget());
            else {
                loggerService.error("No target found in the learning rule");
                return;
            }
            int prediction = CEML.predict(originalRequest.getModel().getLerner(), instance);
            int itShould = originalRequest.getData().getLearningTarget().indexOfValue(target.toString());
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
            if (originalRequest.getEvaluation().evaluate(prediction, itShould))
                originalRequest.deploy();
            else
                originalRequest.undeploy();

            CEML.learn(originalRequest.getModel().getLerner(), instance);

        }catch (NullPointerException e){
            loggerService.error("Ignored instance due null values in some data points");
        }
    }
    public static int classify(Map eventMap, LearningRequest originalRequest) {

        Instance instance = CEML.populateInstance(eventMap,originalRequest);

        return CEML.predict(originalRequest.getModel().getLerner(),instance);


    }



    @Override
    public void destroy() {

    }

}
